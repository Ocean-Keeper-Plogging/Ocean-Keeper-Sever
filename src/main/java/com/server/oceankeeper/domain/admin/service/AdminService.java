package com.server.oceankeeper.domain.admin.service;

import com.server.oceankeeper.domain.admin.dto.req.AdminLoginReqDto;
import com.server.oceankeeper.domain.admin.dto.res.AdminLoginResDto;
import com.server.oceankeeper.domain.user.dto.TokenInfo;
import com.server.oceankeeper.domain.user.entity.OUser;
import com.server.oceankeeper.domain.user.entity.RefreshToken;
import com.server.oceankeeper.domain.user.repository.RedisRepository;
import com.server.oceankeeper.domain.user.repository.UserRepository;
import com.server.oceankeeper.domain.user.service.TokenProvider;
import com.server.oceankeeper.global.exception.IdNotFoundException;
import com.server.oceankeeper.global.exception.IllegalRequestException;
import com.server.oceankeeper.util.UUIDGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {
    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;
    private final RedisRepository refreshTokenRepository;

    @Transactional
    public AdminLoginResDto login(AdminLoginReqDto loginReqDto, Authentication authentication) {
        OUser user = userRepository.findByNickname(loginReqDto.getUserId())
                .orElseThrow(() -> new IdNotFoundException("해당 아이디가 없습니다."));

        TokenInfo tokenDto = tokenProvider.generateTokenDto(authentication);

        RefreshToken refreshToken = RefreshToken.builder()
                .key(authentication.getName()) //{provider}_{providerId}
                .value(tokenDto.getRefreshToken())
                .build();
        log.debug("refresh token :{}", refreshToken);

        refreshTokenRepository.save(refreshToken);

        return new AdminLoginResDto(user.getNickname(), UUIDGenerator.changeUuidToString(user.getUuid()), tokenDto);
    }

    @Transactional
    public void logout(String id) {
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByKey(id + "-9___1-admin");
        log.debug("refresh token :{}", refreshToken);
        if (refreshToken.isEmpty()) {
            //throw new IllegalRequestException("이미 로그아웃 되었습니다.");
            return;
        }

        refreshTokenRepository.delete(refreshToken.get());
    }
}

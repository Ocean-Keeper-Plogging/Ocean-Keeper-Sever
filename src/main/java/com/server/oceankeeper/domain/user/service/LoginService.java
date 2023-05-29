package com.server.oceankeeper.domain.user.service;

import com.server.oceankeeper.domain.user.dto.LoginReqDto;
import com.server.oceankeeper.domain.user.dto.TokenInfo;
import com.server.oceankeeper.domain.user.dto.TokenRequestDto;
import com.server.oceankeeper.domain.user.entitiy.RefreshToken;
import com.server.oceankeeper.domain.user.repository.RedisRepository;
import com.server.oceankeeper.global.exception.IllegalRequestException;
import com.server.oceankeeper.global.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginService {
    private final RedisRepository refreshTokenRepository;
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    @Value("${jwt.password}")
    private String password;

    @Transactional
    public TokenInfo login(LoginReqDto loginReqDto) {
        UsernamePasswordAuthenticationToken authenticationToken = loginReqDto.toAuthentication(password);
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        log.debug("login request :{}, auth : {}, authname :{}", loginReqDto, authentication, authentication.getName());
        TokenInfo tokenDto = tokenProvider.generateTokenDto(authentication);

        RefreshToken refreshToken = RefreshToken.builder()
                .key(authentication.getName()) //{provider}_{providerId}
                .value(tokenDto.getRefreshToken())
                .build();
        log.debug("refresh token :{}", refreshToken);

        refreshTokenRepository.save(refreshToken);

        return tokenDto;
    }

    /**
     * access token이 만료되고, refresh 토큰이 만료되지 않았을 때 신규 access token을 발급 받습니다.
     * refresh 토큰은 유지됩니다.
     *
     * @param tokenRequestDto
     * @return 토큰
     */
    @Transactional
    public TokenInfo reissue(TokenRequestDto tokenRequestDto) {
        if (!tokenProvider.verifyToken(tokenRequestDto.getRefreshToken())) {
            throw new ResourceNotFoundException("Refresh Token이 유효하지 않습니다.");
        }

        Authentication authentication = tokenProvider.getAuthentication(tokenRequestDto.getAccessToken());
        RefreshToken refreshToken = refreshTokenRepository.findByKey(authentication.getName())
                .orElseThrow(() -> new IllegalRequestException("로그아웃된 사용자입니다."));

        if (!refreshToken.getValue().equals(tokenRequestDto.getRefreshToken())) {
            throw new RuntimeException("토큰의 유저 정보가 일치하지 않습니다.");
        }

        TokenInfo tokenDto = tokenProvider.generateTokenDto(authentication, refreshToken.getValue());

        RefreshToken newRefreshToken = refreshToken.updateValue(tokenDto.getRefreshToken());
        refreshTokenRepository.save(newRefreshToken);

        return tokenDto;
    }

    @Transactional
    public void logout(LoginReqDto loginReqDto) {
        final String provider = loginReqDto.getProvider();
        final String providerId = loginReqDto.getProviderId();

        //TODO : 하드코딩 제거
        final String username = provider + "_" + providerId;
        log.info("logout user name : {}", username);
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByKey(username);
        log.debug("refresh token :{}", refreshToken);
        if (refreshToken.isEmpty()) {
            throw new IllegalRequestException("이미 로그아웃 되었습니다.");
        }

        refreshTokenRepository.delete(refreshToken.get());
    }

    @Transactional
    public String[] getProviderInfoFromHeader(HttpServletRequest request) {
        String jwt = tokenProvider.resolveToken(request);
        String[] providerInfo = tokenProvider.getProviderInfo(jwt);
        return providerInfo;
    }
}

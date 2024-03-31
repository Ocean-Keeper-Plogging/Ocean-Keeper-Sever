package com.server.oceankeeper.util;

import com.server.oceankeeper.domain.user.entity.OUser;
import com.server.oceankeeper.domain.user.repository.UserRepository;
import com.server.oceankeeper.domain.user.service.TokenProvider;
import com.server.oceankeeper.global.exception.IdNotFoundException;
import com.server.oceankeeper.global.exception.JwtTokenPayloadException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

@Component
@RequiredArgsConstructor
public class TokenUtil {
    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;

    @Transactional
    public OUser getUserFromHeader(HttpServletRequest request) {
        String jwt = tokenProvider.resolveToken(request);
        String[] providerInfo = tokenProvider.getProviderInfo(jwt);

        if (providerInfo.length != 2)
            throw new JwtTokenPayloadException("토큰에 문제가 있습니다. 일치하는 회원이 없습니다.");
        String provider = providerInfo[0];
        String providerId = providerInfo[1];
        return userRepository.findByProviderAndProviderId(provider, providerId)
                .orElseThrow(() -> new IdNotFoundException("해당 유저를 찾을 수 없습니다."));
    }
}

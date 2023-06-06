package com.server.oceankeeper.domain.user.service;

import com.server.oceankeeper.domain.user.entitiy.LoginUser;
import com.server.oceankeeper.domain.user.entitiy.OUser;
import com.server.oceankeeper.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String oauthId) throws UsernameNotFoundException {
        String[] oauthIdArr = oauthId.split("_");
        String provider = oauthIdArr[0];
        String providerId = oauthIdArr[1];

        log.debug("oauthId : {},", oauthId);

        OUser foundedUser = userRepository.findByProviderAndProviderId(provider, providerId).orElseThrow(
                () -> new InternalAuthenticationServiceException("provider id와 일치하는 회원이 없습니다. 회원가입을 진행해주세요.")
        );
        return new LoginUser(foundedUser);
    }
}
package com.server.oceankeeper.domain.user.service;

import com.server.oceankeeper.domain.user.entity.LoginUser;
import com.server.oceankeeper.domain.user.entity.OUser;
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
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        String[] oauthIdArr = userId.split("-9___1-");
        if (oauthIdArr.length == 1) { //admin case
            OUser foundedUser = userRepository.findByNickname(userId).orElseThrow(
                    () -> new InternalAuthenticationServiceException("일치하는 회원이 없습니다.")
            );
            return new LoginUser(foundedUser);
        } else {
            String provider = oauthIdArr[0];
            String providerId = oauthIdArr[1];

            log.debug("oauthId : {},", userId);

            OUser foundedUser = userRepository.findByProviderAndProviderId(provider, providerId).orElseThrow(
                    () -> new InternalAuthenticationServiceException("provider id와 일치하는 회원이 없습니다. 회원가입을 진행해주세요.")
            );
            return new LoginUser(foundedUser);
        }

    }
}
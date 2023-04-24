package com.server.oceankeeper.Config.Auth;

import com.server.oceankeeper.User.User;
import com.server.oceankeeper.User.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class LoginService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String oauthId) throws UsernameNotFoundException {
        //Provider_providerId 파싱 로직
        String[] oauthIdArr = oauthId.split("_");
        String provider = oauthIdArr[0];
        String providerId = oauthIdArr[1];

        //Provider와 provider_id로 db에서 찾아옴
        User foundedUser = userRepository.findByProviderAndProviderId(provider, providerId).orElseThrow(
                () -> new InternalAuthenticationServiceException("provider id와 일치하는 회원이 없습니다. 회원가입을 진행해주세요")
        );
        return new LoginUser(foundedUser);
    }
}

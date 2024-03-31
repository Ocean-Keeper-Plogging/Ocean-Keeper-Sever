package com.server.oceankeeper.domain.user.service;

import com.server.oceankeeper.domain.user.entity.LoginUser;
import com.server.oceankeeper.domain.user.entity.OUser;
import com.server.oceankeeper.domain.user.repository.UserRepository;
import com.server.oceankeeper.global.exception.IdNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class CustomUserDetailsServiceTest {
    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;
    @Mock
    private UserRepository userRepository;

    @Test
    void loadUserByUsername() {
        //given
        String oauthId = "naver-9___1-providerid";
        OUser user = OUser.builder()
                .id(1L)
                .provider("naver")
                .providerId("provierid")
                .build();
        when(userRepository.findByProviderAndProviderId(any(), any())).thenReturn(Optional.ofNullable(user));
        LoginUser result = (LoginUser) customUserDetailsService.loadUserByUsername(oauthId);

        assertThat(result.getUser()).isEqualTo(user);
    }

    @Test
    void loadUserByUsername_fail_noUser() {
        //given
        String oauthId = "naver-9___1-providerid";
        OUser user = OUser.builder()
                .id(1L)
                .provider("naver")
                .providerId("provierid")
                .build();
        when(userRepository.findByProviderAndProviderId(any(), any()))
                .thenThrow(new IdNotFoundException("provider id와 일치하는 회원이 없습니다. 회원가입을 진행해주세요."));
        assertThrows(IdNotFoundException.class, () -> customUserDetailsService.loadUserByUsername(oauthId));
    }
}
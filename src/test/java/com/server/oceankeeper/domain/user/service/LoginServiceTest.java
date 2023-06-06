package com.server.oceankeeper.domain.user.service;

import com.server.oceankeeper.domain.user.dto.*;
import com.server.oceankeeper.domain.user.entitiy.OUser;
import com.server.oceankeeper.domain.user.entitiy.RefreshToken;
import com.server.oceankeeper.domain.user.entitiy.UserRole;
import com.server.oceankeeper.domain.user.repository.RedisRepository;
import com.server.oceankeeper.domain.user.repository.UserRepository;
import com.server.oceankeeper.global.config.SecurityConfig;
import com.server.oceankeeper.global.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Import(SecurityConfig.class)
@ActiveProfiles("test")
class LoginServiceTest {
    @InjectMocks
    private LoginService loginService;
    @Mock
    private RedisRepository refreshTokenRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TokenProvider tokenProvider;
    @Value("${jwt.password}")
    private String password;

    @Test
    @WithMockUser()
    void login() throws Exception {
        LoginReqDto request = LoginReqDto.builder()
                .deviceToken("deviceToken")
                .provider("provider")
                .providerId("provideId")
                .build();
        TokenInfo mockToken = TokenInfo.builder()
                .accessToken("accessToken")
                .refreshToken("refreshToken")
                .grantType("Bearer")
                .accessTokenExpiresIn(1234L)
                .build();
        OUser mockUser = OUser.builder()
                .uuid(UUID.randomUUID())
                .nickname("kim")
                .deviceToken("deviceToken")
                .build();
        when(tokenProvider.generateTokenDto(any())).thenReturn(mockToken);
        when(userRepository.findByProviderAndProviderId(any(), any())).thenReturn(Optional.ofNullable(mockUser));

        //fake auth
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(UserRole.USER::toString);
        UserDetails principal = new User("naver_providerid", "password", authorities);

        //when
        LoginResDto result = loginService.login(request, new UsernamePasswordAuthenticationToken(principal,password,authorities));

        //then
        assertThat(result).isEqualTo(new LoginResDto(new JoinResDto(mockUser),mockToken));
    }

    @Test
    void reissue() {
        //given
        TokenRequestDto requestToken = TokenRequestDto.builder()
                .accessToken("accessToken")
                .refreshToken("refreshToken")
                .grantType("Bearer")
                .accessTokenExpiresIn(1234L)
                .build();
        TokenInfo mockToken = TokenInfo.builder()
                .accessToken("accessToken2")
                .refreshToken("refreshToken")
                .grantType("Bearer")
                .accessTokenExpiresIn(1234L)
                .build();
        when(tokenProvider.verifyToken(any())).thenReturn(true);
        when(tokenProvider.getAuthentication(any()))
                .thenReturn(new UsernamePasswordAuthenticationToken(null, null));
        when(tokenProvider.generateTokenDto(any(), any())).thenReturn(mockToken);
        RefreshToken mockRefreshToken = new RefreshToken("provider_providerId", "refreshToken");
        when(refreshTokenRepository.findByKey(any())).thenReturn(Optional.of(mockRefreshToken));

        //when
        TokenInfo response = loginService.reissue(requestToken);

        //then
        assertThat(response).isEqualTo(mockToken);
    }

    @Test
    void reissue_fail_리프레시토큰만료() {
        TokenRequestDto requestToken = TokenRequestDto.builder()
                .accessToken("accessToken")
                .refreshToken("refreshToken")
                .grantType("Bearer")
                .accessTokenExpiresIn(1234L)
                .build();
        when(tokenProvider.verifyToken(any())).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> loginService.reissue(requestToken));
    }


    @Test
    void logout() {
        //given
        LogoutReqDto request = LogoutReqDto.builder()
                .deviceToken("deviceToken")
                .provider("provider")
                .providerId("provideId")
                .build();
        RefreshToken mockRefreshToken = new RefreshToken("provider_providerId", "refreshToken");
        when(refreshTokenRepository.findByKey(any())).thenReturn(Optional.of(mockRefreshToken));
        doNothing().when(refreshTokenRepository).delete(any());

        //when
        //then
        loginService.logout(request);
    }
}
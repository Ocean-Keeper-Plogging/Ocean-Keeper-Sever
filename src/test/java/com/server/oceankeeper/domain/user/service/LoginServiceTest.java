package com.server.oceankeeper.domain.user.service;

import com.server.oceankeeper.domain.user.dto.LoginReqDto;
import com.server.oceankeeper.domain.user.dto.TokenInfo;
import com.server.oceankeeper.domain.user.dto.TokenRequestDto;
import com.server.oceankeeper.domain.user.entitiy.RefreshToken;
import com.server.oceankeeper.domain.user.repository.RedisRepository;
import com.server.oceankeeper.global.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {
    @InjectMocks
    private LoginService loginService;
    @Mock
    private RedisRepository refreshTokenRepository;
    @Mock
    private TokenProvider tokenProvider;
    @MockBean
    private AuthenticationManagerBuilder authenticationManagerBuilder;
    @MockBean
    private AuthenticationManager authenticationManager;

    @Test
    @Disabled("authenticationManagerBuilder mock 불가")
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
        ProviderManager providerManager = new ProviderManager();
        when(authenticationManagerBuilder.getObject()).thenReturn(providerManager);
        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(tokenProvider.generateTokenDto(any())).thenReturn(mockToken);

        TokenInfo result = loginService.login(request);
        assertThat(result).isEqualTo(mockToken);
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
        LoginReqDto request = LoginReqDto.builder()
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
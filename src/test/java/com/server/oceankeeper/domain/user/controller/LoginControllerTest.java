package com.server.oceankeeper.domain.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.oceankeeper.domain.user.dto.LoginReqDto;
import com.server.oceankeeper.domain.user.dto.TokenInfo;
import com.server.oceankeeper.domain.user.dto.TokenRequestDto;
import com.server.oceankeeper.domain.user.service.LoginService;
import com.server.oceankeeper.domain.user.service.TokenProvider;
import com.server.oceankeeper.global.config.SecurityConfig;
import com.server.oceankeeper.global.handler.CustomExceptionHandler;
import com.server.oceankeeper.global.jwt.JwtAuthenticationEntryPoint;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@WebMvcTest(value = LoginController.class, includeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = CustomExceptionHandler.class)
})
@Import(JwtAuthenticationEntryPoint.class)
class LoginControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper om;
    @MockBean
    private LoginService loginService;
    @MockBean
    private TokenProvider tokenProvider;
    @MockBean
    private AccessDeniedHandler jwtAccessDeniedHandler;

    @Test
    void login_success_로그인성공() throws Exception {
        //given
        LoginReqDto loginReqDto = LoginReqDto.builder()
                .provider("naver")
                .providerId("id")
                .deviceToken("token")
                .build();
        String requestBody = om.writeValueAsString(loginReqDto);
        when(loginService.login(any())).thenReturn(new TokenInfo("Bearer", "asdf", "asdf", 1234L));

        //when
        ResultActions resultActions = mvc.perform(post("/auth/login")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("Response body : " + responseBody);

        //then
        resultActions.andExpect(status().isCreated());
    }

    @Test
    @WithMockUser
    void logout_success() throws Exception {
        //given
        LoginReqDto loginReqDto = LoginReqDto.builder()
                .provider("naver")
                .providerId("id")
                .deviceToken("token")
                .build();
        String requestBody = om.writeValueAsString(loginReqDto);
        when(loginService.login(any())).thenReturn(new TokenInfo("Bearer", "asdf", "asdf", 1234L));

        //when
        ResultActions resultActions = mvc.perform(post("/auth/logout")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("Response body : " + responseBody);

        //then
        resultActions.andExpect(status().isCreated());
    }

    @Test
    void logout_fail_권한실패() throws Exception {
        //given
        LoginReqDto loginReqDto = LoginReqDto.builder()
                .provider("naver")
                .providerId("id")
                .deviceToken("token")
                .build();
        String requestBody = om.writeValueAsString(loginReqDto);
        when(loginService.login(any())).thenReturn(new TokenInfo("Bearer", "asdf", "asdf", 1234L));

        //when
        ResultActions resultActions = mvc.perform(post("/auth/logout")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("Response body : " + responseBody);

        //then
        resultActions.andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void reissue_sucess() throws Exception {
        //given
        TokenRequestDto tokenRequestDto = TokenRequestDto.builder()
                .grantType("Bearer")
                .accessToken("accessToken")
                .refreshToken("refreshToken")
                .accessTokenExpiresIn(1234L)
                .build();
        String requestBody = om.writeValueAsString(tokenRequestDto);
        when(loginService.reissue(any()))
                .thenReturn(new TokenInfo("Bearer", "accessToken2", "refreshToken2", 1235L));

        //when
        ResultActions resultActions = mvc.perform(post("/auth/reissue")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("Response body : " + responseBody);

        //then
        resultActions.andExpect(status().isCreated());
    }
}
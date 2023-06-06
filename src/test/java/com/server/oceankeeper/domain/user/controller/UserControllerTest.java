package com.server.oceankeeper.domain.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.oceankeeper.domain.user.controller.UserController;
import com.server.oceankeeper.domain.user.dto.JoinReqDto;
import com.server.oceankeeper.domain.user.dto.JoinResDto;
import com.server.oceankeeper.domain.user.dto.UserIdAndNicknameReqDto;
import com.server.oceankeeper.domain.user.entitiy.OUser;
import com.server.oceankeeper.domain.user.service.TokenProvider;
import com.server.oceankeeper.domain.user.service.UserService;
import com.server.oceankeeper.dummy.DummyObject;
import com.server.oceankeeper.global.config.SecurityConfig;
import com.server.oceankeeper.global.exception.DuplicatedResourceException;
import com.server.oceankeeper.global.exception.ForbiddenException;
import com.server.oceankeeper.global.handler.CustomExceptionHandler;
import com.server.oceankeeper.global.jwt.JwtAuthenticationEntryPoint;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@WebMvcTest(value = UserController.class, includeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = CustomExceptionHandler.class)
})
@ActiveProfiles("test")
public class UserControllerTest extends DummyObject {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper om;
    @MockBean
    private UserService userService;

    //Security config
    @MockBean
    private TokenProvider tokenProvider;
    @MockBean
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    @MockBean
    private AccessDeniedHandler jwtAccessDeniedHandler;

    @Test
    public void 회원가입정상() throws Exception {
        //given
        JoinReqDto joinReqDto = JoinReqDto.builder()
                .provider("naver")
                .providerId("12345")
                .nickname("test")
                .email("kim@naver.com")
                .deviceToken("1")
                .build();
        String requestBody = om.writeValueAsString(joinReqDto);
        when(userService.join(any())).thenReturn(new JoinResDto("123","test"));

        System.out.println("request : " + joinReqDto.toEntity());

        //when
        ResultActions resultActions = mvc.perform(post("/auth/signup")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("Response body : " + responseBody);

        //then
        resultActions.andExpect(status().isCreated());
    }

    @Test
    public void join_fail_동일닉네임() throws Exception {
        //given
        JoinReqDto joinReqDto = JoinReqDto.builder()
                .provider("naver")
                .providerId("12345")
                .nickname("overlap")
                .email("kim@naver.com")
                .deviceToken("1")
                .build();

        when(userService.join(any())).thenThrow(new DuplicatedResourceException("동일한 닉네임이 이미 존재합니다."));
        String requestBody = om.writeValueAsString(joinReqDto);

        //when
        ResultActions resultActions = mvc.perform(post("/auth/signup")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON));

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("response body : " + responseBody);

        //then
        resultActions.andExpect(status().isConflict());
        assertThat(responseBody).isEqualTo("{\"status\":\"FAIL\",\"message\":\"동일한 닉네임이 이미 존재합니다.\"}");
    }

    @Test
    public void join_fail_동일ProviderId() throws Exception {
        JoinReqDto joinReqDto = JoinReqDto.builder()
                .provider("naver")
                .providerId("overlap")
                .nickname("1234")
                .email("kim@naver.com")
                .deviceToken("1")
                .profile("http://naver.com")
                .build();
        String requestBody = om.writeValueAsString(joinReqDto);
        JoinResDto expectResult = new JoinResDto(
                OUser.builder()
                .uuid(UUID.fromString("1155e3bc-fbd1-11ed-be56-0242ac120002"))
                .nickname("1234")
                .build()
        );
        when(userService.join(any())).thenThrow(new DuplicatedResourceException("로그인을 시도하는 sns 계정이 이미 가입되어 있습니다."));

        //when
        ResultActions resultActions = mvc.perform(post("/auth/signup")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON));

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("response body : " + responseBody);

        //then
        resultActions.andExpect(status().isConflict());
        assertThat(responseBody).isEqualTo("{\"status\":\"FAIL\",\"message\":\"로그인을 시도하는 sns 계정이 이미 가입되어 있습니다.\"}");
    }

    @Test
    public void nickname_sucess_닉네임중복조회() throws Exception {
        //given
        when(userService.inspectDuplicatedNickname(any())).thenReturn(true);

        //when
        ResultActions resultActions = mvc.perform(get("/auth")
                .param("nickname","kim"));

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("response body : " + responseBody);

        //then
        resultActions.andExpect(status().isOk());
    }

    @Test
    public void nickname_fail_닉네임중복조회동일닉네임() throws Exception {
        //given
        doThrow(new DuplicatedResourceException("로그인을 시도하는 sns 계정이 이미 가입되어 있습니다."))
                .when(userService).inspectDuplicatedNickname(any());

        //when
        ResultActions resultActions = mvc.perform(get("/auth")
                .param("nickname","kim"));

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("response body : " + responseBody);

        //then
        resultActions.andExpect(status().isConflict());
        assertThat(responseBody).isEqualTo("{\"status\":\"FAIL\",\"message\":\"로그인을 시도하는 sns 계정이 이미 가입되어 있습니다.\"}");
    }

    @Test
    @WithMockUser
    public void nickname_sucess_닉네임변경() throws Exception {
        //given
        doNothing().when(userService).modifyNickname(any(),any());
        UserIdAndNicknameReqDto reqDto = new UserIdAndNicknameReqDto("userId","nickname");
        String requestBody = om.writeValueAsString(reqDto);

        //when
        ResultActions resultActions = mvc.perform(put("/auth/nickname")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON));

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("response body : " + responseBody);

        //then
        resultActions.andExpect(status().isCreated());
    }

    @Test
    @WithMockUser
    public void nickname_fail_닉네임변경아이디권한없음() throws Exception {
        //given
        doThrow(new ForbiddenException("해당 아이디의 닉네임을 변경할 권한이 없습니다.")).when(userService).modifyNickname(any(),any());
        UserIdAndNicknameReqDto reqDto = new UserIdAndNicknameReqDto("userId","nickname");
        String requestBody = om.writeValueAsString(reqDto);

        //when
        ResultActions resultActions = mvc.perform(put("/auth/nickname")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON));

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("response body : " + responseBody);

        //then
        resultActions.andExpect(status().isForbidden());
        assertThat(responseBody).isEqualTo("{\"status\":\"FAIL\",\"message\":\"해당 아이디의 닉네임을 변경할 권한이 없습니다.\"}");
    }

    @Test
    @WithMockUser
    public void nickname_fail_닉네임변경중복닉네임() throws Exception {
        //given
        doThrow(new DuplicatedResourceException("동일한 닉네임이 이미 존재합니다.")).when(userService).modifyNickname(any(),any());
        UserIdAndNicknameReqDto reqDto = new UserIdAndNicknameReqDto("userId","nickname");
        String requestBody = om.writeValueAsString(reqDto);

        //when
        ResultActions resultActions = mvc.perform(put("/auth/nickname")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON));

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("response body : " + responseBody);

        //then
        resultActions.andExpect(status().isConflict());
        assertThat(responseBody).isEqualTo("{\"status\":\"FAIL\",\"message\":\"동일한 닉네임이 이미 존재합니다.\"}");
    }
}
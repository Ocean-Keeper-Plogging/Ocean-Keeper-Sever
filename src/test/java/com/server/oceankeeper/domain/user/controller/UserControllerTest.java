package com.server.oceankeeper.domain.user.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import com.server.oceankeeper.global.response.APIResponse;
import com.server.oceankeeper.global.response.ErrorResponse;
import org.junit.jupiter.api.DisplayName;
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
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = CustomExceptionHandler.class),
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
    @DisplayName("회원가입 정상")
    public void signup_success() throws Exception {
        //given
        JoinReqDto joinReqDto = JoinReqDto.builder()
                .provider("naver")
                .providerId("12345")
                .nickname("test")
                .email("kim@naver.com")
                .deviceToken("1")
                .build();
        String requestBody = om.writeValueAsString(joinReqDto);
        when(userService.join(any())).thenReturn(new JoinResDto("123", "test"));

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
    @DisplayName("join_fail_동일닉네임")
    public void join_fail_same_nickname() throws Exception {
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

        APIResponse<ErrorResponse> responseBody = om.readValue(resultActions.andReturn().getResponse().getContentAsString(), new TypeReference<>() {});
        System.out.println("response body : " + responseBody);

        //then
        resultActions.andExpect(status().isConflict());
        assertThat(responseBody.getResponse().getErrorDetail()).isEqualTo("동일한 닉네임이 이미 존재합니다.");
    }

    @Test
    @DisplayName("join_fail_동일ProviderId")
    public void join_fail_same_providerId() throws Exception {
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
        APIResponse<ErrorResponse> responseBody = om.readValue(resultActions.andReturn().getResponse().getContentAsString(), new TypeReference<>() {});
        System.out.println("response body : " + responseBody);

        //then
        resultActions.andExpect(status().isConflict());
        assertThat(responseBody.getResponse().getErrorDetail()).isEqualTo("로그인을 시도하는 sns 계정이 이미 가입되어 있습니다.");
    }

    @Test
    @DisplayName("닉네임 중복조회에 성공한다")
    public void check_nickname_duplicated() throws Exception {
        //given
        when(userService.inspectDuplicatedNickname(any())).thenReturn(true);

        //when
        ResultActions resultActions = mvc.perform(get("/auth")
                .param("nickname", "kim"));

        APIResponse<String> responseBody = om.readValue(resultActions.andReturn().getResponse().getContentAsString(), new TypeReference<>() {});
        System.out.println("response body : " + responseBody);

        //then
        resultActions.andExpect(status().isOk());
    }

    @Test
    @DisplayName("닉네임 중복 조회했으나 동일 닉네임이 존재하여 실패한다.")
    public void duplicated_nickname_exists() throws Exception {
        //given
        doThrow(new DuplicatedResourceException("로그인을 시도하는 sns 계정이 이미 가입되어 있습니다."))
                .when(userService).inspectDuplicatedNickname(any());

        //when
        ResultActions resultActions = mvc.perform(get("/auth")
                .param("nickname", "kim"));

        APIResponse<ErrorResponse> responseBody = om.readValue(resultActions.andReturn().getResponse().getContentAsString(), new TypeReference<>() {});
        System.out.println("response body : " + responseBody);

        //then
        resultActions.andExpect(status().isConflict());
        assertThat(responseBody.getResponse().getErrorDetail()).isEqualTo("로그인을 시도하는 sns 계정이 이미 가입되어 있습니다.");
    }

    @Test
    @WithMockUser
    @DisplayName("닉네임 변경에 성공한다")
    public void change_nickname() throws Exception {
        //given
        doNothing().when(userService).modifyNickname(any(), any());
        UserIdAndNicknameReqDto reqDto = new UserIdAndNicknameReqDto("userId", "nickname");
        String requestBody = om.writeValueAsString(reqDto);

        //when
        ResultActions resultActions = mvc.perform(put("/auth/nickname")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON));

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("response body : " + responseBody);

        //then
        resultActions.andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    @DisplayName("닉네임 변경하려는 권한이 없어 실패한다")
    public void change_nickname_fail_no_acess() throws Exception {
        //given
        doThrow(new ForbiddenException("해당 아이디의 닉네임을 변경할 권한이 없습니다.")).when(userService).modifyNickname(any(), any());
        UserIdAndNicknameReqDto reqDto = new UserIdAndNicknameReqDto("userId", "nickname");
        String requestBody = om.writeValueAsString(reqDto);

        //when
        ResultActions resultActions = mvc.perform(put("/auth/nickname")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON));

        APIResponse<ErrorResponse> responseBody = om.readValue(resultActions.andReturn().getResponse().getContentAsString(), new TypeReference<>() {});
        System.out.println("response body : " + responseBody);

        //then
        resultActions.andExpect(status().isForbidden());
        assertThat(responseBody.getResponse().getErrorDetail()).isEqualTo("해당 아이디의 닉네임을 변경할 권한이 없습니다.");
    }

    @Test
    @WithMockUser
    @DisplayName("변경하려는 닉네임이 중복되어 실패한다")
    public void change_nickname_duplicated() throws Exception {
        //given
        doThrow(new DuplicatedResourceException("동일한 닉네임이 이미 존재합니다.")).when(userService).modifyNickname(any(), any());
        UserIdAndNicknameReqDto reqDto = new UserIdAndNicknameReqDto("userId", "nickname");
        String requestBody = om.writeValueAsString(reqDto);

        //when
        ResultActions resultActions = mvc.perform(put("/auth/nickname")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON));

        APIResponse<ErrorResponse> responseBody = om.readValue(resultActions.andReturn().getResponse().getContentAsString(), new TypeReference<>() {});
        System.out.println("response body : " + responseBody);

        //then
        resultActions.andExpect(status().isConflict());
        assertThat(responseBody.getResponse().getErrorDetail()).isEqualTo("동일한 닉네임이 이미 존재합니다.");
    }
}
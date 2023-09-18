package com.server.oceankeeper.domain.notice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.oceankeeper.domain.notice.controller.NoticeController;
import com.server.oceankeeper.domain.notice.dto.request.NoticeModifyReqDto;
import com.server.oceankeeper.domain.notice.dto.response.NoticeResDto;
import com.server.oceankeeper.domain.notice.service.NoticeService;
import com.server.oceankeeper.domain.user.service.TokenProvider;
import com.server.oceankeeper.global.config.SecurityConfig;
import com.server.oceankeeper.global.exception.ResourceNotFoundException;
import com.server.oceankeeper.global.handler.CustomExceptionHandler;
import com.server.oceankeeper.global.jwt.JwtAuthenticationEntryPoint;
import com.server.oceankeeper.util.TokenUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = NoticeController.class, includeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = CustomExceptionHandler.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = AccessDeniedHandler.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationEntryPoint.class)
})
@ActiveProfiles("test")
class NoticeControllerTest {
    private MockMvc mvc;
    @Autowired
    private ObjectMapper om;

    @MockBean
    private NoticeService service;

    @MockBean
    private TokenProvider tokenProvider;
    @MockBean
    private TokenUtil tokenUtil;

    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    private void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("공지사항 조회")
    void getNotice() throws Exception {
        //given
        when(service.get(any(), any())).thenReturn(null);

        //when
        ResultActions resultActions = mvc.perform(get("/notice")
                .param("id", "1")
                .contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("Response body : " + responseBody);

        //then
        resultActions.andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("공지사항 수정 성공")
    void putNotice() throws Exception {
        //given
        NoticeResDto expectResponse = new NoticeResDto(1L, "new contents", LocalDateTime.now().toLocalDate());
        when(service.put(any())).thenReturn(expectResponse);

        //when
        NoticeModifyReqDto request = new NoticeModifyReqDto(1L, "new contents", "title");
        ResultActions resultActions = mvc.perform(put("/admin/notice")
                .content(om.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("Response body : " + responseBody);

        //then
        resultActions.andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("공지사항 수정 권한 없음")
    void putNotice_no_roles() throws Exception {
        //given
        NoticeResDto expectResponse = new NoticeResDto(1L, "new contents", LocalDateTime.now().toLocalDate());
        when(service.put(any())).thenReturn(expectResponse);

        //when
        NoticeModifyReqDto request = new NoticeModifyReqDto(1L, "new contents", "title");
        ResultActions resultActions = mvc.perform(put("/admin/notice")
                .content(om.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("Response body : " + responseBody);

        //then
        resultActions.andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("공지사항 수정 아이디 누락")
    void putNotice_no_id() throws Exception {
        //given
        when(service.put(any())).thenThrow(new ResourceNotFoundException("공지사항 아이디에 해당하는 공지사항이 없습니다."));

        //when
        NoticeModifyReqDto request = new NoticeModifyReqDto(null, "new contents", "title");
        ResultActions resultActions = mvc.perform(put("/admin/notice")
                .content(om.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("Response body : " + responseBody);

        //then
        resultActions.andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("공지사항 작성")
    void postNotice() throws Exception {
        //given
        when(service.get(any(), any())).thenReturn(null);

        //when
        ResultActions resultActions = mvc.perform(get("/notice")
                .param("id", "1")
                .contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("Response body : " + responseBody);

        //then
        resultActions.andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    @DisplayName("공지사항 상세 내용 조회")
    void getNoticeDetail() throws Exception {
        //given
        when(service.getDetail(any())).thenReturn(null);

        //when
        ResultActions resultActions = mvc.perform(get("/notice/detail")
                .param("notice-id", "1")
                .contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("Response body : " + responseBody);

        //then
        resultActions.andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    @DisplayName("공지사항 상세 내용 조회했으나 아이디없음")
    void getNoticeDetail_err() throws Exception {
        //given
        when(service.getDetail(any())).thenThrow(new ResourceNotFoundException("해당 공지사항 없음"));

        //when
        ResultActions resultActions = mvc.perform(get("/notice/detail")
                .param("notice-id", "5")
                .contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("Response body : " + responseBody);

        //then
        resultActions.andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("공지사항 삭제")
    void deleteNotice() throws Exception {
        //given
        when(service.delete(any())).thenReturn(true);

        //when
        ResultActions resultActions = mvc.perform(delete("/admin/notice")
                .param("notice-id", "1")
                .contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("Response body : " + responseBody);

        //then
        resultActions.andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("공지사항 삭제요청했으나 해당아이디없음")
    void deleteNotice_err() throws Exception {
        //given
        when(service.delete(any())).thenThrow(new ResourceNotFoundException("해당 공지사항 없음"));

        //when
        ResultActions resultActions = mvc.perform(delete("/admin/notice")
                .param("notice-id", "5")
                .contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("Response body : " + responseBody);

        //then
        resultActions.andExpect(status().isNotFound());
    }
}
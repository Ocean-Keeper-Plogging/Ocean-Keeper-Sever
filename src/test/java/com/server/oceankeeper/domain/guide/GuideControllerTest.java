package com.server.oceankeeper.domain.guide;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.oceankeeper.domain.guide.controller.GuideController;
import com.server.oceankeeper.domain.guide.dto.request.GuideModifyReqDto;
import com.server.oceankeeper.domain.guide.dto.response.GuideResDto;
import com.server.oceankeeper.domain.guide.service.GuideService;
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

@WebMvcTest(value = GuideController.class, includeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = CustomExceptionHandler.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = AccessDeniedHandler.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationEntryPoint.class)
})
@ActiveProfiles("test")
class GuideControllerTest {
    //@Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper om;

    @MockBean
    private GuideService service;

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
    void getGuide() throws Exception {
        //given
        when(service.get(any(), any())).thenReturn(null);

        //when
        ResultActions resultActions = mvc.perform(get("/guide")
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
    void putGuide() throws Exception {
        //given
        GuideResDto expectResponse = new GuideResDto(1L, "new contents", LocalDateTime.now().toLocalDate());
        when(service.put(any())).thenReturn(expectResponse);

        //when
        GuideModifyReqDto request = new GuideModifyReqDto(1L, "new contents", "title");
        ResultActions resultActions = mvc.perform(put("/admin/guide")
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
    void putGuide_no_roles() throws Exception {
        //given
        GuideResDto expectResponse = new GuideResDto(1L, "new contents", LocalDateTime.now().toLocalDate());
        when(service.put(any())).thenReturn(expectResponse);

        //when
        GuideModifyReqDto request = new GuideModifyReqDto(1L, "new contents", "title");
        ResultActions resultActions = mvc.perform(put("/admin/guide")
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
    void putGuide_no_id() throws Exception {
        //given
        when(service.put(any())).thenThrow(new ResourceNotFoundException("공지사항 아이디에 해당하는 공지사항이 없습니다."));

        //when
        GuideModifyReqDto request = new GuideModifyReqDto(null, "new contents", "title");
        ResultActions resultActions = mvc.perform(put("/admin/guide")
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
    void postGuide() throws Exception {
        //given
        when(service.get(any(), any())).thenReturn(null);

        //when
        ResultActions resultActions = mvc.perform(get("/guide")
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
    void getGuideDetail() throws Exception {
        //given
        when(service.getDetail(any())).thenReturn(null);

        //when
        ResultActions resultActions = mvc.perform(get("/guide/detail")
                .param("guide-id", "1")
                .contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("Response body : " + responseBody);

        //then
        resultActions.andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    @DisplayName("공지사항 상세 내용 조회했으나 아이디없음")
    void getGuideDetail_err() throws Exception {
        //given
        when(service.getDetail(any())).thenThrow(new ResourceNotFoundException("해당 공지사항 없음"));

        //when
        ResultActions resultActions = mvc.perform(get("/guide/detail")
                .param("guide-id", "5")
                .contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("Response body : " + responseBody);

        //then
        resultActions.andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("공지사항 삭제")
    void deleteGuide() throws Exception {
        //given
        when(service.delete(any())).thenReturn(true);

        //when
        ResultActions resultActions = mvc.perform(delete("/admin/guide")
                .param("guide-id", "1")
                .contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("Response body : " + responseBody);

        //then
        resultActions.andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("공지사항 삭제요청했으나 해당아이디없음")
    void deleteGuide_err() throws Exception {
        //given
        when(service.delete(any())).thenThrow(new ResourceNotFoundException("해당 공지사항 없음"));

        //when
        ResultActions resultActions = mvc.perform(delete("/admin/guide")
                .param("guide-id", "5")
                .contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("Response body : " + responseBody);

        //then
        resultActions.andExpect(status().isNotFound());
    }
}
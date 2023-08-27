package com.server.oceankeeper.domain.message.messageController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.oceankeeper.domain.message.service.MessageService;
import com.server.oceankeeper.domain.user.service.TokenProvider;
import com.server.oceankeeper.global.config.SecurityConfig;
import com.server.oceankeeper.global.handler.CustomExceptionHandler;
import com.server.oceankeeper.global.jwt.JwtAuthenticationEntryPoint;
import com.server.oceankeeper.util.TokenUtil;
import org.junit.jupiter.api.Disabled;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = MessageController.class, includeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = CustomExceptionHandler.class)
})
@ActiveProfiles("test")
@Disabled("not implemented")
class MessageControllerTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper om;

    @MockBean
    private MessageService messageService;

    @MockBean
    private TokenProvider tokenProvider;
    @MockBean
    private AccessDeniedHandler accessDeniedHandler;
    @MockBean
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    @MockBean
    private TokenUtil tokenUtil;

    @Test
    @WithMockUser
    @DisplayName("보낸 쪽지함 확인")
    public void getMailing() throws Exception {
        //given
        when(messageService.getMailing(any(), any(), any(), any(), any())).thenReturn(null);

        //when
        ResultActions resultActions = mvc.perform(get("/message/post/user/1")
                .contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("Response body : " + responseBody);

        //then
        resultActions.andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    @DisplayName("보낸 쪽지함 확인 produce 타입에러")
    public void getMailing_error() throws Exception {
        //given
        when(messageService.getMailing(any(), any(), any(), any(), any())).thenReturn(null);

        //when
        ResultActions resultActions = mvc.perform(get("/message/post/user/1")
                .contentType(MediaType.MULTIPART_FORM_DATA));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("Response body : " + responseBody);

        //then
        resultActions.andExpect(status().isOk());
    }
}
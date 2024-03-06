package com.server.oceankeeper.domain.notification.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.oceankeeper.domain.notification.dto.NotificationAlarmDto;
import com.server.oceankeeper.domain.notification.dto.NotificationResDto;
import com.server.oceankeeper.domain.notification.service.NotificationService;
import com.server.oceankeeper.domain.user.dto.JoinReqDto;
import com.server.oceankeeper.domain.user.dto.JoinResDto;
import com.server.oceankeeper.domain.user.repository.UserRepository;
import com.server.oceankeeper.domain.user.service.CustomUserDetailsService;
import com.server.oceankeeper.domain.user.service.LoginService;
import com.server.oceankeeper.domain.user.service.TokenProvider;
import com.server.oceankeeper.global.config.SecurityConfig;
import com.server.oceankeeper.global.handler.CustomExceptionHandler;
import com.server.oceankeeper.global.jwt.JwtAuthenticationEntryPoint;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@WebMvcTest(value = NotificationController.class, includeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = CustomExceptionHandler.class)
})
@Import({JwtAuthenticationEntryPoint.class, CustomUserDetailsService.class})
@ActiveProfiles("test")
class NotificationControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper om;
    @MockBean
    private NotificationService notificationService;
    @MockBean
    private TokenProvider tokenProvider;
    @MockBean
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    @MockBean
    private AccessDeniedHandler jwtAccessDeniedHandler;
    @MockBean
    private UserRepository userRepository;

    @Test
    void getNotification() throws Exception {
        //given
        NotificationResDto notificationResDto = new NotificationResDto(List.of(
                new NotificationResDto.NotificationData(
                        0L,"contents", LocalDate.now().toString(),false))
                ,new NotificationResDto.Meta(1,true));
        String requestBody = om.writeValueAsString(notificationResDto);
        when(notificationService.getNotificationList(any(),any(),any(),any()))
                .thenReturn(notificationResDto);

        System.out.println("request : " + notificationResDto);

        //when
        ResultActions resultActions = mvc.perform(get("/notification/user/test")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("Response body : " + responseBody);

        //then
        resultActions.andExpect(status().isOk());
    }

    @Test
    void setNotification() throws Exception {
        //given
        NotificationAlarmDto request = new NotificationAlarmDto(true);
        String requestBody = om.writeValueAsString(request);
        when(notificationService.setNotification(any(),any(),any()))
                .thenReturn(request);

        System.out.println("request : " + request);

        //when
        ResultActions resultActions = mvc.perform(post("/notification/user/test/alarm")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        resultActions.andExpect(status().isOk());
    }

    @Test
    void testGetNotification() throws Exception {
        //given
        NotificationAlarmDto request = new NotificationAlarmDto(true);
        String requestBody = om.writeValueAsString(request);
        when(notificationService.getNotification(any(),any()))
                .thenReturn(request);

        System.out.println("request : " + request);

        //when
        ResultActions resultActions = mvc.perform(get("/notification/user/test/alarm")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        resultActions.andExpect(status().isOk());
    }
}
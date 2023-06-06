package com.server.oceankeeper.domain.activity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.oceankeeper.domain.activity.controller.ActivityController;
import com.server.oceankeeper.domain.activity.dto.response.MyActivitiesDto;
import com.server.oceankeeper.domain.activity.dto.response.MyActivityDto;
import com.server.oceankeeper.domain.activity.service.ActivityService;
import com.server.oceankeeper.domain.user.service.TokenProvider;
import com.server.oceankeeper.global.config.SecurityConfig;
import com.server.oceankeeper.global.handler.CustomExceptionHandler;
import com.server.oceankeeper.global.jwt.JwtAuthenticationEntryPoint;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = ActivityController.class, includeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = CustomExceptionHandler.class)
})
@ActiveProfiles("test")
class ActivityControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper om;
    @MockBean
    private ActivityService activityService;
    @MockBean
    private TokenProvider tokenProvider;
    @MockBean
    private AccessDeniedHandler accessDeniedHandler;
    @MockBean
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Test
    @WithMockUser
    @DisplayName("내가 속한 활동 리스트 가져오기")
    public void getMyActivity() throws Exception {
        //given
        when(activityService.getMyActivity(any())).thenReturn(List.of(
                new MyActivityDto("1", 11,
                        "This is flogging",
                        LocalDateTime.now(),
                        "제주도 능금해변"
                ),
                new MyActivityDto("2", 12,
                        "This is flogging 2",
                        LocalDateTime.now().plusDays(3),
                        "제주도 능금해변"
                )
        ));

        //when
        ResultActions resultActions = mvc.perform(get("/activity/user/1")
                .contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("Response body : " + responseBody);

        //then
        resultActions.andExpect(status().isOk());
    }
}
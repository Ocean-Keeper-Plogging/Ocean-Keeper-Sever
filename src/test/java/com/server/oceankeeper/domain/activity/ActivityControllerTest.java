package com.server.oceankeeper.domain.activity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.oceankeeper.domain.activity.controller.ActivityController;
import com.server.oceankeeper.domain.activity.dto.request.*;
import com.server.oceankeeper.domain.activity.dto.response.ApplicationReqDto;
import com.server.oceankeeper.domain.activity.dto.response.ApplyActivityResDto;
import com.server.oceankeeper.domain.activity.dto.response.MyScheduledActivityDto;
import com.server.oceankeeper.domain.activity.dto.response.RegisterActivityResDto;
import com.server.oceankeeper.domain.activity.entity.GarbageCategory;
import com.server.oceankeeper.domain.activity.entity.LocationTag;
import com.server.oceankeeper.domain.activity.service.ActivityService;
import com.server.oceankeeper.domain.user.entitiy.OUser;
import com.server.oceankeeper.domain.user.service.TokenProvider;
import com.server.oceankeeper.global.aop.DtoValidationAdvice;
import com.server.oceankeeper.global.config.SecurityConfig;
import com.server.oceankeeper.global.handler.CustomExceptionHandler;
import com.server.oceankeeper.global.jwt.JwtAuthenticationEntryPoint;
import com.server.oceankeeper.util.TokenUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = ActivityController.class, includeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = CustomExceptionHandler.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = AccessDeniedHandler.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationEntryPoint.class)
})
@Import({DtoValidationAdvice.class, AopAutoConfiguration.class})
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
    private TokenUtil tokenUtil;

    @Test
    @WithMockUser
    @DisplayName("내가 속한 활동 리스트 가져오기")
    public void getMyActivity() throws Exception {
        //given
        when(activityService.getMyScheduleActivity(any())).thenReturn(List.of(
                new MyScheduledActivityDto("1", 11,
                        "This is flogging",
                        LocalDateTime.now().toString(),
                        "제주도 능금해변"
                ),
                new MyScheduledActivityDto("2", 12,
                        "This is flogging 2",
                        LocalDateTime.now().plusDays(3).toString(),
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

    @Test
    @DisplayName("활동 수정하기")
    @WithMockUser
    void edit() throws Exception {
        when(activityService.getMyScheduleActivity(any())).thenReturn(List.of(
                new MyScheduledActivityDto("1", 11,
                        "This is flogging",
                        LocalDateTime.now().toString(),
                        "제주도 능금해변"
                ),
                new MyScheduledActivityDto("2", 12,
                        "This is flogging 2",
                        LocalDateTime.now().plusDays(3).toString(),
                        "제주도 능금해변"
                )
        ));

        ModifyActivityReqDto request = ModifyActivityReqDto.builder().build();
        //when
        ResultActions resultActions = mvc.perform(patch("/activity/recruitment/1")
                        .content(om.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("Response body : " + responseBody);

        //then
        resultActions.andExpect(status().isOk());
    }

    @Test
    @DisplayName("활동 등록")
    @WithMockUser
    void registerActivity() throws Exception {
        when(activityService.registerActivity(any())).thenReturn(
                new RegisterActivityResDto("activityId")
        );

        RegisterActivityReqDto request = RegisterActivityReqDto.builder()
                .userId("831ea182ffcd11edbe560242ac120002")
                .title("title")
                .location(new LocationDto("제주시", 123.1, 123.1))
                .transportation("카셰어링 연결 예정")
                .garbageCategory(GarbageCategory.COASTAL)
                .locationTag(LocationTag.EAST)
                .recruitStartAt(LocalDate.now())
                .recruitEndAt(LocalDate.now().plusDays(5))
                .startAt(LocalDateTime.now().plusHours(10))
                //.thumbnailUrl("")
                .keeperIntroduction("hi")
                //.keeperImageUrl("")
                .activityStory("story")
                //.storyImageUrl("")
                .quota(5)
                .programDetails("12시 집결")
                .preparation("긴 상하의")
                .rewards("점심제공")
                .etc("오세요")
                .build();
        String requestStr = om.writeValueAsString(request);

        //when
        ResultActions resultActions = mvc.perform(post("/activity/recruitment")
                        .content(requestStr)
                .contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("Response body : " + responseBody);

        //then
        resultActions.andExpect(status().isCreated());
    }

    @Test
    @DisplayName("활동 모집 취소")
    @WithMockUser
    void cancelActivity() throws Exception {
        doNothing().when(activityService).cancelActivity(any(),any());

        //when
        ResultActions resultActions = mvc.perform(delete("/activity/recruitment")
                        .param("activity-id","831ea182ffcd11edbe560242ac120002")
                .contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("Response body : " + responseBody);

        //then
        resultActions.andExpect(status().isOk());
    }

    @Test
    @DisplayName("api명 에러")
    @WithMockUser
    void errorApiName() throws Exception {
        //when
        ResultActions resultActions = mvc.perform(delete("/activity/recruitment1")
                .param("activity-id","831ea182ffcd11edbe560242ac120002")
                .contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("Response body : " + responseBody);

        //then
        resultActions.andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("활동 지원")
    @WithMockUser
    void applyActivity() throws Exception {
        when(activityService.applyActivity(any())).thenReturn(
                new ApplyActivityResDto("activityId","applicationId")
        );

        ApplyApplicationReqDto request = ApplyApplicationReqDto.builder()
                .activityId("activityId")
                .userId("userId")
                .name("kim")
                .phoneNumber("01012341234")
                .email("kim@naver.com")
                .transportation("자차")
                .privacyAgreement(true)
                .build();
        String requestStr = om.writeValueAsString(request);

        //when
        ResultActions resultActions = mvc.perform(post("/activity/recruitment/application")
                .content(requestStr)
                .contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("Response body : " + responseBody);

        //then
        resultActions.andExpect(status().isCreated());
    }

    @Test
    @DisplayName("활동 지원 프라이버시 동의 확인없음")
    @WithMockUser
    void applyActivity_no_privacy_agreement() throws Exception {
        when(activityService.applyActivity(any())).thenReturn(
                new ApplyActivityResDto("activityId","applicationId")
        );

        ApplyApplicationReqDto request = ApplyApplicationReqDto.builder()
                .activityId("activityId")
                .userId("userId")
                .build();
        String requestStr = om.writeValueAsString(request);

        //when
        ResultActions resultActions = mvc.perform(post("/activity/recruitment/application")
                .content(requestStr)
                .contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("Response body : " + responseBody);

        //then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("활동 수정")
    @WithMockUser
    void modifyActivity() throws Exception {
        when(tokenUtil.getUserFromHeader(any())).thenReturn(OUser.builder().build());
        doNothing().when(activityService).modifyActivity(any(),any(),any());

        ModifyActivityReqDto request = ModifyActivityReqDto.builder()
                .title("title")
                .location(new LocationDto("제주시", 123.1, 123.1))
                .transportation("카셰어링 연결 예정")
                .garbageCategory(GarbageCategory.COASTAL)
                .locationTag(LocationTag.EAST)
                .recruitStartAt(LocalDate.now())
                .recruitEndAt(LocalDate.now().plusDays(5))
                .startAt(LocalDateTime.now().plusHours(10))
                //.thumbnailUrl("")
                .keeperIntroduction("hi")
                //.keeperImageUrl("")
                .activityStory("story")
                //.storyImageUrl("")
                .quota(5)
                .programDetails("12시 집결")
                .preparation("긴 상하의")
                .rewards("점심제공")
                .etc("오세요")
                .build();
        String requestStr = om.writeValueAsString(request);

        //when
        ResultActions resultActions = mvc.perform(patch("/activity/recruitment/1")
                        .content(requestStr)
                .contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("Response body : " + responseBody);

        //then
        resultActions.andExpect(status().isOk());
    }

    @Test
    @DisplayName("활동 지원서 수정")
    @WithMockUser
    void modifyApplication() throws Exception {
        when(tokenUtil.getUserFromHeader(any())).thenReturn(OUser.builder().build());
        doNothing().when(activityService).modifyApplication(any(),any(),any());

        ModifyApplicationReqDto request = ModifyApplicationReqDto.builder()
                .privacyAgreement(true)
                .build();
        String requestStr = om.writeValueAsString(request);

        //when
        ResultActions resultActions = mvc.perform(patch("/activity/recruitment/application/1")
                        .content(requestStr)
                .contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("Response body : " + responseBody);

        //then
        resultActions.andExpect(status().isOk());
    }

    @Test
    @DisplayName("마지막 활동 지원서 불러오기")
    @WithMockUser
    void getLastApplication() throws Exception {
        when(tokenUtil.getUserFromHeader(any())).thenReturn(OUser.builder().build());
        when(activityService.getLastApplication(any())).thenReturn(ApplicationReqDto.builder().build());

        //when
        ResultActions resultActions = mvc.perform(get("/activity/recruitment/application/last")
                .contentType(MediaType.APPLICATION_JSON));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("Response body : " + responseBody);

        //then
        resultActions.andExpect(status().isOk());
    }
}
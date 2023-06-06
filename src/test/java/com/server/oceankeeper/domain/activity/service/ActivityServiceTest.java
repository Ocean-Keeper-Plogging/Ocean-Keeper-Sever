package com.server.oceankeeper.domain.activity.service;

import com.server.oceankeeper.domain.activity.dto.request.ApplyActivityReqDto;
import com.server.oceankeeper.domain.activity.dto.request.LocationDto;
import com.server.oceankeeper.domain.activity.dto.request.RegisterActivityReqDto;
import com.server.oceankeeper.domain.activity.dto.response.ApplyActivityResDto;
import com.server.oceankeeper.domain.activity.dto.response.MyActivityDto;
import com.server.oceankeeper.domain.activity.dto.response.RegisterActivityResDto;
import com.server.oceankeeper.domain.activity.entity.*;
import com.server.oceankeeper.domain.activity.repository.ActivityDetailRepository;
import com.server.oceankeeper.domain.activity.repository.ActivityRepository;
import com.server.oceankeeper.domain.crew.entitiy.Crews;
import com.server.oceankeeper.domain.crew.repository.CrewRepository;
import com.server.oceankeeper.domain.crew.service.CrewService;
import com.server.oceankeeper.domain.user.entitiy.OUser;
import com.server.oceankeeper.domain.user.entitiy.UserRole;
import com.server.oceankeeper.domain.user.entitiy.UserStatus;
import com.server.oceankeeper.domain.user.repository.UserRepository;
import com.server.oceankeeper.dummy.DummyObject;
import com.server.oceankeeper.global.exception.IdNotFoundException;
import com.server.oceankeeper.global.exception.IllegalRequestException;
import com.server.oceankeeper.util.UUIDGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class ActivityServiceTest extends DummyObject {
    @InjectMocks
    private ActivityService activityService;
    @Mock
    private ActivityRepository activityRepository;
    @Mock
    private ActivityDetailRepository activityDetailRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CrewService crewService;
    @Mock
    private CrewRepository crewRepository;

    private OUser createUser() {
        return OUser.builder()
                .uuid(UUID.fromString("831ea182-ffcd-11ed-be56-0242ac120002"))
                .id(1L)
                .deviceToken("devicetoken")
                .email("kim@naver.com")
                .providerId("providerId")
                .provider("naver")
                .nickname("kim")
                .createdAt(LocalDateTime.now().minusMinutes(30))
                .password("123")
                .role(UserRole.USER)
                .profile("profile@naver.com")
                .status(UserStatus.ACTIVE)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void registerActivity() {
        //given
        RegisterActivityReqDto request = getRegisterActivityRequest();
        Activity expectActivity = request.toActivityEntity();
        ActivityDetail expectActivityDetail = request.toActivityDetailEntity();
        OUser expectUser = createUser();
        when(activityRepository.findByUuid(any())).thenReturn(Optional.of(expectActivity));
        when(activityDetailRepository.findByActivity(any())).thenReturn(Optional.of(expectActivityDetail));
        when(userRepository.findByUuid(any())).thenReturn(Optional.ofNullable(expectUser));

        //when
        RegisterActivityResDto result = activityService.registerActivity(request);

        Activity resultActivity = activityRepository.findByUuid(UUIDGenerator.changeUuidFromString(result.getActivityId())).get();
        ActivityDetail resultActivityDetail = activityDetailRepository.findByActivity(resultActivity).get();

        assertThat(resultActivityDetail.getActivityStory()).isEqualTo(request.getActivityStory());
        assertThat(resultActivityDetail.getEtc()).isEqualTo(request.getEtc());
        assertThat(resultActivity.getGarbageCategory()).isEqualTo(request.getGarbageCategory());
        assertThat(resultActivityDetail.getKeeperIntroduction()).isEqualTo(request.getKeeperIntroduction());
        assertThat(resultActivity.getLocationTag()).isEqualTo(request.getLocationTag());
        assertThat(resultActivityDetail.getPreparation()).isEqualTo(request.getPreparation());
        assertThat(resultActivityDetail.getProgramDetails()).isEqualTo(request.getProgramDetails());
        assertThat(resultActivity.getQuota()).isEqualTo(request.getQuota());
        assertThat(resultActivity.getRecruitStartAt()).isEqualTo(request.getRecruitStartAt());
        assertThat(resultActivity.getRecruitEndAt()).isEqualTo(request.getRecruitEndAt());
        assertThat(resultActivity.getStartAt()).isEqualTo(request.getStartAt());
        assertThat(resultActivityDetail.getRewards()).isEqualTo(request.getRewards());
        assertThat(resultActivity.getTitle()).isEqualTo(request.getTitle());
        assertThat(resultActivity.getLocation()).isEqualTo(request.getLocation().toEntity());
        assertThat(resultActivity.getParticipants()).isEqualTo(0);
    }

    @Test
    @DisplayName("registerActivity_fail_유저못찾음")
    void registerActivity_fail_noFoundUser() {
        //given
        RegisterActivityReqDto request = getRegisterActivityRequest();
        when(userRepository.findByUuid(any())).thenThrow(IdNotFoundException.class);

        //then
        assertThrows(IdNotFoundException.class, () -> activityService.registerActivity(request));
    }

    @Test
    @DisplayName("나의 활동 찾기")
    void getMyActivity() {
        //given
        OUser mockUser = newMockUser(123L, "kim", "naver", "providerid1", UUID.randomUUID());
        List<MyActivityDto> mockResult = List.of(
                MyActivityDto.builder()
                        .id("id")
                        .location("제주")
                        .dDay(5)
                        .startDay(LocalDateTime.now().plusDays(5))
                        .title("테스트")
                        .build(),
                MyActivityDto.builder()
                        .id("id2")
                        .location("제주2")
                        .dDay(5)
                        .startDay(LocalDateTime.now().plusDays(6))
                        .title("테스트2")
                        .build()
        );
        when(crewService.findCrews(any())).thenReturn(mockResult);

        //when
        List<MyActivityDto> result = activityService.getMyActivity(UUIDGenerator.changeUuidToString(mockUser.getUuid()));
        assertThat(result).isEqualTo(mockResult);
    }

    @Test
    @DisplayName("활동 지원")
    void applyActivity() {
        //given
        Activity mockActivity = Activity.builder()
                .quota(5)
                .participants(1)
                .uuid(UUIDGenerator.changeUuidFromString("123ea182ffcd11edbe560242ac120002"))
                .build();
        when(activityRepository.findByUuid(any())).thenReturn(Optional.ofNullable(mockActivity));

        OUser mockUser = OUser.builder()
                .id(123L)
                .uuid(UUIDGenerator.changeUuidFromString("831ea182ffcd11edbe560242ac120002"))
                .build();
        when(userRepository.findByUuid(any())).thenReturn(Optional.ofNullable(mockUser));

        Crews newCrew = Crews.builder()
                .id(10L)
                .uuid(UUID.randomUUID())
                .build();
        when(crewService.addCrew(any(), any(), any())).thenReturn(newCrew);

        //when
        ApplyActivityReqDto request = ApplyActivityReqDto.builder()
                .activityId("123ea182ffcd11edbe560242ac120002")
                .dayOfBirth("20010101")
                .email("kim@naver.com")
                .id1365("kim-id1365")
                .phoneNumber("01012345678")
                .transportation("자차 (카쉐어링불가능)")
                .question(null)
                .startPoint("서울시")
                .userId("831ea182ffcd11edbe560242ac120002")
                .privacyAgreement(true)
                .build();
        ApplyActivityResDto response = activityService.applyActivity(request);

        //then
        System.out.println("response : " + response);
        assertThat(response.getApplicationId()).isEqualTo(UUIDGenerator.changeUuidToString(newCrew.getUuid()));
        assertThat(mockActivity.getParticipants()).isEqualTo(2);
    }

    @Test
    @DisplayName("활동 지원 정원초과")
    void applyActivity_fail() {
        //given
        Activity mockActivity = Activity.builder()
                .quota(5)
                .participants(5)
                .uuid(UUIDGenerator.changeUuidFromString("123ea182ffcd11edbe560242ac120002"))
                .build();
        when(activityRepository.findByUuid(any())).thenReturn(Optional.ofNullable(mockActivity));

        OUser mockUser = OUser.builder()
                .id(123L)
                .uuid(UUIDGenerator.changeUuidFromString("831ea182ffcd11edbe560242ac120002"))
                .build();
        when(userRepository.findByUuid(any())).thenReturn(Optional.ofNullable(mockUser));

        //when
        ApplyActivityReqDto request = ApplyActivityReqDto.builder()
                .activityId("123ea182ffcd11edbe560242ac120002")
                .dayOfBirth("20010101")
                .email("kim@naver.com")
                .id1365("kim-id1365")
                .phoneNumber("01012345678")
                .transportation("자차 (카쉐어링불가능)")
                .question(null)
                .startPoint("서울시")
                .userId("831ea182ffcd11edbe560242ac120002")
                .privacyAgreement(true)
                .build();

        //then
        assertThrows(IllegalRequestException.class, () -> activityService.applyActivity(request));
    }

    private RegisterActivityReqDto getRegisterActivityRequest() {
        return RegisterActivityReqDto.builder()
                .userId("831ea182ffcd11edbe560242ac120002")
                .title("title")
                .location(new LocationDto("함덕", "제주시", 123.1, 123.1))
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
    }
}
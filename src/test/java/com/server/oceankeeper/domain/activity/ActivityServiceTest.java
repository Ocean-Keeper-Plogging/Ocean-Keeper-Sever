package com.server.oceankeeper.domain.activity;

import com.server.oceankeeper.domain.activity.dao.MyActivityDao;
import com.server.oceankeeper.domain.activity.dto.request.*;
import com.server.oceankeeper.domain.activity.dto.response.ApplyActivityResDto;
import com.server.oceankeeper.domain.activity.dto.response.MyScheduledActivityDto;
import com.server.oceankeeper.domain.activity.dto.response.RegisterActivityResDto;
import com.server.oceankeeper.domain.activity.entity.Activity;
import com.server.oceankeeper.domain.activity.entity.ActivityDetail;
import com.server.oceankeeper.domain.activity.entity.GarbageCategory;
import com.server.oceankeeper.domain.activity.entity.LocationTag;
import com.server.oceankeeper.domain.activity.repository.ActivityDetailRepository;
import com.server.oceankeeper.domain.activity.repository.ActivityRepository;
import com.server.oceankeeper.domain.activity.service.ActivityService;
import com.server.oceankeeper.domain.crew.entitiy.CrewRole;
import com.server.oceankeeper.domain.crew.entitiy.Crews;
import com.server.oceankeeper.domain.crew.repository.CrewRepository;
import com.server.oceankeeper.domain.crew.service.CrewService;
import com.server.oceankeeper.domain.user.entitiy.OUser;
import com.server.oceankeeper.domain.user.entitiy.UserRole;
import com.server.oceankeeper.domain.user.entitiy.UserStatus;
import com.server.oceankeeper.domain.user.repository.UserRepository;
import com.server.oceankeeper.dummy.DummyObject;
import com.server.oceankeeper.global.eventfilter.EventPublisher;
import com.server.oceankeeper.global.exception.DuplicatedResourceException;
import com.server.oceankeeper.global.exception.IdNotFoundException;
import com.server.oceankeeper.global.exception.IllegalRequestException;
import com.server.oceankeeper.util.TokenUtil;
import com.server.oceankeeper.util.UUIDGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
    @Mock
    private TokenUtil tokenUtil;
    @Mock
    private EventPublisher eventPublisher;

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

    private static long userId = 2L;

    private OUser createRandomUser() {
        return OUser.builder()
                .uuid(UUIDGenerator.createUuid())
                .id(userId++)
                .deviceToken("devicetoken" + createRandomString())
                .email("kim" + createRandomString() + "@naver.com")
                .providerId("providerId" + createRandomString())
                .provider("naver")
                .nickname("kim" + createRandomString())
                .createdAt(LocalDateTime.now().minusMinutes(30))
                .password("123")
                .role(UserRole.USER)
                .profile("profile@naver.com")
                .status(UserStatus.ACTIVE)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private String createRandomString() {
        int targetStringLength = 10;
        Random random = new Random();
        return random.ints('a', 'z' + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    @Test
    @DisplayName("활동 등록")
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
        assertThat(resultActivity.getRewards()).isEqualTo(request.getRewards());
        assertThat(resultActivity.getTitle()).isEqualTo(request.getTitle());
        assertThat(resultActivity.getLocation()).isEqualTo(request.getLocation().toEntity());
        assertThat(resultActivity.getParticipants()).isEqualTo(0);
    }

    @Test
    @DisplayName("활동 등록 시작시각이 모집시각보다 늦음")
    void registerActivity_startTime_error() {
        //given
        RegisterActivityReqDto request = RegisterActivityReqDto.builder()
                .userId("831ea182ffcd11edbe560242ac120002")
                .title("title")
                .location(new LocationDto("제주시", 123.1, 123.1))
                .transportation("카셰어링 연결 예정")
                .garbageCategory(GarbageCategory.COASTAL)
                .locationTag(LocationTag.EAST)
                .recruitStartAt(LocalDate.now())
                .recruitEndAt(LocalDate.now().plusDays(10))
                .startAt(LocalDateTime.now())
                .keeperIntroduction("hi")
                .activityStory("story")
                .quota(5)
                .programDetails("12시 집결")
                .preparation("긴 상하의")
                .rewards("점심제공")
                .etc("오세요")
                .build();
        ;

        //when
        //then
        assertThrows(IllegalRequestException.class, () -> activityService.registerActivity(request));
    }

    @Test
    @DisplayName("활동 등록, 모집시작보다 모집끝이 더 빠름")
    void registerActivity_recruitTime_error() {
        //given
        RegisterActivityReqDto request = RegisterActivityReqDto.builder()
                .userId("831ea182ffcd11edbe560242ac120002")
                .title("title")
                .location(new LocationDto("제주시", 123.1, 123.1))
                .transportation("카셰어링 연결 예정")
                .garbageCategory(GarbageCategory.COASTAL)
                .locationTag(LocationTag.EAST)
                .recruitStartAt(LocalDate.now().plusDays(10))
                .recruitEndAt(LocalDate.now())
                .startAt(LocalDateTime.now().plusDays(10))
                .keeperIntroduction("hi")
                .activityStory("story")
                .quota(5)
                .programDetails("12시 집결")
                .preparation("긴 상하의")
                .rewards("점심제공")
                .etc("오세요")
                .build();
        ;

        //when
        //then
        assertThrows(IllegalRequestException.class, () -> activityService.registerActivity(request));
    }

    @Test
    @DisplayName("활동 모집 취소")
    void cancelActivity() {
        //given
        RegisterActivityReqDto request = getRegisterActivityRequest();
        Activity expectActivity = request.toActivityEntity();
        ActivityDetail expectActivityDetail = request.toActivityDetailEntity();
        OUser expectUser = createUser();
        Crews host = getHost(expectActivity, expectUser);
        List<Crews> crews = List.of(getCrew(expectActivity, createRandomUser()),
                getCrew(expectActivity, createRandomUser()),
                getCrew(expectActivity, createRandomUser()));
        when(activityRepository.findByUuid(any())).thenReturn(Optional.ofNullable(expectActivity));
        when(crewService.findApplication(any(), (Activity) any())).thenReturn(host);
        when(crewService.findCrews(any(Activity.class))).thenReturn(crews);
        when(activityDetailRepository.findByActivity(any())).thenReturn(Optional.ofNullable(expectActivityDetail));
        when(tokenUtil.getUserFromHeader(any())).thenReturn(expectUser);

        //when
        //then
        activityService.cancelActivity(UUIDGenerator.changeUuidToString(expectActivity.getUuid()), new MockHttpServletRequest());
        assertThat(expectActivityDetail.getActivityStory()).isNull();
        assertThat(expectActivityDetail.getStoryImage()).isNull();
        assertThat(expectActivityDetail.getKeeperImage()).isNull();
        assertThat(expectActivityDetail.getKeeperIntroduction()).isNull();
        assertThat(expectActivityDetail.getProgramDetails()).isNull();
        assertThat(expectActivityDetail.getPreparation()).isNull();
        assertThat(expectActivityDetail.getTransportation()).isNull();
        assertThat(expectActivity.getRewards()).isNull();
        assertThat(expectActivityDetail.getEtc()).isNull();

        //verify(publisher, atLeast(2)).publishEvent(any());
    }

    private ActivityDetail emptyActivityDetail(Activity activity) {
        return new ActivityDetail(
                null,
                activity, UUID.randomUUID(),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null);
    }

    private Crews getHost(Activity activity, OUser user) {
        return Crews.builder()
                .user(user)
                .activity(activity)
                .activityRole(CrewRole.HOST)
                .uuid(UUID.randomUUID())
                .createdAt(LocalDateTime.now().minusMinutes(10))
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private Crews getCrew(Activity activity, OUser user) {
        return Crews.builder()
                .user(user)
                .activity(activity)
                .activityRole(CrewRole.CREW)
                .uuid(UUID.randomUUID())
                .createdAt(LocalDateTime.now().minusMinutes(5))
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("활동 상세 조회")
    void getActivityDetail() {
        //given
        RegisterActivityReqDto request = getRegisterActivityRequest();
        Activity expectActivity = request.toActivityEntity();
        ActivityDetail expectActivityDetail = request.toActivityDetailEntity();
        expectActivity.addParticipant(); //fake 참여자 넣기
        OUser expectUser = newMockUser(155L, "hostkim", "naver", "naver123", UUID.randomUUID());

        //when
        when(activityRepository.findByUuid(any())).thenReturn(Optional.of(expectActivity));
        when(activityDetailRepository.findByActivity(any())).thenReturn(Optional.of(expectActivityDetail));
        when(crewService.findOwner(any())).thenReturn(expectUser);

        //then
        var result = activityService.getActivityDetail(UUIDGenerator.changeUuidToString(expectActivity.getUuid()));

        assertThat(result.getActivityStory()).isEqualTo(request.getActivityStory());
        assertThat(result.getEtc()).isEqualTo(request.getEtc());
        assertThat(result.getGarbageCategory()).isEqualTo(request.getGarbageCategory());
        assertThat(result.getKeeperIntroduction()).isEqualTo(request.getKeeperIntroduction());
        assertThat(result.getLocationTag()).isEqualTo(request.getLocationTag());
        assertThat(result.getPreparation()).isEqualTo(request.getPreparation());
        assertThat(result.getProgramDetails()).isEqualTo(request.getProgramDetails());
        assertThat(result.getQuota()).isEqualTo(request.getQuota());
        assertThat(result.getRecruitStartAt()).isEqualTo(request.getRecruitStartAt());
        assertThat(result.getRecruitEndAt()).isEqualTo(request.getRecruitEndAt());
        assertThat(result.getStartAt()).isEqualTo(request.getStartAt());
        assertThat(result.getRewards()).isEqualTo(request.getRewards());
        assertThat(result.getTitle()).isEqualTo(request.getTitle());
        assertThat(result.getLocation()).isEqualTo(request.getLocation());
        assertThat(result.getParticipants()).isEqualTo(1);
        assertThat(result.getHostNickName()).isEqualTo(expectUser.getNickname());
        assertThat(result.getHostImageUrl()).isEqualTo(expectUser.getProfile());
    }

    @Test
    @DisplayName("활동 수정")
    void modifyActivity() {
        //given
        ModifyActivityReqDto request = getModifyActivityRequest();
        Activity expectActivity = request.toActivityEntity();
        ActivityDetail expectActivityDetail = request.toActivityDetailEntity();
        OUser expectUser = createUser();
        when(activityRepository.findByUuid(any())).thenReturn(Optional.of(expectActivity));
        when(activityDetailRepository.findByActivity(any())).thenReturn(Optional.of(expectActivityDetail));
        when(crewService.findOwner(any())).thenReturn(expectUser);
        when(tokenUtil.getUserFromHeader(any())).thenReturn(expectUser);
        String activityId = UUIDGenerator.changeUuidToString(expectActivity.getUuid());

        //when
        //then
        activityService.modifyActivity(activityId, request, new MockHttpServletRequest());
    }

    private Crews createCrew(Activity expectActivity, OUser expectUser) {
        return Crews.builder()
                .activity(expectActivity)
                .user(expectUser)
                .build();
    }

    @Test
    @DisplayName("registerActivity_fail_유저못찾음")
    void registerActivityFailNoFoundUser() {
        //given
        RegisterActivityReqDto request = getRegisterActivityRequest();
        when(userRepository.findByUuid(any())).thenThrow(IdNotFoundException.class);

        //then
        assertThrows(IdNotFoundException.class, () -> activityService.registerActivity(request));
    }

    @Test
    @DisplayName("나의 다가오는 일정 찾기")
    void getMyActivity() {
        //given
        final OUser mockUser = newMockUser(123L, "kim", "naver", "providerid1", UUID.randomUUID());
        final String uuid1 = UUIDGenerator.changeUuidToString(UUIDGenerator.createUuid());
        final String uuid2 = UUIDGenerator.changeUuidToString(UUIDGenerator.createUuid());
        final LocalDateTime currentTime = LocalDateTime.now();
        List<MyScheduledActivityDto> expectResult = Arrays.asList(
                MyScheduledActivityDto.builder()
                        .id(uuid1)
                        .location("제주")
                        .dDay(5)
                        .startDay(ReflectionTestUtils.invokeMethod(activityService, "getStartDay", currentTime.plusDays(5)))
                        .title("테스트")
                        .build(),
                MyScheduledActivityDto.builder()
                        .id(uuid2)
                        .location("제주2")
                        .dDay(6)
                        .startDay(ReflectionTestUtils.invokeMethod(activityService, "getStartDay", currentTime.plusDays(6)))
                        .title("테스트2")
                        .build()
        );
        List<MyActivityDao> mockMyActivity = Arrays.asList(
                MyActivityDao.builder()
                        .uuid(UUIDGenerator.changeUuidFromString(uuid1))
                        .address("제주")
                        .startAt(currentTime.plusDays(5))
                        .title("테스트")
                        .build(),
                MyActivityDao.builder()
                        .uuid(UUIDGenerator.changeUuidFromString(uuid2))
                        .address("제주2")
                        .startAt(currentTime.plusDays(6))
                        .title("테스트2")
                        .build()
        );
        when(activityRepository.getMyActivitiesLimit5(any())).thenReturn(mockMyActivity);

        //when
        List<MyScheduledActivityDto> result = activityService.getMyScheduleActivity(UUIDGenerator.changeUuidToString(mockUser.getUuid()));
        assertThat(result).isEqualTo(expectResult);
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
                .nickname("nickname")
                .provider("provider")
                .providerId("providerid")
                .uuid(UUIDGenerator.changeUuidFromString("831ea182ffcd11edbe560242ac120002"))
                .build();
        when(userRepository.findByUuid(any())).thenReturn(Optional.ofNullable(mockUser));

        Crews newCrew = Crews.builder()
                .id(10L)
                .uuid(UUID.randomUUID())
                .build();
        when(crewService.addCrew(any(), any(), any(), any())).thenReturn(newCrew);

        //when
        ApplyApplicationReqDto request = getApplicationRequest();
        ApplyActivityResDto response = activityService.applyActivity(request);

        //then
        System.out.println("response : " + response);
        assertThat(response.getApplicationId()).isEqualTo(UUIDGenerator.changeUuidToString(newCrew.getUuid()));
        assertThat(mockActivity.getParticipants()).isEqualTo(2);
        //verify(publisher, atLeast(1)).publishEvent(any());
    }

    @Test
    @DisplayName("활동 지원서 수정")
    void modifyApplication() {
        //given
        ModifyApplicationReqDto request = getModifyApplicationRequest();
        RegisterActivityReqDto mockActivity = getRegisterActivityRequest();
        Activity expectActivity = mockActivity.toActivityEntity();

        OUser expectUser = createUser();
        Crews expectedCrew = createCrew(expectActivity, expectUser);

        when(crewService.findApplication(any(), (String) any())).thenReturn(expectedCrew);
        when(tokenUtil.getUserFromHeader(any())).thenReturn(expectUser);
        String activityId = UUIDGenerator.changeUuidToString(expectActivity.getUuid());

        //when
        //then
        activityService.modifyApplication(activityId, request, new MockHttpServletRequest());
    }

    private ApplyApplicationReqDto getApplicationRequest() {
        return ApplyApplicationReqDto.builder()
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
    }

    @Test
    @DisplayName("이미 지원한 활동에 지원")
    void applyActivity_fail_apply_again() {
        //given
        Activity mockActivity = Activity.builder()
                .quota(5)
                .participants(3)
                .uuid(UUIDGenerator.changeUuidFromString("123ea182ffcd11edbe560242ac120002"))
                .build();
        when(activityRepository.findByUuid(any())).thenReturn(Optional.ofNullable(mockActivity));

        OUser mockUser = OUser.builder()
                .id(123L)
                .nickname("nickname")
                .provider("provider")
                .providerId("providerid")
                .uuid(UUIDGenerator.changeUuidFromString("831ea182ffcd11edbe560242ac120002"))
                .build();
        when(userRepository.findByUuid(any())).thenReturn(Optional.ofNullable(mockUser));

        ApplyApplicationReqDto request = getApplicationRequest();
        Crews newCrew = Crews.builder()
                .id(10L)
                .uuid(UUID.randomUUID())
                .build();
        when(crewService.addCrew(any(), any(), any(), any())).thenReturn(newCrew);
        activityService.applyActivity(request);

        //when
        when(crewService.existCrew(any(), any())).thenReturn(true);

        //then
        assertThrows(DuplicatedResourceException.class, () -> activityService.applyActivity(request));
    }

    @Test
    @DisplayName("지원하였으나 취소할 경우 문제 없이 취소된다")
    void cancelApplication_success() {
        //given
        Activity mockActivity = Activity.builder()
                .quota(5)
                .participants(3)
                .uuid(UUIDGenerator.changeUuidFromString("123ea182ffcd11edbe560242ac120002"))
                .build();
        when(activityRepository.findByUuid(any())).thenReturn(Optional.ofNullable(mockActivity));

        OUser mockUser = OUser.builder()
                .id(123L)
                .nickname("nickname")
                .provider("provider")
                .providerId("providerid")
                .uuid(UUIDGenerator.changeUuidFromString("831ea182ffcd11edbe560242ac120002"))
                .build();
        when(userRepository.findByUuid(any())).thenReturn(Optional.ofNullable(mockUser));

        ApplyApplicationReqDto request = getApplicationRequest();
        Crews newCrew = Crews.builder()
                .activity(mockActivity)
                .user(mockUser)
                .id(10L)
                .uuid(UUID.randomUUID())
                .build();
        when(crewService.addCrew(any(), any(), any(), any())).thenReturn(newCrew);
        ApplyActivityResDto result = activityService.applyActivity(request);
        when(crewService.findApplication(any(), anyString())).thenReturn(newCrew);
        when(tokenUtil.getUserFromHeader(any())).thenReturn(mockUser);

        //when
        activityService.cancelApplication(result.getApplicationId(), new MockHttpServletRequest());

        //then
        assertThat(mockActivity.getParticipants()).isEqualTo(3); //하나 줄어듦
        //verify(publisher, atLeast(1)).publishEvent(any());
    }

    @Test
    @DisplayName("본인의 것이 아닌 다른 사람의 활동을 삭제할 경우 예외가 발생한다.")
    void cancelApplication_fail() {
        //given
        when(crewService.findApplication(any(), anyString())).thenThrow(IdNotFoundException.class);
        OUser fakeUser = OUser.builder()
                .id(125L)
                .uuid(UUIDGenerator.changeUuidFromString("831ea182ffcd11edbe560242ac121112"))
                .build();
        when(tokenUtil.getUserFromHeader(any())).thenReturn(fakeUser);

        //when
        //then
        assertThrows(IdNotFoundException.class,
                () -> activityService.cancelApplication(UUIDGenerator.changeUuidToString(UUID.randomUUID()), new MockHttpServletRequest()));
    }

    private RegisterActivityReqDto getRegisterActivityRequest() {
        return RegisterActivityReqDto.builder()
                .userId("831ea182ffcd11edbe560242ac120002")
                .title("title")
                .location(new LocationDto("제주시", 123.1, 123.1))
                .transportation("카셰어링 연결 예정")
                .garbageCategory(GarbageCategory.COASTAL)
                .locationTag(LocationTag.EAST)
                .recruitStartAt(LocalDate.now())
                .recruitEndAt(LocalDate.now().plusDays(5))
                .startAt(LocalDateTime.now().plusDays(15))
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

    private ModifyActivityReqDto getModifyActivityRequest() {
        return ModifyActivityReqDto.builder()
                .title("title2")
                .location(new LocationDto("제주시", 234.2, 123.1))
                .transportation("자차")
                .garbageCategory(GarbageCategory.COASTAL)
                .locationTag(LocationTag.EAST)
                .recruitStartAt(LocalDate.now())
                .recruitEndAt(LocalDate.now().plusDays(5))
                .startAt(LocalDateTime.now().plusDays(15))
                //.thumbnailUrl("")
                .keeperIntroduction("hi")
                //.keeperImageUrl("")
                .activityStory("new story")
                //.storyImageUrl("")
                .quota(5)
                .programDetails("12시 집결")
                .preparation("긴 상하의")
                .rewards("점심제공")
                .etc("오세요")
                .build();
    }

    private ModifyApplicationReqDto getModifyApplicationRequest() {
        return ModifyApplicationReqDto.builder()
                .dayOfBirth("20010101")
                .email("kim@naver.com")
                .id1365("kim-id1365")
                .phoneNumber("01012345678")
                .transportation("자차 (카쉐어링가능)")
                .question(null)
                .startPoint("경기도")
                .build();
    }
}
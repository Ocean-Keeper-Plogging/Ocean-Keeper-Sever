package com.server.oceankeeper.domain.activity;

import com.amazonaws.services.s3.AmazonS3;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.oceankeeper.domain.activity.dto.request.ApplyApplicationReqDto;
import com.server.oceankeeper.domain.activity.dto.request.LocationDto;
import com.server.oceankeeper.domain.activity.dto.request.RegisterActivityReqDto;
import com.server.oceankeeper.domain.activity.dto.response.MyScheduledActivityDto;
import com.server.oceankeeper.domain.activity.dto.response.RegisterActivityResDto;
import com.server.oceankeeper.domain.activity.entity.Activity;
import com.server.oceankeeper.domain.activity.entity.ActivityDetail;
import com.server.oceankeeper.domain.activity.entity.GarbageCategory;
import com.server.oceankeeper.domain.activity.entity.LocationTag;
import com.server.oceankeeper.domain.activity.repository.ActivityDetailRepository;
import com.server.oceankeeper.domain.activity.repository.ActivityRepository;
import com.server.oceankeeper.domain.activity.service.ActivityMessageFacadeService;
import com.server.oceankeeper.domain.activity.service.ActivityService;
import com.server.oceankeeper.domain.crew.repository.CrewRepository;
import com.server.oceankeeper.domain.scheduler.config.QuartzConfig;
import com.server.oceankeeper.domain.scheduler.service.SchedulerService;
import com.server.oceankeeper.domain.statistics.dto.ActivityInfoResDto;
import com.server.oceankeeper.domain.statistics.repository.ActivityInfoRepository;
import com.server.oceankeeper.domain.statistics.service.ActivityInfoService;
import com.server.oceankeeper.domain.user.entitiy.OUser;
import com.server.oceankeeper.domain.user.repository.UserRepository;
import com.server.oceankeeper.dummy.DummyObject;
import com.server.oceankeeper.global.config.AwsS3Config;
import com.server.oceankeeper.global.exception.IllegalRequestException;
import com.server.oceankeeper.util.S3Util;
import com.server.oceankeeper.util.UUIDGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Disabled("s3 mocking error")
public class ActivityIntegrationTest extends DummyObject {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper om;
    @Autowired
    private ActivityRepository activityRepository;
    @Autowired
    private ActivityService activityService;
    @Autowired
    private ActivityMessageFacadeService activityMessageService;

    @Mock
    private AwsS3Config awsS3Config;
    @Mock
    private AmazonS3 amazonS3;
    @MockBean
    private S3Util s3Util;
    @MockBean
    private QuartzConfig quartzConfig;
    @MockBean
    private SchedulerFactoryBean schedulerFactoryBean;
    @MockBean
    private SchedulerService schedulerService;

    @Autowired
    private ActivityDetailRepository activityDetailRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ActivityInfoService activityInfoService;
    @Autowired
    private ActivityInfoRepository activityInfoRepository;
    @Autowired
    private CrewRepository crewRepository;

    @BeforeEach
    public void init() {
        OUser kim = newUserWithR("kim", "provider", "provider-kim", UUID.randomUUID());
        OUser park = newUserWithR("park", "provider", "provider-park", UUID.randomUUID());
        OUser lee = newUserWithR("lee", "provider", "provider-lee", UUID.randomUUID());
        activityInfoRepository.save(newUserInfoWithR(kim));
        activityInfoRepository.save(newUserInfoWithR(park));
        activityInfoRepository.save(newUserInfoWithR(lee));

        userRepository.save(kim);
        userRepository.save(park);
        userRepository.save(lee);
    }

    @DisplayName("김이 활동을 생성하고, 다가오는 일정에 있는지 확인한다")
    @Test
    public void testCreateActivity() throws Exception {
        OUser user = userRepository.findByNickname("kim").get();
        RegisterActivityResDto kimActivity = createActivity(user, GarbageCategory.COASTAL, LocationTag.JEJU);

        List<MyScheduledActivityDto> kimScheduledActivity =
                activityService.getMyScheduleActivity(UUIDGenerator.changeUuidToString(user.getUuid()));

        ActivityInfoResDto activityInfoResDto = activityInfoService.getUserActivityInfo(user);

        List<Activity> activityList = activityRepository.findAll();
        List<ActivityDetail> activityDetailList = activityDetailRepository.findAll();
        long crewsCount = crewRepository.count();
        assertThat(activityList.size()).isEqualTo(1);
        assertThat(activityDetailList.size()).isEqualTo(1);
        assertThat(crewsCount).isEqualTo(1);
        assertThat(activityInfoResDto.getActivity()).isEqualTo(0);
        assertThat(activityInfoResDto.getHosting()).isEqualTo(1);
        assertThat(activityInfoResDto.getNoShow()).isEqualTo(0);

        assertThat(kimScheduledActivity.size()).isEqualTo(1);
        MyScheduledActivityDto expectedScheduledActivity =
                new MyScheduledActivityDto(
                        kimActivity.getActivityId(),
                        10,
                        "title",
                        ReflectionTestUtils.invokeMethod(activityService, "getStartDay", LocalDateTime.now().plusDays(10)),
                        "제주시");
        assertThat(kimScheduledActivity.get(0)).isEqualTo(expectedScheduledActivity);
    }

    @DisplayName("모집기간과 활동 시작기간이 맞지 않는 활동생성을 요청한다.")
    @Test
    public void testActivityValidator() throws Exception {
        OUser user = userRepository.findByNickname("kim").get();
        RegisterActivityReqDto request = RegisterActivityReqDto.builder()
                .userId(UUIDGenerator.changeUuidToString(user.getUuid()))
                .title("title")
                .location(new LocationDto("제주시", 123.1, 123.1))
                .transportation("카셰어링 연결 예정")
                .garbageCategory(GarbageCategory.COASTAL)
                .locationTag(LocationTag.EAST)
                .recruitStartAt(LocalDate.now())
                .recruitEndAt(LocalDate.now().plusDays(5))
                .startAt(LocalDateTime.now().plusDays(3))
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

        assertThrows(IllegalRequestException.class, () -> activityService.registerActivity(request));
    }

    @DisplayName("김이 생성한 활동에 이가 가입한다")
    @Test
    public void testApplyActivity() throws Exception {
        OUser kim = userRepository.findByNickname("kim").get();
        OUser lee = userRepository.findByNickname("lee").get();
        RegisterActivityResDto kimActivity = createActivity(kim, GarbageCategory.COASTAL, LocationTag.JEJU);
        createApplication(lee, kimActivity.getActivityId());

        ActivityInfoResDto activityInfoResDto = activityInfoService.getUserActivityInfo(lee);

        List<MyScheduledActivityDto> leeScheduledActivity =
                activityService.getMyScheduleActivity(UUIDGenerator.changeUuidToString(lee.getUuid()));

        long crewsCount = crewRepository.count();
        assertThat(crewsCount).isEqualTo(2);

        assertThat(activityInfoResDto.getActivity()).isEqualTo(1);
        assertThat(activityInfoResDto.getHosting()).isEqualTo(0);
        assertThat(activityInfoResDto.getNoShow()).isEqualTo(0);

        MyScheduledActivityDto expectedScheduledActivity =
                new MyScheduledActivityDto(
                        kimActivity.getActivityId(),
                        10,
                        "title",
                        ReflectionTestUtils.invokeMethod(activityService, "getStartDay", LocalDateTime.now().plusDays(10)),
                        "제주시");
        assertThat(leeScheduledActivity.get(0)).isEqualTo(expectedScheduledActivity);
    }

    private RegisterActivityResDto createActivity(OUser user, GarbageCategory category, LocationTag tag) throws JsonProcessingException {
        RegisterActivityReqDto request = RegisterActivityReqDto.builder()
                .userId(UUIDGenerator.changeUuidToString(user.getUuid()))
                .title("title")
                .location(new LocationDto("제주시", 123.1, 123.1))
                .transportation("카셰어링 연결 예정")
                .garbageCategory(category)
                .locationTag(tag)
                .recruitStartAt(LocalDate.now())
                .recruitEndAt(LocalDate.now().plusDays(5))
                .startAt(LocalDateTime.now().plusDays(10))
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

        return activityService.registerActivity(request);
    }

    private void createApplication(OUser user, String activityId) {
        ApplyApplicationReqDto request = ApplyApplicationReqDto.builder()
                .userId(UUIDGenerator.changeUuidToString(user.getUuid()))
                .activityId(activityId)
                .privacyAgreement(true)
                .dayOfBirth("20010101")
                .email("kim@naver.com")
                .phoneNumber("01012341234")
                .name("kim")
                .transportation("transportation")
                .id1365("id1365")
                .question("question")
                .startPoint("startpoint")
                .build();

        activityService.applyActivity(request);
    }
}

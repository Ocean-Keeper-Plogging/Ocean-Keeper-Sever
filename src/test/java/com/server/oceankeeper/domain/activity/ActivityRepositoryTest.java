package com.server.oceankeeper.domain.activity;

import com.server.oceankeeper.domain.activity.dao.ActivityDao;
import com.server.oceankeeper.domain.activity.dao.AllActivityDao;
import com.server.oceankeeper.domain.activity.dao.CrewInfoDetailDao;
import com.server.oceankeeper.domain.activity.dao.MyActivityDao;
import com.server.oceankeeper.domain.activity.entity.Activity;
import com.server.oceankeeper.domain.activity.entity.ActivityStatus;
import com.server.oceankeeper.domain.activity.entity.GarbageCategory;
import com.server.oceankeeper.domain.activity.entity.LocationTag;
import com.server.oceankeeper.domain.activity.repository.ActivityRepository;
import com.server.oceankeeper.domain.blockUser.entity.BlockUser;
import com.server.oceankeeper.domain.crew.entity.CrewRole;
import com.server.oceankeeper.domain.crew.entity.CrewStatus;
import com.server.oceankeeper.domain.crew.entity.Crews;
import com.server.oceankeeper.domain.crew.param.MyActivityParam;
import com.server.oceankeeper.domain.user.entity.OUser;
import com.server.oceankeeper.domain.user.repository.UserRepository;
import com.server.oceankeeper.dummy.DummyObject;
import com.server.oceankeeper.global.config.QuerydslConfig;
import com.server.oceankeeper.util.UUIDGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(QuerydslConfig.class)
@ActiveProfiles("test")
class ActivityRepositoryTest extends DummyObject {
    @Autowired
    private ActivityRepository activityRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EntityManager em;

    @BeforeEach
    void setUp() {
        setUpUserAndActivity();
    }

    protected void setUpUserAndActivity() {
        OUser kim = newUserWithR("kim", "naver", "kimproviderId", UUIDGenerator.changeUuidFromString("123ea182ffcd11edbe560242ac120001"));
        em.persist(kim);

        OUser lee = newUserWithR("lee", "naver", "leeproviderId", UUIDGenerator.changeUuidFromString("123ea182ffcd11edbe560242ac130001"));
        em.persist(lee);

        OUser park = newUserWithR("park", "naver", "parkproviderId", UUIDGenerator.changeUuidFromString("123ea182ffcd11edbe560242ac140001"));
        em.persist(park);

        OUser blockedUserFromLee = newUserWithR("block", "naver", "blockproviderId", UUIDGenerator.changeUuidFromString("555ea182ffcd11edbe560242ac150001"));
        em.persist(blockedUserFromLee);

        Activity activityFromKim = newMockActivity(kim, 5, ActivityStatus.OPEN, LocationTag.EAST, GarbageCategory.COASTAL, 10,
                UUIDGenerator.changeUuidFromString("123ea182ffcd11edbe560242ac120001"));
        em.persist(activityFromKim);
        Activity activityFromKim2 = newMockActivity(kim, 6, ActivityStatus.CLOSED, LocationTag.EAST, GarbageCategory.ETC, 5,
                UUIDGenerator.changeUuidFromString("123ea182ffcd11edbe560242ac120002"));
        em.persist(activityFromKim2);
        Activity activityFromKim3 = newMockActivity(kim, 15, ActivityStatus.CANCEL, LocationTag.JEJU, GarbageCategory.DEPOSITED, 10,
                UUIDGenerator.changeUuidFromString("123ea182ffcd11edbe560242ac120003"));
        em.persist(activityFromKim3);
        Activity activityFromKim4 = newMockActivity(kim, 25, ActivityStatus.OPEN, LocationTag.SOUTH, GarbageCategory.FLOATING, 10,
                UUIDGenerator.changeUuidFromString("123ea182ffcd11edbe560242ac120004"));
        em.persist(activityFromKim4);
        Activity activityFromKim5 = newMockActivity(kim, 35, ActivityStatus.OPEN, LocationTag.WEST, GarbageCategory.DEPOSITED, 10,
                UUIDGenerator.changeUuidFromString("123ea182ffcd11edbe560242ac120005"));
        em.persist(activityFromKim5);
        Activity activityFromKim6 = newMockActivity(kim, 35, ActivityStatus.CLOSED, LocationTag.WEST, GarbageCategory.DEPOSITED, -20,
                UUIDGenerator.changeUuidFromString("123ea182ffcd11edbe560242ac120006"));
        em.persist(activityFromKim6);
        Activity activityFromLee = newMockActivity(lee, 10, ActivityStatus.OPEN, LocationTag.JEJU, GarbageCategory.FLOATING, 10,
                UUIDGenerator.changeUuidFromString("123ea182ffcd11edbe560242ac120011"));
        em.persist(activityFromLee);
        Activity activityFromPark = newMockActivity(park, 11, ActivityStatus.OPEN, LocationTag.WEST, GarbageCategory.ETC, 10,
                UUIDGenerator.changeUuidFromString("123ea182ffcd11edbe560242ac120021"));
        em.persist(activityFromPark);
        Activity activityFromPark2 = newMockActivity(park, 10, ActivityStatus.CANCEL, LocationTag.ETC, GarbageCategory.FLOATING, 5,
                UUIDGenerator.changeUuidFromString("123ea182ffcd11edbe560242ac120022"));
        em.persist(activityFromPark2);

        Activity activityFromBlock = newMockActivity(blockedUserFromLee, 20, ActivityStatus.OPEN, LocationTag.JEJU, GarbageCategory.FLOATING, 10,
                UUIDGenerator.changeUuidFromString("5555a182ffcd11edbe560242ac120011"));
        em.persist(activityFromBlock);
        Activity activityFromBlock2 = newMockActivity(blockedUserFromLee, 31, ActivityStatus.RECRUITMENT_CLOSE, LocationTag.WEST, GarbageCategory.ETC, 0,
                UUIDGenerator.changeUuidFromString("5555a182ffcd11edbe560242ac120021"));
        em.persist(activityFromBlock2);
        Activity activityFromBlock3 = newMockActivity(blockedUserFromLee, 40, ActivityStatus.CLOSED, LocationTag.ETC, GarbageCategory.FLOATING, 0,
                UUIDGenerator.changeUuidFromString("5555a182ffcd11edbe560242ac120022"));
        em.persist(activityFromBlock3);

        BlockUser blocked = new BlockUser(null, lee, blockedUserFromLee, LocalDateTime.now(), LocalDateTime.now());
        lee.addBlockedUser(blocked);
        em.persist(blocked);

        Crews crews = newCrew(activityFromKim, kim, kim, CrewStatus.IN_PROGRESS, CrewRole.HOST);
        Crews crews1 = newCrew(activityFromKim2, kim, kim, CrewStatus.CLOSED, CrewRole.HOST);
        Crews crews12 = newCrew(activityFromKim3, kim, kim, CrewStatus.CANCEL, CrewRole.HOST);
        Crews crews13 = newCrew(activityFromKim4, kim, kim, CrewStatus.IN_PROGRESS, CrewRole.HOST);
        Crews crews14 = newCrew(activityFromKim5, kim, kim, CrewStatus.IN_PROGRESS, CrewRole.HOST);
        Crews crews16 = newCrew(activityFromKim6, kim, kim, CrewStatus.CLOSED, CrewRole.HOST);
        Crews crews26 = newCrew(activityFromKim6, lee, kim, CrewStatus.CLOSED, CrewRole.CREW);
        Crews crews36 = newCrew(activityFromKim6, park, kim, CrewStatus.CLOSED, CrewRole.CREW);

        Crews crews2 = newCrew(activityFromLee, lee, lee, CrewStatus.IN_PROGRESS, CrewRole.HOST);
        Crews crews3 = newCrew(activityFromPark, park, park, CrewStatus.IN_PROGRESS, CrewRole.HOST);
        Crews crews6 = newCrew(activityFromPark2, park, park, CrewStatus.CANCEL, CrewRole.HOST);

        Crews crews4 = newCrew(activityFromLee, kim, lee, CrewStatus.REJECT, CrewRole.CREW);
        Crews crews5 = newCrew(activityFromPark, kim, park, CrewStatus.NO_SHOW, CrewRole.CREW);
        Crews crews62 = newCrew(activityFromPark2, kim, park, CrewStatus.CANCEL, CrewRole.CREW);

        em.persist(crews);
        em.persist(crews1);
        em.persist(crews12);
        em.persist(crews13);
        em.persist(crews14);
        em.persist(crews16);
        em.persist(crews26);
        em.persist(crews36);
        em.persist(crews2);
        em.persist(crews3);
        em.persist(crews4);
        em.persist(crews5);
        em.persist(crews6);
        em.persist(crews62);


    }

    @Test
    @DisplayName("현재 날짜 기준으로 열린 일정 탑 5 조회")
    void test() {
        //given
        MyActivityParam param = new MyActivityParam(LocalDateTime.now(),
                UUIDGenerator.changeUuidFromString("123ea182ffcd11edbe560242ac120001")); //kim
        List<MyActivityDao> result = activityRepository.getMyActivitiesLimit5(param);

        MyActivityParam param2 = new MyActivityParam(LocalDateTime.now(),
                UUIDGenerator.changeUuidFromString("123ea182ffcd11edbe560242ac130001")); //lee
        List<MyActivityDao> result2 = activityRepository.getMyActivitiesLimit5(param2);

        MyActivityParam param3 = new MyActivityParam(LocalDateTime.now(),
                UUIDGenerator.changeUuidFromString("123ea182ffcd11edbe560242ac140001")); //park
        List<MyActivityDao> result3 = activityRepository.getMyActivitiesLimit5(param3);

        assertThat(result.size()).isEqualTo(5);
        assertThat(result2.size()).isEqualTo(1);
        assertThat(result3.size()).isEqualTo(1);

        assertThat(result2.get(0).getUuid()).isEqualTo(UUIDGenerator.changeUuidFromString("123ea182ffcd11edbe560242ac120011"));
        assertThat(result3.get(0).getUuid()).isEqualTo(UUIDGenerator.changeUuidFromString("123ea182ffcd11edbe560242ac120021"));
        System.out.println(result);
    }

    @Test
    @DisplayName("닫힌 활동은 조회되지 않아야한다.")
    void testClosedMyActivity() {
        //given
        MyActivityParam param = new MyActivityParam(LocalDateTime.now().plusDays(11),
                UUIDGenerator.changeUuidFromString("123ea182ffcd11edbe560242ac120001")); //kim
        List<MyActivityDao> result = activityRepository.getMyActivitiesLimit5(param);

        assertThat(result.size()).isEqualTo(0);

        System.out.println(result);
    }

    @Test
    @DisplayName("김이 속한 활동 중 열려있는 2개만 조회한다")
    void testFindMyActivitiesBasicFeature() throws Exception {
        //Given

        //When
        Slice<ActivityDao> result = activityRepository
                .getMyActivitiesWithoutCancel(UUIDGenerator.changeUuidFromString("123ea182ffcd11edbe560242ac120001"),
                        null, ActivityStatus.OPEN, null, LocalDateTime.now().plusDays(5), Pageable.ofSize(2));

        //Then
        assertThat(result.getContent().size()).isEqualTo(2);
        assertThat(result.getContent().get(0).getActivityId()).isEqualTo(UUIDGenerator.changeUuidFromString("123ea182ffcd11edbe560242ac120021"));
        assertThat(result.getContent().get(1).getActivityId()).isEqualTo(UUIDGenerator.changeUuidFromString("123ea182ffcd11edbe560242ac120011"));
    }

    @Test
    @DisplayName("김이 속한 활동 중 activity id를 기준으로 1개만 조회한다")
    void testPageable() throws Exception {
        //Given

        //When
        Slice<ActivityDao> result = activityRepository
                .getMyActivitiesWithoutCancel(UUIDGenerator.changeUuidFromString("123ea182ffcd11edbe560242ac120001"),
                        UUIDGenerator.changeUuidFromString("123ea182ffcd11edbe560242ac120021"),
                        ActivityStatus.OPEN, null, LocalDateTime.now().plusDays(5), Pageable.ofSize(1));

        //Then
        assertThat(result.getContent().size()).isEqualTo(1);
        assertThat(result.getContent().get(0).getActivityId())
                .isEqualTo(UUIDGenerator.changeUuidFromString("123ea182ffcd11edbe560242ac120011"));
    }

    @Test
    @DisplayName("김이 속한 활동 중 종료된 활동을 조회한다")
    void selectEndActivities() throws Exception {
        //Given

        //When
        Slice<ActivityDao> result = activityRepository
                .getMyActivitiesWithoutCancel(UUIDGenerator.changeUuidFromString("123ea182ffcd11edbe560242ac120001"),
                        null, ActivityStatus.CLOSED, null, LocalDateTime.now().plusDays(6), Pageable.ofSize(5));

        //Then
        assertThat(result.getContent().size()).isEqualTo(2);
        assertThat(result.getContent().get(0).getActivityId())
                .isEqualTo(UUIDGenerator.changeUuidFromString("123ea182ffcd11edbe560242ac120006"));
    }

    @Test
    @DisplayName("김이 속한 전체 활동 중 열려있는 활동을 조회한다")
    void testCrewOpenActivities() throws Exception {
        //Given

        //When
        Slice<ActivityDao> result = activityRepository
                .getMyActivitiesWithoutCancel(UUIDGenerator.changeUuidFromString("123ea182ffcd11edbe560242ac120001"),
                        null, ActivityStatus.OPEN, null, LocalDateTime.now().plusDays(9), Pageable.ofSize(15));

        //Then
        assertThat(result.getContent().size()).isEqualTo(5);
        assertThat(result.getContent().get(0).getActivityId())
                .isEqualTo(UUIDGenerator.changeUuidFromString("123ea182ffcd11edbe560242ac120021"));
        assertThat(result.getContent().get(1).getActivityId())
                .isEqualTo(UUIDGenerator.changeUuidFromString("123ea182ffcd11edbe560242ac120011"));
    }

    @Test
    @DisplayName("김이 속한 활동 중 취소한 활동을 제외하고 조회한다")
    void testCrewActivities() throws Exception {
        //Given

        //When
        Slice<ActivityDao> result = activityRepository
                .getMyActivitiesWithoutCancel(UUIDGenerator.changeUuidFromString("123ea182ffcd11edbe560242ac120001"),
                        null, null, null, null, Pageable.ofSize(15));

        //Then
        assertThat(result.getContent().size()).isEqualTo(7);
        assertThat(result.getContent().get(0).getActivityId())
                .isEqualTo(UUIDGenerator.changeUuidFromString("123ea182ffcd11edbe560242ac120021"));
        assertThat(result.getContent().get(1).getActivityId())
                .isEqualTo(UUIDGenerator.changeUuidFromString("123ea182ffcd11edbe560242ac120011"));
    }

    @Test
    @DisplayName("앱 내 전체 활동 중 열린 활동 조회한다")
    void testFindScheduleActivities() throws Exception {
        //Given
        OUser kim = userRepository.findByNickname("kim").get();

        //When
        Slice<AllActivityDao> result = activityRepository
                .getAllActivities(null, ActivityStatus.OPEN, null, null, Pageable.ofSize(15), kim);

        //Then
        System.out.println("result = " + result.getContent());
        assertThat(result.getContent().size()).isEqualTo(6);
        assertThat(result.getContent().get(0).getActivityId())
                .isEqualTo(UUIDGenerator.changeUuidFromString("5555a182ffcd11edbe560242ac120011"));
        assertThat(result.getContent().get(0).getHostNickname()).isEqualTo("block");

        assertThat(result.getContent().get(1).getActivityId())
                .isEqualTo(UUIDGenerator.changeUuidFromString("123ea182ffcd11edbe560242ac120021"));
        assertThat(result.getContent().get(1).getHostNickname()).isEqualTo("park");
        assertThat(result.getContent().get(2).getActivityId())
                .isEqualTo(UUIDGenerator.changeUuidFromString("123ea182ffcd11edbe560242ac120011"));
        assertThat(result.getContent().get(2).getHostNickname()).isEqualTo("lee");
        assertThat(result.stream().map(AllActivityDao::getActivityId).collect(Collectors.toList())).isSortedAccordingTo(Comparator.reverseOrder());
    }

    @Test
    @DisplayName("킴 입장에서 앱 내 전체 활동 중 닫힌 활동 조회한다")
    void findClosedActivitiesFromKim() throws Exception {
        //Given
        OUser kim = userRepository.findByNickname("kim").get();

        //When
        Slice<AllActivityDao> result = activityRepository
                .getAllActivities(null, ActivityStatus.CLOSED, null, null, Pageable.ofSize(15), kim);

        //Then
        System.out.println("result = " + result.getContent());
        assertThat(result.getContent().size()).isEqualTo(3);
        assertThat(result.getContent().get(0).getActivityId())
                .isEqualTo(UUIDGenerator.changeUuidFromString("5555a182ffcd11edbe560242ac120022"));
        assertThat(result.stream().map(AllActivityDao::getActivityId).collect(Collectors.toList())).isSortedAccordingTo(Comparator.reverseOrder());
    }

    @Test
    @DisplayName("유저를 블락한 이 입장에서 앱 내 전체 활동 중 닫힌 활동 조회한다")
    void findClosedActivitiesFromBlocker() throws Exception {
        //Given
        OUser lee = userRepository.findByNickname("lee").get();

        //When
        Slice<AllActivityDao> result = activityRepository
                .getAllActivities(null, ActivityStatus.CLOSED, null, null, Pageable.ofSize(15), lee);

        //Then
        System.out.println("result = " + result.getContent());
        assertThat(result.getContent().size()).isEqualTo(2);
        assertThat(result.getContent().get(0).getActivityId())
                .isEqualTo(UUIDGenerator.changeUuidFromString("123ea182ffcd11edbe560242ac120006"));
        assertThat(result.stream().map(AllActivityDao::getActivityId).collect(Collectors.toList())).isSortedAccordingTo(Comparator.reverseOrder());
    }

    @Test
    @DisplayName("앱 내 전체 활동 중 특정 쓰레기타입을 조회한다")
    void testFindScheduleActivitiesByGarbage() throws Exception {
        //Given
        OUser kim = userRepository.findByNickname("kim").get();
        //When
        Slice<AllActivityDao> result = activityRepository
                .getAllActivities(null, null, null, GarbageCategory.COASTAL, Pageable.ofSize(15), kim);

        //Then
        System.out.println("result = " + result.getContent());
        assertThat(result.getContent().size()).isEqualTo(1);
        assertThat(result.getContent().get(0).getActivityId())
                .isEqualTo(UUIDGenerator.changeUuidFromString("123ea182ffcd11edbe560242ac120001"));
    }

    @Test
    @DisplayName("앱 내 전체 활동 중 특정 위치 활동 조회한다")
    void testFindScheduleActivitiesByLocationTag() throws Exception {
        //Given
        OUser kim = userRepository.findByNickname("kim").get();
        //When
        Slice<AllActivityDao> result = activityRepository
                .getAllActivities(null, null, LocationTag.EAST, null, Pageable.ofSize(15), kim);

        //Then
        System.out.println("result = " + result.getContent());
        assertThat(result.getContent().size()).isEqualTo(2);
        assertThat(result.getContent().get(0).getActivityId())
                .isEqualTo(UUIDGenerator.changeUuidFromString("123ea182ffcd11edbe560242ac120002"));
        assertThat(result.getContent().get(1).getActivityId())
                .isEqualTo(UUIDGenerator.changeUuidFromString("123ea182ffcd11edbe560242ac120001"));
    }

    @Test
    @DisplayName("신청자 리스트를 조회한다")
    void testFindCrewInfo() {
        //given

        //when
        List<CrewInfoDetailDao> result = activityRepository
                .getCrewInfo(UUIDGenerator.changeUuidFromString("123ea182ffcd11edbe560242ac120021")); //activity from park

        //then
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getCrewStatus()).isEqualTo(CrewStatus.NO_SHOW);
    }

    @Test
    @DisplayName("기한이 지난 크루정보는 삭제한다.")
    void testDeleteClosedCrewInfo() {
        //given
        //when
        long result = activityRepository
                .selectByCrewStatusAndStartAtAndUpdateCrewStatusAsDeleted(CrewStatus.CLOSED, 10);
        //then
        assertThat(result).isEqualTo(3);
    }

    @Test
    @DisplayName("기한이 지나지 않은 크루정보는 삭제하지 않는다.")
    void testNotDeleteNotClosedCrewInfo() {
        //given
        //when
        long result = activityRepository
                .selectByCrewStatusAndStartAtAndUpdateCrewStatusAsDeleted(CrewStatus.CLOSED, 30);
        //then
        assertThat(result).isEqualTo(0);
    }
}
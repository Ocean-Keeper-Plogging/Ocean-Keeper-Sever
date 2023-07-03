package com.server.oceankeeper.domain.activity;

import com.server.oceankeeper.domain.activity.dto.ActivityDao;
import com.server.oceankeeper.domain.activity.dto.MyActivityDao;
import com.server.oceankeeper.domain.activity.dto.ScheduledActivityDao;
import com.server.oceankeeper.domain.activity.entity.Activity;
import com.server.oceankeeper.domain.activity.entity.ActivityStatus;
import com.server.oceankeeper.domain.activity.entity.GarbageCategory;
import com.server.oceankeeper.domain.activity.entity.LocationTag;
import com.server.oceankeeper.domain.activity.repository.ActivityRepository;
import com.server.oceankeeper.domain.crew.entitiy.CrewRole;
import com.server.oceankeeper.domain.crew.entitiy.CrewStatus;
import com.server.oceankeeper.domain.crew.entitiy.Crews;
import com.server.oceankeeper.domain.crew.param.MyActivityParam;
import com.server.oceankeeper.domain.user.entitiy.OUser;
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
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(QuerydslConfig.class)
@ActiveProfiles("test")
class ActivityRepositoryTest extends DummyObject {
    @Autowired
    private ActivityRepository activityRepository;
    @Autowired
    private EntityManager em;

    @BeforeEach
    void setUp() {
        OUser kim = newUserWithR("kim", "naver", "kimproviderId", UUIDGenerator.changeUuidFromString("123ea182ffcd11edbe560242ac120001"));
        em.persist(kim);

        OUser lee = newUserWithR("lee", "naver", "leeproviderId", UUIDGenerator.changeUuidFromString("123ea182ffcd11edbe560242ac130001"));
        em.persist(lee);

        OUser park = newUserWithR("park", "naver", "parkproviderId", UUIDGenerator.changeUuidFromString("123ea182ffcd11edbe560242ac140001"));
        em.persist(park);

        Activity activityFromKim = newMockActivity(5, ActivityStatus.OPEN, LocationTag.EAST,GarbageCategory.COASTAL,
                UUIDGenerator.changeUuidFromString("123ea182ffcd11edbe560242ac120001"));
        em.persist(activityFromKim);
        Activity activityFromKim2 = newMockActivity(6, ActivityStatus.CLOSE, LocationTag.EAST,GarbageCategory.ETC,
                UUIDGenerator.changeUuidFromString("123ea182ffcd11edbe560242ac120002"));
        em.persist(activityFromKim2);
        Activity activityFromKim3 = newMockActivity(15, ActivityStatus.OPEN, LocationTag.JEJU, GarbageCategory.DEPOSITED,
                UUIDGenerator.changeUuidFromString("123ea182ffcd11edbe560242ac120003"));
        em.persist(activityFromKim3);
        Activity activityFromKim4 = newMockActivity(25, ActivityStatus.OPEN, LocationTag.SOUTH,GarbageCategory.FLOATING,
                UUIDGenerator.changeUuidFromString("123ea182ffcd11edbe560242ac120004"));
        em.persist(activityFromKim4);
        Activity activityFromKim5 = newMockActivity(35, ActivityStatus.OPEN, LocationTag.WEST, GarbageCategory.DEPOSITED,
                UUIDGenerator.changeUuidFromString("123ea182ffcd11edbe560242ac120005"));
        em.persist(activityFromKim5);
        Activity activityFromLee = newMockActivity(10, ActivityStatus.OPEN, LocationTag.JEJU,GarbageCategory.FLOATING,
                UUIDGenerator.changeUuidFromString("123ea182ffcd11edbe560242ac120011"));
        em.persist(activityFromLee);
        Activity activityFromPark = newMockActivity(11, ActivityStatus.OPEN,  LocationTag.WEST,GarbageCategory.ETC,
                UUIDGenerator.changeUuidFromString("123ea182ffcd11edbe560242ac120021"));
        em.persist(activityFromPark);

        Crews crews = newCrew(activityFromKim, kim, CrewStatus.IN_PROGRESS, CrewRole.HOST);
        Crews crews1 = newCrew(activityFromKim2, kim, CrewStatus.CLOSED, CrewRole.HOST);
        Crews crews12 = newCrew(activityFromKim3, kim, CrewStatus.IN_PROGRESS, CrewRole.HOST);
        Crews crews13 = newCrew(activityFromKim4, kim, CrewStatus.IN_PROGRESS, CrewRole.HOST);
        Crews crews14 = newCrew(activityFromKim5, kim, CrewStatus.IN_PROGRESS, CrewRole.HOST);
        Crews crews2 = newCrew(activityFromLee, lee, CrewStatus.IN_PROGRESS, CrewRole.HOST);
        Crews crews3 = newCrew(activityFromPark, park, CrewStatus.IN_PROGRESS, CrewRole.HOST);

        Crews crews4 = newCrew(activityFromLee, kim, CrewStatus.IN_PROGRESS, CrewRole.CREW);
        Crews crews5 = newCrew(activityFromPark, kim, CrewStatus.IN_PROGRESS, CrewRole.CREW);

        em.persist(crews);
        em.persist(crews1);
        em.persist(crews12);
        em.persist(crews13);
        em.persist(crews14);
        em.persist(crews2);
        em.persist(crews3);
        em.persist(crews4);
        em.persist(crews5);
    }

    @Test
    @DisplayName("현재 날짜 기준으로 열린 일정 탑 5 조회")
    void test() {
        //given
        MyActivityParam param = new MyActivityParam(LocalDate.now(),
                UUIDGenerator.changeUuidFromString("123ea182ffcd11edbe560242ac120001"), //kim
                CrewStatus.IN_PROGRESS);
        List<MyActivityDao> result = activityRepository.getMyActivitiesLimit5(param);

        MyActivityParam param2 = new MyActivityParam(LocalDate.now(),
                UUIDGenerator.changeUuidFromString("123ea182ffcd11edbe560242ac130001"), //lee
                CrewStatus.IN_PROGRESS);
        List<MyActivityDao> result2 = activityRepository.getMyActivitiesLimit5(param2);

        MyActivityParam param3 = new MyActivityParam(LocalDate.now(),
                UUIDGenerator.changeUuidFromString("123ea182ffcd11edbe560242ac140001"), //park
                CrewStatus.IN_PROGRESS);
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
        MyActivityParam param = new MyActivityParam(LocalDate.now().plusDays(6),
                UUIDGenerator.changeUuidFromString("123ea182ffcd11edbe560242ac120001"), //kim
                CrewStatus.IN_PROGRESS);
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
                .getMyActivities(UUIDGenerator.changeUuidFromString("123ea182ffcd11edbe560242ac120001"),
                        null, ActivityStatus.OPEN, null, Pageable.ofSize(2));

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
                .getMyActivities(UUIDGenerator.changeUuidFromString("123ea182ffcd11edbe560242ac120001"),
                        UUIDGenerator.changeUuidFromString("123ea182ffcd11edbe560242ac120021"),
                        ActivityStatus.OPEN, null, Pageable.ofSize(1));

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
                .getMyActivities(UUIDGenerator.changeUuidFromString("123ea182ffcd11edbe560242ac120001"),
                        null, ActivityStatus.CLOSE, null, Pageable.ofSize(5));

        //Then
        assertThat(result.getContent().size()).isEqualTo(1);
        assertThat(result.getContent().get(0).getActivityId())
                .isEqualTo(UUIDGenerator.changeUuidFromString("123ea182ffcd11edbe560242ac120002"));
    }

    @Test
    @DisplayName("김이 속한 전체 활동을 조회한다")
    void testCrewActivities() throws Exception {
        //Given

        //When
        Slice<ActivityDao> result = activityRepository
                .getMyActivities(UUIDGenerator.changeUuidFromString("123ea182ffcd11edbe560242ac120001"),
                        null, ActivityStatus.OPEN, CrewRole.CREW, Pageable.ofSize(15));

        //Then
        assertThat(result.getContent().size()).isEqualTo(2);
        assertThat(result.getContent().get(0).getActivityId())
                .isEqualTo(UUIDGenerator.changeUuidFromString("123ea182ffcd11edbe560242ac120021"));
        assertThat(result.getContent().get(1).getActivityId())
                .isEqualTo(UUIDGenerator.changeUuidFromString("123ea182ffcd11edbe560242ac120011"));
    }

    @Test
    @DisplayName("앱 내 전체 활동 중 열린 활동 조회한다")
    void testFindScheduleActivities() throws Exception {
        //Given

        //When
        Slice<ScheduledActivityDao> result = activityRepository
                .getAllActivities(null, ActivityStatus.OPEN, null, null, Pageable.ofSize(15));

        //Then
        System.out.println("result = "+result.getContent());
        assertThat(result.getContent().size()).isEqualTo(6);
        assertThat(result.getContent().get(0).getActivityId())
                .isEqualTo(UUIDGenerator.changeUuidFromString("123ea182ffcd11edbe560242ac120021"));
        assertThat(result.getContent().get(1).getActivityId())
                .isEqualTo(UUIDGenerator.changeUuidFromString("123ea182ffcd11edbe560242ac120011"));
    }

    @Test
    @DisplayName("앱 내 전체 활동 중 특정 쓰레기타입을 조회한다")
    void testFindScheduleActivitiesByGarbage() throws Exception {
        //Given

        //When
        Slice<ScheduledActivityDao> result = activityRepository
                .getAllActivities(null, null, null, GarbageCategory.COASTAL, Pageable.ofSize(15));

        //Then
        System.out.println("result = "+result.getContent());
        assertThat(result.getContent().size()).isEqualTo(1);
        assertThat(result.getContent().get(0).getActivityId())
                .isEqualTo(UUIDGenerator.changeUuidFromString("123ea182ffcd11edbe560242ac120001"));
    }

    @Test
    @DisplayName("앱 내 전체 활동 중 특정 위치 활동 조회한다")
    void testFindScheduleActivitiesByLocationTag() throws Exception {
        //Given

        //When
        Slice<ScheduledActivityDao> result = activityRepository
                .getAllActivities(null, null, LocationTag.EAST, null, Pageable.ofSize(15));

        //Then
        System.out.println("result = "+result.getContent());
        assertThat(result.getContent().size()).isEqualTo(2);
        assertThat(result.getContent().get(0).getActivityId())
                .isEqualTo(UUIDGenerator.changeUuidFromString("123ea182ffcd11edbe560242ac120002"));
        assertThat(result.getContent().get(1).getActivityId())
                .isEqualTo(UUIDGenerator.changeUuidFromString("123ea182ffcd11edbe560242ac120001"));
    }
}
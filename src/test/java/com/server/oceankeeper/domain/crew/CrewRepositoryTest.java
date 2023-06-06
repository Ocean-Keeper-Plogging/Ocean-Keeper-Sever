package com.server.oceankeeper.domain.crew;

import com.server.oceankeeper.domain.activity.dto.response.MyActivityDao;
import com.server.oceankeeper.domain.activity.entity.Activity;
import com.server.oceankeeper.domain.activity.entity.ActivityStatus;
import com.server.oceankeeper.domain.crew.entitiy.CrewRole;
import com.server.oceankeeper.domain.crew.entitiy.CrewStatus;
import com.server.oceankeeper.domain.crew.entitiy.Crews;
import com.server.oceankeeper.domain.crew.param.MyActivityParam;
import com.server.oceankeeper.domain.crew.repository.CrewRepository;
import com.server.oceankeeper.domain.user.entitiy.OUser;
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
import org.springframework.test.context.ActiveProfiles;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(QuerydslConfig.class)
@ActiveProfiles("test")
class CrewRepositoryTest extends DummyObject {
    @Autowired
    private CrewRepository crewRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EntityManager em;

    @BeforeEach
    void setUp() {
        OUser kim = newUserWithR("kim", "naver", "kimproviderId", UUIDGenerator.changeUuidFromString("123ea182ffcd11edbe560242ac120002"));
        em.persist(kim);

        OUser lee = newUserWithR("lee", "naver", "leeproviderId", UUID.randomUUID());
        userRepository.save(lee);

        OUser park = newUserWithR("park", "naver", "parkproviderId", UUID.randomUUID());
        userRepository.save(park);

        Activity activity1 = newMockActivity(5, ActivityStatus.OPEN);
        em.persist(activity1);
        Activity activityFromLee = newMockActivity(5, ActivityStatus.OPEN);
        em.persist(activityFromLee);
        Activity activityFromPark = newMockActivity(5, ActivityStatus.OPEN);
        em.persist(activityFromPark);

        Crews crews = newCrew(activity1, kim, CrewStatus.IN_PROGRESS, CrewRole.HOST);
        Crews crews2 = newCrew(activityFromLee, lee, CrewStatus.IN_PROGRESS, CrewRole.HOST);
        Crews crews3 = newCrew(activityFromPark, park, CrewStatus.IN_PROGRESS, CrewRole.HOST);

        Crews crews4 = newCrew(activityFromLee, kim, CrewStatus.IN_PROGRESS, CrewRole.CREW);
        Crews crews5 = newCrew(activityFromPark, kim, CrewStatus.IN_PROGRESS, CrewRole.CREW);

        em.persist(crews);
        em.persist(crews2);
        em.persist(crews3);
        em.persist(crews4);
        em.persist(crews5);
    }

    @Test
    @DisplayName("김이 속한 활동 찾기")
    void test() {
        //given
        MyActivityParam param = new MyActivityParam(LocalDateTime.now(),
                UUIDGenerator.changeUuidFromString("123ea182ffcd11edbe560242ac120002"),
                CrewStatus.IN_PROGRESS);
        List<MyActivityDao> result = crewRepository.getMyActivities(param);

        assertThat(result.size()).isEqualTo(3);
        System.out.println(result);
    }
}
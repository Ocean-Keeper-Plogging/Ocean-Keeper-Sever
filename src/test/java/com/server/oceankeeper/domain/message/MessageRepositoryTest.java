package com.server.oceankeeper.domain.message;

import com.server.oceankeeper.domain.activity.entity.Activity;
import com.server.oceankeeper.domain.crew.entity.CrewRole;
import com.server.oceankeeper.domain.crew.entity.CrewStatus;
import com.server.oceankeeper.domain.crew.entity.Crews;
import com.server.oceankeeper.domain.message.entity.OMessage;
import com.server.oceankeeper.domain.message.repository.MessageRepository;
import com.server.oceankeeper.domain.user.entity.OUser;
import com.server.oceankeeper.dummy.DummyObject;
import com.server.oceankeeper.global.config.QuerydslConfig;
import com.server.oceankeeper.util.UUIDGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import javax.persistence.EntityManager;
import java.util.UUID;

@DataJpaTest
@Import(QuerydslConfig.class)
@ActiveProfiles("test")
@Disabled("not implemented")
class MessageRepositoryTest extends DummyObject {
    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private EntityManager em;

    @BeforeEach
    public void setUp() {
    }

    @Test
    public void test() {
        UUID userUUID = UUIDGenerator.createUuid();
        OUser host = newMockUser(99L, userUUID);
        em.persist(host);

        UUID userUUID2 = UUIDGenerator.createUuid();
        OUser user2 = newMockUser(100L, userUUID2);
        em.persist(user2);

        UUID userUUID3 = UUIDGenerator.createUuid();
        OUser user3 = newMockUser(101L, userUUID3);
        em.persist(user3);

        UUID activityUUID = UUIDGenerator.createUuid();
        Activity activity = newMockActivity(host, activityUUID);
        em.persist(activity);

        Crews crews = newCrew(activity, host, host, CrewStatus.IN_PROGRESS, CrewRole.HOST);
        Crews crews2 = newCrew(activity, user2, host, CrewStatus.IN_PROGRESS, CrewRole.CREW);
        Crews crews3 = newCrew(activity, user3, host, CrewStatus.IN_PROGRESS, CrewRole.CREW);
        em.persist(crews);
        em.persist(crews2);
        em.persist(crews3);

        OMessage message = OMessage.builder()
                .id(99L)
                .messageFrom(host.getNickname())
                .read(false)
                .contents("asdfasdf")
                .sender(host)
                .activity(activity)
                .build();
        em.persist(message);
    }
}
package com.server.oceankeeper.domain.message.repository;

import com.server.oceankeeper.domain.activity.entity.Activity;
import com.server.oceankeeper.domain.message.entity.MessageType;
import com.server.oceankeeper.domain.message.entity.OMessage;
import com.server.oceankeeper.domain.user.entitiy.OUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<OMessage, Long>, MessageQueryDslRepository {
    Optional<OMessage> findByActivityAndMessageType(Activity activity, MessageType messageType);
    List<OMessage> findByMessageFrom(String messageFrom);
    List<OMessage> findByMessageTo(String messageTo);
}

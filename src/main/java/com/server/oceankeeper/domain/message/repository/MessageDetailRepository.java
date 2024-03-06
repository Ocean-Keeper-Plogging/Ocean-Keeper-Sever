package com.server.oceankeeper.domain.message.repository;

import com.server.oceankeeper.domain.message.entity.MessageDetail;
import com.server.oceankeeper.domain.message.entity.MessageType;
import com.server.oceankeeper.domain.message.entity.OMessage;
import com.server.oceankeeper.domain.user.entitiy.OUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageDetailRepository extends JpaRepository<MessageDetail, Long> {
}

package com.server.oceankeeper.domain.message.repository;

import com.server.oceankeeper.domain.message.dto.MessageDao;
import com.server.oceankeeper.domain.message.entity.MessageType;
import com.server.oceankeeper.domain.user.entity.OUser;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.UUID;

public interface MessageQueryDslRepository {
    Slice<MessageDao> findByUserAndMessageType(Long lastId, OUser user, MessageType type, UUID activityId, Pageable pageable);
    Slice<MessageDao> findBySenderAndMessageType(Long lastId, OUser user, Pageable pageable);
}

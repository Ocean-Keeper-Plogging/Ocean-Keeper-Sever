package com.server.oceankeeper.domain.message.repository;

import com.server.oceankeeper.domain.message.dto.MessageDao;
import com.server.oceankeeper.domain.message.entity.MessageType;
import com.server.oceankeeper.domain.user.entitiy.OUser;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface MessageQueryDslRepository {
    Slice<MessageDao> findByUserAndMessageType(Long lastId, OUser user, MessageType type, Pageable pageable);
}

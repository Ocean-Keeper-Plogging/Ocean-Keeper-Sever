package com.server.oceankeeper.domain.message.repository;

import com.server.oceankeeper.domain.message.entity.MessageDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageDetailRepository extends JpaRepository<MessageDetail, Long> {
}

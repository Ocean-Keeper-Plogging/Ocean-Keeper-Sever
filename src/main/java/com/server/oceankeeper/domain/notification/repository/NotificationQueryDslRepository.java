package com.server.oceankeeper.domain.notification.repository;

import com.server.oceankeeper.domain.notification.entity.Notification;
import com.server.oceankeeper.domain.user.entitiy.OUser;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface NotificationQueryDslRepository {
    Slice<Notification> getNotification(OUser user, Long id, Pageable pageable);
}

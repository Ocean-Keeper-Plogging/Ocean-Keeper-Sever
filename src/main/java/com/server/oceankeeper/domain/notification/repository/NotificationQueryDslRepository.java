package com.server.oceankeeper.domain.notification.repository;

import com.server.oceankeeper.domain.notification.entity.Notification;
import com.server.oceankeeper.domain.user.entity.OUser;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface NotificationQueryDslRepository {
    Slice<Notification> getNotification(OUser user, Long id, Pageable pageable);
}

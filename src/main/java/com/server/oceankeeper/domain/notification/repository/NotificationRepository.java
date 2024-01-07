package com.server.oceankeeper.domain.notification.repository;

import com.server.oceankeeper.domain.notification.entity.Notification;
import org.springframework.data.repository.CrudRepository;

public interface NotificationRepository extends CrudRepository<Notification, Long>, NotificationQueryDslRepository {
}

package com.server.oceankeeper.notification.repository;

import com.server.oceankeeper.domain.user.entitiy.OUser;
import com.server.oceankeeper.notification.entity.Notification;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface NotificationRepository extends CrudRepository<Notification,Long> {
    List<Notification> findByUser(OUser user);
}

package com.server.oceankeeper.domain.notification.service;

import com.server.oceankeeper.domain.notification.dto.FCMRequestDto;
import com.server.oceankeeper.domain.notification.dto.MessagePreFormat;
import com.server.oceankeeper.domain.notification.dto.NotificationResDto;
import com.server.oceankeeper.domain.notification.repository.NotificationRepository;
import com.server.oceankeeper.domain.user.entitiy.OUser;
import com.server.oceankeeper.domain.user.service.UserService;
import com.server.oceankeeper.domain.notification.entity.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final UserService userService;
    private final NotificationRepository notificationRepository;

    @Transactional
    public FCMRequestDto sendNotification(String name) {
        OUser user = userService.findByNickname(name);
        FCMRequestDto request = new FCMRequestDto(user.getDeviceToken(), null, MessagePreFormat.NEW_MESSAGE.get());

        saveNewMessage(user);
        return request;
    }

    private void saveNewMessage(OUser user) {
        Notification notification = Notification.builder()
                .isRead(false)
                .user(user)
                .type(MessagePreFormat.NEW_MESSAGE)
                .build();
        notificationRepository.save(notification);
    }

    @Transactional
    public NotificationResDto getNotificationList(String userId) {
        OUser user = userService.findByUUID(userId);
        List<Notification> response = notificationRepository.findByUser(user);
        for (Notification notification : response) {
            notification.read();
        }
        return null;
    }
}

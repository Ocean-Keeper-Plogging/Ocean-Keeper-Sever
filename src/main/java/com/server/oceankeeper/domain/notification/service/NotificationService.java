package com.server.oceankeeper.domain.notification.service;

import com.server.oceankeeper.domain.message.entity.MessageEvent;
import com.server.oceankeeper.domain.notification.dto.FCMRequestDto;
import com.server.oceankeeper.domain.notification.dto.MessagePreFormat;
import com.server.oceankeeper.domain.notification.dto.NotificationAlarmDto;
import com.server.oceankeeper.domain.notification.dto.NotificationResDto;
import com.server.oceankeeper.domain.notification.entity.Notification;
import com.server.oceankeeper.domain.notification.repository.NotificationRepository;
import com.server.oceankeeper.domain.user.entitiy.OUser;
import com.server.oceankeeper.domain.user.service.UserService;
import com.server.oceankeeper.global.eventfilter.OceanKeeperEventType;
import com.server.oceankeeper.global.exception.IllegalRequestException;
import com.server.oceankeeper.util.TokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    private final UserService userService;
    private final NotificationRepository notificationRepository;
    private final TokenUtil tokenUtil;

    @Transactional
    public FCMRequestDto sendNotification(String name, OceanKeeperEventType eventType) {
        OUser user = userService.findByNickname(name);
        if (user.isAlarm()) {
            FCMRequestDto request = new FCMRequestDto(user.getDeviceToken(), MessagePreFormat.get(eventType).getValue());

            saveNewMessage(user, MessagePreFormat.get(eventType));
            return request;
        }
        return null;
    }

    @Transactional
    public FCMRequestDto sendNotification(OUser user, OceanKeeperEventType eventType) {
        if (user.isAlarm()) {
            FCMRequestDto request = new FCMRequestDto(user.getDeviceToken(), MessagePreFormat.get(eventType).getValue());

            saveNewMessage(user, MessagePreFormat.get(eventType));
            return request;
        }
        return null;
    }

    private void saveNewMessage(OUser user, MessagePreFormat messageType) {
        Notification notification = Notification.builder()
                .isRead(false)
                .user(user)
                .type(messageType)
                .build();
        notificationRepository.save(notification);
    }

    @Transactional
    public NotificationAlarmDto setNotification(String userId, Boolean alarm, HttpServletRequest request) {
        OUser user = userService.findByUUID(userId);
        OUser requester = tokenUtil.getUserFromHeader(request);

        if (!requester.equals(user))
            throw new IllegalRequestException("해당 유저의 알림을 변경할 권한이 없습니다.");

        userService.setAlarm(alarm, user);
        return new NotificationAlarmDto(alarm);
    }

    @Transactional
    public NotificationAlarmDto getNotification(String userId, HttpServletRequest request) {
        OUser user = userService.findByUUID(userId);
        OUser requester = tokenUtil.getUserFromHeader(request);

        if (!requester.equals(user))
            throw new IllegalRequestException("해당 유저의 알림을 확인할 권한이 없습니다.");

        boolean alarm = userService.getAlarm(user);
        return new NotificationAlarmDto(alarm);
    }

    @Transactional
    public NotificationResDto getNotificationList(String userId, Long id, Integer size, HttpServletRequest request) {
        OUser user = userService.findByUUID(userId);
        OUser requester = tokenUtil.getUserFromHeader(request);

        if (!requester.equals(user))
            throw new IllegalRequestException("해당 유저의 알림을 확인할 권한이 없습니다.");

        Slice<Notification> response = notificationRepository.getNotification(user, id, Pageable.ofSize(size != null ? size : 20));
        log.debug("[getNotificationList] response:{}", response.getContent());
        List<NotificationResDto.NotificationData> result = response.getContent().stream()
                .map(r -> new NotificationResDto.NotificationData(
                        r.getId(),
                        r.getType().getValue(),
                        convertDate(r.getCreatedAt()),
                        r.getIsRead()
                ))
                .collect(Collectors.toList());

        for (Notification notification : response) {
            notification.read();
        }
        return new NotificationResDto(
                result,
                new NotificationResDto.Meta(response.getContent().size(), !response.hasNext()));
    }

    private String convertDate(LocalDateTime createdAt) {
        long seconds = Duration.between(createdAt, LocalDateTime.now()).toSeconds();
        if (seconds <= 0) {
            throw new RuntimeException("이상한 값이 입력되었습니다. 생성일:" + createdAt + " ,현재 시각: " + LocalDateTime.now());
        } else if (seconds <= 60 * 60) {
            return seconds / 60 + "분 전";
        } else if (seconds <= 1440 * 60) {
            return seconds / 3600 + "시간 전";
        } else {
            DateTimeFormatter pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return createdAt.format(pattern);
        }
    }

    @Transactional
    public Slice<OUser> saveNotificationAndGetAlarmedUsers(MessageEvent event, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Slice<OUser> users = userService.findUsersByNotificationAlarm(true, pageable);
        log.info("[saveNotificationAndGetAlarmedUsers] users:{}", users.getContent());
        for (OUser user : users) {
            saveNewMessage(user, MessagePreFormat.get(event.getEvent()));
        }
        return users;
    }
}

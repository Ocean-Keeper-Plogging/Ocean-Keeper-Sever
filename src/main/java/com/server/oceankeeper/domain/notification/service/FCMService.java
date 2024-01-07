package com.server.oceankeeper.domain.notification.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.server.oceankeeper.domain.activity.dto.inner.UserListDto;
import com.server.oceankeeper.domain.message.entity.MessageEvent;
import com.server.oceankeeper.domain.notification.dto.FCMRequestDto;
import com.server.oceankeeper.domain.notification.dto.MessagePreFormat;
import com.server.oceankeeper.domain.user.entitiy.OUser;
import com.server.oceankeeper.global.eventfilter.OceanKeeperEventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FCMService {
    private final NotificationService notificationService;
    private final FirebaseMessaging firebaseMessaging;

    public static String ALL_MEMBER_TOPIC = "OCEANKEEPER_ALL_MEMBER";

    @EventListener
    @Async
    public void handle(MessageEvent event) {
        if (event.getEvent().equals(OceanKeeperEventType.MESSAGE_SENT_EVENT)) {
            handleMessageSentEvent(event);
        } else if (event.getEvent().equals(OceanKeeperEventType.USER_JOINED_EVENT)) {
            handleUserJoinEvent(event);
        } else if (event.getEvent().equals(OceanKeeperEventType.USER_WITHDRAWAL_EVENT)) {
            handleUserWithdrawnEvent(event);
        } else if (event.getEvent().equals(OceanKeeperEventType.NEW_NOTICE_EVENT) ||
                event.getEvent().equals(OceanKeeperEventType.TERMS_CHANGED_EVENT)) {
            handleAdminMessageEvent(event);
        } else if (
                event.getEvent().equals(OceanKeeperEventType.ACTIVITY_START_SOON_EVENT) ||
                event.getEvent().equals(OceanKeeperEventType.ACTIVITY_RECRUITMENT_CLOSED_EVENT) ||
                event.getEvent().equals(OceanKeeperEventType.ACTIVITY_CHANGED_EVENT) ||
                event.getEvent().equals(OceanKeeperEventType.ACTIVITY_REGISTRATION_CANCEL_EVENT) ||
                event.getEvent().equals(OceanKeeperEventType.ACTIVITY_CLOSE_EVENT)
        ) {
            handleActivityEvent(event);
        }
    }

    private void handleAdminMessageEvent(MessageEvent event) {
        notificationService.saveMessageAllUsers(event);
        sendFCMMessageToTopic(MessagePreFormat.get(event.getEvent()).getValue());
    }

    private void handleUserWithdrawnEvent(MessageEvent event) {
        log.info("[handleUserWithdrawnEvent] event");
        OUser user = (OUser) event.getObject();
        try {
            firebaseMessaging.unsubscribeFromTopic(List.of(user.getDeviceToken()), ALL_MEMBER_TOPIC);
            log.info("user unsubscribe event");
        } catch (FirebaseMessagingException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleUserJoinEvent(MessageEvent event) {
        log.info("JBJB USER_JOINED_EVENT event");
        OUser user = (OUser) event.getObject();
        try {
            firebaseMessaging.subscribeToTopic(List.of(user.getDeviceToken()), ALL_MEMBER_TOPIC);
            log.info("JBJB user event");
        } catch (FirebaseMessagingException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public String sendFCMMessage(FCMRequestDto request) {
        Notification notification = Notification.builder()
                .setBody(request.getContents())
                .build();
        Message message = Message.builder()
                .setToken(request.getDeviceToken())
                .setNotification(notification)
                .build();
        try {
            firebaseMessaging.send(message);
            log.info("Firebase Message sent. message = {}", request.getContents());
            return "true";
        } catch (FirebaseMessagingException e) {
            log.error("Firebase 메세지 전송 실패 : {}", e.getMessagingErrorCode().toString());
            //throw new IOException(String.format("Firebase 메세지 전송 실패 :%s", e.getMessagingErrorCode().toString()));
            return "false";
        }
    }

    @Transactional
    public String sendFCMMessageToTopic(String contents) {
        Notification notification = Notification.builder()
                .setBody(contents)
                .build();
        Message message = Message.builder()
                .setTopic(ALL_MEMBER_TOPIC)
                .setNotification(notification)
                .build();
        try {
            firebaseMessaging.send(message);
            log.info("Firebase Message sent. message = {}", contents);
            return "true";
        } catch (FirebaseMessagingException e) {
            log.error("Firebase 메세지 전송 실패 : {}", e.getMessagingErrorCode().toString());
            //throw new IOException(String.format("Firebase 메세지 전송 실패 :%s", e.getMessagingErrorCode().toString()));
            return "false";
        }
    }

    private void handleMessageSentEvent(MessageEvent event) {
        FCMRequestDto request = notificationService.sendNotification((String) event.getObject(), event.getEvent());
        if (request != null)
            sendFCMMessage(request);
    }

    @Transactional
    public void handleActivityEvent(MessageEvent event) {
        log.info("[handleActivityEvent] event:{}", event);
        UserListDto crews = (UserListDto) event.getObject();
        for (OUser user : crews.getUser()) {
            FCMRequestDto request = notificationService.sendNotification(user, event.getEvent());
            if (request != null)
                sendFCMMessage(request);
        }
    }
}

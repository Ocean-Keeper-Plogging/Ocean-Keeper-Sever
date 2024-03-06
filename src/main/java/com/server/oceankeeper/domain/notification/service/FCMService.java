package com.server.oceankeeper.domain.notification.service;

import com.google.firebase.messaging.*;
import com.server.oceankeeper.domain.activity.dto.inner.UserListDto;
import com.server.oceankeeper.domain.message.entity.MessageEvent;
import com.server.oceankeeper.domain.notification.dto.FCMRequestDto;
import com.server.oceankeeper.domain.notification.dto.MessagePreFormat;
import com.server.oceankeeper.domain.user.entitiy.OUser;
import com.server.oceankeeper.global.eventfilter.OceanKeeperEventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Slice;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FCMService {
    private final NotificationService notificationService;
    private final FirebaseMessaging firebaseMessaging;

    @EventListener
    @Async
    public void handle(MessageEvent event) {
        if (event.getEvent().equals(OceanKeeperEventType.MESSAGE_SENT_EVENT)) {
            handleMessageSentEvent(event);
        } else if (event.getEvent().equals(OceanKeeperEventType.NEW_NOTICE_EVENT) ||
                event.getEvent().equals(OceanKeeperEventType.TERMS_CHANGED_EVENT)) {
            handleAdminMessageEvent(event);
        } else if (
                event.getEvent().equals(OceanKeeperEventType.ACTIVITY_START_SOON_EVENT) ||
                        event.getEvent().equals(OceanKeeperEventType.ACTIVITY_RECRUITMENT_CLOSED_EVENT) ||
                        event.getEvent().equals(OceanKeeperEventType.ACTIVITY_CHANGED_EVENT) ||
                        event.getEvent().equals(OceanKeeperEventType.ACTIVITY_REGISTRATION_CANCEL_EVENT)
        ) {
            handleActivityEvent(event);
        }
    }

    private void handleAdminMessageEvent(MessageEvent event) {
        log.info("[handleAdminMessageEvent] event = {}", event);
        int page = 0;
        final int size = 100;
        boolean continued = true;
        while (continued) {
            Slice<OUser> users = notificationService.saveNotificationAndGetAlarmedUsers(event, page, size);
            sendFCMMessageToTopic(MessagePreFormat.get(event.getEvent()).getValue(),
                    users.stream().map(OUser::getDeviceToken).collect(Collectors.toList()));
            continued = users.hasNext();
            page += size;
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
    public void sendFCMMessageToTopic(String contents, List<String> deviceTokenList) {
        Notification notification = Notification.builder()
                .setBody(contents)
                .build();
        MulticastMessage multicastMessage = MulticastMessage.builder()
                .setNotification(notification)
                .addAllTokens(deviceTokenList)
                .build();
        log.info("JBJB device list :{}", deviceTokenList);
        try {
            firebaseMessaging.sendEachForMulticast(multicastMessage);
            log.info("Firebase Message sent. message = {}", contents);
        } catch (FirebaseMessagingException e) {
            log.error("Firebase 메세지 전송 실패 : {}", e.getMessagingErrorCode().toString());
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
            log.info("[handleActivityEvent] request:{}", request);
            if (request != null)
                sendFCMMessage(request);
        }
    }
}

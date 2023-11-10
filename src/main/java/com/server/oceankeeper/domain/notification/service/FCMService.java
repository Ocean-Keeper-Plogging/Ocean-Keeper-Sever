package com.server.oceankeeper.domain.notification.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.server.oceankeeper.domain.message.entity.MessageEvent;
import com.server.oceankeeper.domain.notification.dto.FCMRequestDto;
import com.server.oceankeeper.global.eventfilter.OceanKeeperEventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
@RequiredArgsConstructor
public class FCMService {
    private final NotificationService notificationService;
    private final FirebaseMessaging firebaseMessaging;

    @EventListener
    @Async
    public void handle(MessageEvent event) throws IOException {
        if (event.getEvent().equals(OceanKeeperEventType.MESSAGE_SENT_EVENT)) {
            handleMessageSentEvent(event);
        }
    }

    public void sendMessage(MessageEvent event){
        try {
            handleMessageSentEvent(event);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleMessageSentEvent(MessageEvent event) throws IOException {
        FCMRequestDto request = notificationService.sendNotification(event.getNickname());
        Notification notification= Notification.builder()
                .setBody(request.getContents())
                .build();
        Message message = Message.builder()
                .setToken(request.getDeviceToken())
                .setNotification(notification)
                .build();
        try{
            firebaseMessaging.send(message);
        }catch (FirebaseMessagingException e){
            //throw new IOException(String.format("Firebase 메세지 전송 실패 :%s", e.getMessagingErrorCode().toString()));
        }
    }
}

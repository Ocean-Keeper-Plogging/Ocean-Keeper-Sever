package com.server.oceankeeper.global.eventfilter;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventPublisher {
    private final ApplicationEventPublisher publisher;

//    public static void setPublisher(ApplicationEventPublisher publisher) {
//        EventPublisher.publisher = publisher;
//    }

    public void emit(Object event) {
        if (publisher != null) {
            publisher.publishEvent(event);
        }
    }
}

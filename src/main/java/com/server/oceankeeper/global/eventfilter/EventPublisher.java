package com.server.oceankeeper.global.eventfilter;

import org.springframework.context.ApplicationEventPublisher;

public class EventPublisher {
    private static ApplicationEventPublisher publisher;

    public static void setPublisher(ApplicationEventPublisher publisher) {
        EventPublisher.publisher = publisher;
    }

    public static void emit(Object event) {
        if (publisher != null) {
            publisher.publishEvent(event);
        }
    }
}

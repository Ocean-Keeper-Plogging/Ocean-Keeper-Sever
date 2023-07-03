package com.server.oceankeeper.domain.statistics.entity;

import org.springframework.context.ApplicationEventPublisher;

public class EventPublisher {
    private static ApplicationEventPublisher publisher;

    public static void setPublisher(ApplicationEventPublisher publisher) {
        EventPublisher.publisher = publisher;
    }

    public static void raise(Object event) {
        if (publisher != null) {
            publisher.publishEvent(event);
        }
    }
}

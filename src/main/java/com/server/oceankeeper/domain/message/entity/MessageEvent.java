package com.server.oceankeeper.domain.message.entity;

import com.server.oceankeeper.global.eventfilter.OceanKeeperEventType;
import lombok.Getter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

@ToString
public class MessageEvent extends ApplicationEvent {
    @Getter
    private final Object object;
    @Getter
    private final OceanKeeperEventType event;


    public MessageEvent(Object source, Object object, OceanKeeperEventType event) {
        super(source);
        this.object = object;
        this.event = event;
    }
}

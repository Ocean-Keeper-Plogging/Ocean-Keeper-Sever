package com.server.oceankeeper.domain.statistics.entity;

import com.server.oceankeeper.global.eventfilter.OceanKeeperEventType;
import lombok.Getter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

@ToString
public class ActivityEvent extends ApplicationEvent {
    @Getter
    private final Object object;
    @Getter
    private final OceanKeeperEventType event;

    public ActivityEvent(Object source, Object obj, OceanKeeperEventType event) {
        super(source);
        this.object = obj;
        this.event = event;
    }
}

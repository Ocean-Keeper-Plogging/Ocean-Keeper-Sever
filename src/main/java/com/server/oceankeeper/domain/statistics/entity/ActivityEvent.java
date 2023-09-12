package com.server.oceankeeper.domain.statistics.entity;

import com.server.oceankeeper.domain.user.entitiy.OUser;
import com.server.oceankeeper.global.eventfilter.OceanKeeperEventType;
import lombok.Getter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

@ToString
public class ActivityEvent extends ApplicationEvent {
    @Getter
    private final OUser user;
    @Getter
    private final OceanKeeperEventType event;

    public ActivityEvent(Object source, OUser user, OceanKeeperEventType event) {
        super(source);
        this.user = user;
        this.event = event;
    }
}

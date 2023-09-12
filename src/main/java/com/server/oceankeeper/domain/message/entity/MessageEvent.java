package com.server.oceankeeper.domain.message.entity;

import com.server.oceankeeper.domain.message.entity.OMessage;
import com.server.oceankeeper.domain.user.entitiy.OUser;
import com.server.oceankeeper.global.eventfilter.OceanKeeperEventType;
import lombok.Getter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

@ToString
public class MessageEvent extends ApplicationEvent {
    @Getter
    private final String nickname;
    @Getter
    private final OceanKeeperEventType event;


    public MessageEvent(Object source, String nickname, OceanKeeperEventType event) {
        super(source);
        this.nickname = nickname;
        this.event = event;
    }
}

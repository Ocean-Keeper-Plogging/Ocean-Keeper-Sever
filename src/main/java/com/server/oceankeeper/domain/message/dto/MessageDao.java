package com.server.oceankeeper.domain.message.dto;

import com.server.oceankeeper.domain.activity.entity.GarbageCategory;
import com.server.oceankeeper.domain.message.entity.MessageType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class MessageDao {
    private final Long id;
    private final MessageType type;

    private final String from;
    private final UUID activityId;
    private final String title;
    private final GarbageCategory garbageCategory;

    private final LocalDateTime time;

    private final boolean read;
}

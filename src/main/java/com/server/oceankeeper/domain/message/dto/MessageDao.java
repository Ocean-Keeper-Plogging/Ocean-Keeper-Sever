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
    private final String activityTitle;
    private final String messageBody;
    private final GarbageCategory garbageCategory;

    private final LocalDateTime messageSentAt;
    private final LocalDateTime activityStartAt;

    private final boolean read;
}

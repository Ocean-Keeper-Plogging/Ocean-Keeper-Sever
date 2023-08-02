package com.server.oceankeeper.domain.message.dto.request;

import com.server.oceankeeper.domain.message.entity.MessageType;
import lombok.Data;

import java.util.List;

@Data
public class MessageSendReqDto {
    private final List<String> targetNicknames;
    private final MessageType type;
    private final String activityId;
    private final String contents;
}

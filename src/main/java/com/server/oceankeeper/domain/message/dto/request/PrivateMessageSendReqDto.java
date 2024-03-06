package com.server.oceankeeper.domain.message.dto.request;

import com.server.oceankeeper.domain.message.entity.MessageType;
import lombok.Data;

import java.util.List;

@Data
public class PrivateMessageSendReqDto {
    private final String targetNickname;
    private final String activityId;
    private final String contents;
}

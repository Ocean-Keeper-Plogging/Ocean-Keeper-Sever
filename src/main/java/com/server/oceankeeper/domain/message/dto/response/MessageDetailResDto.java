package com.server.oceankeeper.domain.message.dto.response;

import com.server.oceankeeper.domain.message.entity.MessageDetail;
import com.server.oceankeeper.domain.message.entity.MessageType;
import com.server.oceankeeper.domain.message.entity.OMessage;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageDetailResDto {
    private final Long messageId;
    private final String contents;
    private final String from;
    private final String to;
    private final LocalDateTime createdAt;
    private final MessageType messageType;

    public static MessageDetailResDto fromEntity(OMessage message) {
        return new MessageDetailResDto(
                message.getId(),
                message.getContents(),
                message.getMessageFrom(),
                message.getMessageTo(),
                message.getTime(),
                message.getMessageType()
        );
    }
}

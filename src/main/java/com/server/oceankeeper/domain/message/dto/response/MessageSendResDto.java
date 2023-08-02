package com.server.oceankeeper.domain.message.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class MessageSendResDto {
    private final List<Long> messageId;
}

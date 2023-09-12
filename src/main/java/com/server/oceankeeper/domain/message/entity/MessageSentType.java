package com.server.oceankeeper.domain.message.entity;

import lombok.Getter;

@Getter
public enum MessageSentType {
    REJECT,
    NOTICE,
    PRIVATE,
}
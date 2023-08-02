package com.server.oceankeeper.domain.message.entity;

import lombok.Getter;

@Getter
public enum MessageType {
    REJECT,
    NOTICE,
    PRIVATE,
    POST,
    ALL
}
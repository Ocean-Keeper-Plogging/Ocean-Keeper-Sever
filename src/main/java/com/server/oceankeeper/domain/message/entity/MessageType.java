package com.server.oceankeeper.domain.message.entity;

import com.server.oceankeeper.global.exception.ResourceNotFoundException;
import lombok.Getter;

@Getter
public enum MessageType {
    REJECT("REJECT"),
    NOTICE("NOTICE"),
    PRIVATE("PRIVATE"),
    POST("POST"),
    ALL("ALL");

    private final String str;

    MessageType(String s) {
        this.str = s;
    }

    public static MessageType toClass(String s) {
        if (s == null) return MessageType.ALL;

        switch (s) {
            case "REJECT":
                return MessageType.REJECT;
            case "NOTICE":
                return MessageType.NOTICE;
            case "PRIVATE":
                return MessageType.PRIVATE;
            case "ALL":
                return MessageType.ALL;
            default:
                throw new ResourceNotFoundException("해당 메세지 타입이 존재하지 않습니다. 철자를 확인해주세요.");
        }
    }
}
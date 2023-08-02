package com.server.oceankeeper.domain.message.dto.response;

import com.server.oceankeeper.domain.message.entity.MessageType;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PostResDto {
    private final List<MessageDto> messages;

    @Getter
    @RequiredArgsConstructor
    public static class MessageDto {
        private final Long id;
        private final MessageType type;

        private final String from;
        private final String activityId;
        private final String title;

        private final LocalDateTime time;

        private final boolean read;
    }
}

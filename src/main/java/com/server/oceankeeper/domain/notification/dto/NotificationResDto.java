package com.server.oceankeeper.domain.notification.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class NotificationResDto {
    private final Long id;
    private final String contents;
    private final LocalDateTime createdAt;
    private final boolean isRead;
}

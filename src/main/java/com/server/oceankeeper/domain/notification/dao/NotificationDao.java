package com.server.oceankeeper.domain.notification.dao;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationDao {
    private final String contents;
    private final LocalDateTime createdAt;
    private final Long id;
}

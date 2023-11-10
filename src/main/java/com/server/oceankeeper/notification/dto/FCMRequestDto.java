package com.server.oceankeeper.notification.dto;

import lombok.Data;

@Data
public class FCMRequestDto {
    private final String deviceToken;
    private final String title;
    private final String contents;
}

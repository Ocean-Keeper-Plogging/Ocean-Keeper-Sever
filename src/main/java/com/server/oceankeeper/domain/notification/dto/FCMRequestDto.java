package com.server.oceankeeper.domain.notification.dto;

import lombok.Data;

@Data
public class FCMRequestDto {
    private final String deviceToken;
    private final String contents;
}

package com.server.oceankeeper.domain.activity.dto.inner;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ModifyActivityEventDto {
    private final LocalDateTime startAt;
    private final String activityId;
}

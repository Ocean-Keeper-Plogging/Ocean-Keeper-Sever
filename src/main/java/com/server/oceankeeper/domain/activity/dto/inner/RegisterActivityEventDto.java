package com.server.oceankeeper.domain.activity.dto.inner;

import com.server.oceankeeper.domain.user.entity.OUser;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class RegisterActivityEventDto {
    private final LocalDateTime startAt;
    private final LocalDate recruitEndAt;
    private final String activityId;
    private final OUser host;
}

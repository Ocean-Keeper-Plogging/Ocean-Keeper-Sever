package com.server.oceankeeper.domain.activity.dto;

import com.server.oceankeeper.domain.activity.entity.ActivityStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class ActivityDao {
    private final UUID activityId;

    private final String title;

    private final String hostNickname;
    private final Integer quota;
    private final Integer participants;

    private String activityImageUrl;

    private final LocalDate recruitStartAt;
    private final LocalDate recruitEndAt;

    private final LocalDateTime startAt;
    private final ActivityStatus status;

    private String address;
}

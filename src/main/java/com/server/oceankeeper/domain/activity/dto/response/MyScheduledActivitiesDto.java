package com.server.oceankeeper.domain.activity.dto.response;

import lombok.*;

import java.util.List;

@ToString
@Getter
@AllArgsConstructor
public class MyScheduledActivitiesDto {
    private final List<MyScheduledActivityDto> activities;
}

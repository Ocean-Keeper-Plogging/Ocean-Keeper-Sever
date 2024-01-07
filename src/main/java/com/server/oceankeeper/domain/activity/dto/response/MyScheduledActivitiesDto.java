package com.server.oceankeeper.domain.activity.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@ToString
@Getter
@AllArgsConstructor
public class MyScheduledActivitiesDto {
    private final List<MyScheduledActivityDto> activities;
}

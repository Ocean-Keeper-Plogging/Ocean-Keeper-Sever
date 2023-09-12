package com.server.oceankeeper.domain.activity.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
public class HostActivityDto {
    private final List<HostActivityInnerDto> hostActivities;

    @RequiredArgsConstructor
    @Data
    public static class HostActivityInnerDto {
        private final String activityId;
        private final String title;
    }
}

package com.server.oceankeeper.domain.activity.dto.response;

import com.server.oceankeeper.domain.activity.dto.HostActivityDto;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
public class CrewActivityDto {
    private final String activityId;
    private final String activityTitle;
    private final List<CrewActivityInnerClass> crewInformationList;

    @RequiredArgsConstructor
    @Data
    public static class CrewActivityInnerClass {
        private final String nickname;
    }
}

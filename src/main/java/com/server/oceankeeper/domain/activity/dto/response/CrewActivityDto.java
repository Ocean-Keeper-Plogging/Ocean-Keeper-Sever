package com.server.oceankeeper.domain.activity.dto.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
public class CrewActivityDto {
    @ApiModelProperty(
            value = "활동 id",
            required = true
    )
    private final String activityId;

    @ApiModelProperty(
            value = "활동 타이틀",
            required = true
    )
    private final String activityTitle;
    private final List<CrewActivityInnerClass> crewInformationList;

    @RequiredArgsConstructor
    @Data
    public static class CrewActivityInnerClass {
        @ApiModelProperty(
                value = "크루원 닉네임",
                required = true
        )
        private final String nickname;
    }
}

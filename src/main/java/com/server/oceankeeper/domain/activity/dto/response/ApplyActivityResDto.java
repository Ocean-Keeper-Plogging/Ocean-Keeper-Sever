package com.server.oceankeeper.domain.activity.dto.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@RequiredArgsConstructor
public class ApplyActivityResDto {
    @ApiModelProperty(
            value = "등록한 activity id",
            required = true
    )
    private final String activityId;

    @ApiModelProperty(
            value = "신청서 아이디",
            required = true
    )
    private final String applicationId;
}

package com.server.oceankeeper.domain.activity.dto.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ActivityTransportationDto {
    @ApiModelProperty(value = "활동 게시자가 지원하는 교통 수단")
    private final String transportation;
}

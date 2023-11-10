package com.server.oceankeeper.domain.activity.dto;

import com.server.oceankeeper.domain.crew.entitiy.CrewStatus;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ApplicationSettingResDto {
    @ApiModelProperty(
            value="api 실행 결과 true/false",
            example = "true"
    )
    private final boolean result;
    @ApiModelProperty(
            value = "바뀐 crew status NO_SHOW/REJECT만 가능",
            example = "NO_SHOW/REJECT"
    )
    private final CrewStatus crewStatus;
}

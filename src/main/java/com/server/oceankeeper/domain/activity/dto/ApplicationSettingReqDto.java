package com.server.oceankeeper.domain.activity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class ApplicationSettingReqDto {
    @ApiModelProperty(
            value = "status 바꿀 지원서 id. 리스트 형태로",
            example = "[11ee2962ed293b2a869b0f30e7d4f7c1]"
    )
    private List<String> applicationId;
    @ApiModelProperty(
            value = "바꿀 status NO_SHOW,REJECT 중 하나여야함",
            example = "[11ee2962ed293b2a869b0f30e7d4f7c1]"
    )
    private String status;
}

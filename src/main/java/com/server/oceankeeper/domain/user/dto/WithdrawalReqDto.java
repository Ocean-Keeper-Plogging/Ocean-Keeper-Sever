package com.server.oceankeeper.domain.user.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class WithdrawalReqDto {
    @ApiModelProperty(
            value = "Oauth Provider",
            dataType = "string",
            example = "naver",
            required = true)
    @NotEmpty
    private final String provider;

    @ApiModelProperty(
            value = "Oauth Provider Id",
            dataType = "string",
            required = true
    )
    @NotEmpty
    private final String providerId;

    @NotEmpty
    @ApiModelProperty(
            value = "디바이스 토큰",
            dataType = "string",
            required = true
    )
    private final String deviceToken;
}

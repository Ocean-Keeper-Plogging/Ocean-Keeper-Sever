package com.server.oceankeeper.domain.user.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
@Data
@Builder
public class UserInfoDto {
    @ApiModelProperty(value = "신청자 닉네임")
    private final String nickname;
    @ApiModelProperty(value = "신청자 프로필")
    private final String profile;
}

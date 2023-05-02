package com.server.oceankeeper.Domain.User.dto;

import com.server.oceankeeper.Domain.User.User;
import com.server.oceankeeper.Domain.User.UserEnum.UserRole;
import com.server.oceankeeper.Domain.User.UserEnum.UserStatus;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

@Data
public class JoinReqDto{
    @ApiModelProperty(
            value = "Oauth Provider",
            dataType = "String",
            example = "naver")
    private final String provider;
    @ApiModelProperty(
            value = "Oauth Provider Id",
            dataType = "String"
    )
    private final String providerId;

    private final String nickname;
    private final String email;
    private final String profile;

    private final String deviceToken;

    @Builder
    public JoinReqDto(String provider, String providerId, String nickname, String email, String profile, String deviceToken) {
        this.provider = provider;
        this.providerId = providerId;
        this.nickname = nickname;
        this.email = email;
        this.profile = profile;
        this.deviceToken = deviceToken;
    }


    public User toEntity(){
        return User.builder()
                .provider(provider)
                .providerId(providerId)
                .nickname(nickname)
                .email(email)
                .profile(profile)
                .status(UserStatus.ACTIVE)
                .role(UserRole.USER)
                .deviceToken(deviceToken)
                .build();
    }
}
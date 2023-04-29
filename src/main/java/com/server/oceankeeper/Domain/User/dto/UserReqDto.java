package com.server.oceankeeper.Domain.User.dto;

import com.server.oceankeeper.Domain.User.User;
import com.server.oceankeeper.Domain.User.UserEnum.UserRole;
import com.server.oceankeeper.Domain.User.UserEnum.UserStatus;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

public class UserReqDto {

    @Data
    public static class LoginReqDto{
        private String providerId;
        private String provider;

    }

    @Data
    public static class JoinReqDto{
        @ApiModelProperty(
                value = "Oauth Provider",
                dataType = "String",
                example = "naver")
        private String provider;
        @ApiModelProperty(
                value = "Oauth Provider Id",
                dataType = "String"
                )
        private String providerId;

        private String nickname;
        private String email;
        private String profile;

        private String deviceToken;

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
}

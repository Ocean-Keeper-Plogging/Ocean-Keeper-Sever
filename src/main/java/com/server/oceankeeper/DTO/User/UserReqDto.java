package com.server.oceankeeper.DTO.User;

import com.server.oceankeeper.User.User;
import com.server.oceankeeper.User.UserEnum.UserRole;
import com.server.oceankeeper.User.UserEnum.UserStatus;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

public class UserReqDto {

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

        public User toEntity(){
            return User.builder()
                    .provider(provider)
                    .providerId(providerId)
                    .nickname(nickname)
                    .email(email)
                    .profile(profile)
                    .status(UserStatus.ACTIVE)
                    .role(UserRole.USER)
                    .build();
        }

    }
}

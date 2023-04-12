package com.server.oceankeeper.DTO.User;

import com.server.oceankeeper.User.User;
import com.server.oceankeeper.User.UserEnum.UserRole;
import com.server.oceankeeper.User.UserEnum.UserStatus;
import lombok.Data;

public class UserReqDto {

    @Data
    public static class JoinReqDto{
        private String provider;
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

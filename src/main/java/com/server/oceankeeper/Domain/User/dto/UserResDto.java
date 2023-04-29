package com.server.oceankeeper.Domain.User.dto;

import com.server.oceankeeper.Domain.User.User;
import lombok.Data;

public class UserResDto {

    @Data
    public static class JoinResDto{
        private Long id;
        private String nickname;

        public JoinResDto(User user) {
            this.id = user.getId();
            this.nickname = user.getNickname();
        }
    }

    @Data
    public static class LoginResDto{
        private Long id;
        private String nickname;


        public LoginResDto(User user){
            this.id = user.getId();
            this.nickname = user.getNickname();
        }

    }

    @Data
    public static class UserInfoDto{
        private String nickname;
        private String profile;
        private String cratedAt;

    }
}

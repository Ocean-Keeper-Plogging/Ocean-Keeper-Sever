package com.server.oceankeeper.DTO.User;

import com.server.oceankeeper.User.User;
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
}

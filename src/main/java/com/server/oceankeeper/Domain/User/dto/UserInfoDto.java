package com.server.oceankeeper.Domain.User.dto;

import lombok.Builder;
import lombok.Data;
@Data
public class UserInfoDto {

    private final String nickname;
    private final String profile;
    private final String cratedAt;

    @Builder
    public UserInfoDto(String nickname, String profile, String cratedAt) {
        this.nickname = nickname;
        this.profile = profile;
        this.cratedAt = cratedAt;
    }
}

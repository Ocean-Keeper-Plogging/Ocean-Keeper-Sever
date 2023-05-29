package com.server.oceankeeper.domain.user.dto;

import lombok.Builder;
import lombok.Data;
@Data
public class UserInfoDto {

    private final String nickname;
    private final String profile;
    private final String createdAt;

    @Builder
    public UserInfoDto(String nickname, String profile, String createdAt) {
        this.nickname = nickname;
        this.profile = profile;
        this.createdAt = createdAt;
    }
}

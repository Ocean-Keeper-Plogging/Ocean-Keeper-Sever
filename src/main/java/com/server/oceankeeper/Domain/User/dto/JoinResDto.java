package com.server.oceankeeper.Domain.User.dto;

import com.server.oceankeeper.Domain.User.User;
import lombok.Data;

@Data
public class JoinResDto{
    private final Long id;
    private final String nickname;

    public JoinResDto(User user) {
        this.id = user.getId();
        this.nickname = user.getNickname();
    }
}

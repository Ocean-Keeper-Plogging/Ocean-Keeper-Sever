package com.server.oceankeeper.Domain.User.dto;

import com.server.oceankeeper.Domain.User.User;
import lombok.Builder;
import lombok.Data;


@Data
public class LoginResDto{

    private final String msg;

    @Builder
    public LoginResDto(String msg) {
        this.msg = msg;
    }
}


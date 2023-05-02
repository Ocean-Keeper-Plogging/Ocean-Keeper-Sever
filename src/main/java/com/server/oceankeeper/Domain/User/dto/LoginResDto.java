package com.server.oceankeeper.Domain.User.dto;

import com.server.oceankeeper.Domain.User.User;
import lombok.Builder;
import lombok.Data;


@Data
public class LoginResDto{

    private final String msg;
    private final String jwtToken;

    @Builder

    public LoginResDto(String msg, String jwtToken) {
        this.msg = msg;
        this.jwtToken = jwtToken;
    }
}


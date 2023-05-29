package com.server.oceankeeper.domain.user.dto;

import lombok.Builder;
import lombok.Data;


@Data
public class LoginResDto{
    private final String msg;
    private final TokenInfo token;


    @Builder
    public LoginResDto(String msg, TokenInfo token) {
        this.msg = msg;
        this.token = token;
    }

//    @Builder
//    public LoginResDto(String msg, String jwtToken) {
//        this.msg = msg;
//        this.jwtToken = jwtToken;
//    }
}


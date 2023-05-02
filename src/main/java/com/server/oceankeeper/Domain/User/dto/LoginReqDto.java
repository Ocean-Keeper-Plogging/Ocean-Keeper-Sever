package com.server.oceankeeper.Domain.User.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class LoginReqDto {

    private String providerId;
    private String provider;

    public LoginReqDto() {
    }

    @Builder
    public LoginReqDto(String providerId, String provider) {
        this.providerId = providerId;
        this.provider = provider;
    }
}

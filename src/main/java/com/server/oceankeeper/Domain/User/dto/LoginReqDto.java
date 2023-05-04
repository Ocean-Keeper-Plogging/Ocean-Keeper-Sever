package com.server.oceankeeper.Domain.User.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class LoginReqDto {
    @NotEmpty

    private String providerId;
    @NotEmpty
    private String provider;

    public LoginReqDto() {
    }

    @Builder
    public LoginReqDto(String providerId, String provider) {
        this.providerId = providerId;
        this.provider = provider;
    }
}

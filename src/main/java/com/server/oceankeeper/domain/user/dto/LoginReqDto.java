package com.server.oceankeeper.domain.user.dto;

import lombok.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.validation.constraints.NotEmpty;

@Data
public class LoginReqDto {
    @NotEmpty
    private final String providerId;
    @NotEmpty
    private final String provider;
    @NotEmpty
    private final String deviceToken;

    @Builder
    public LoginReqDto(String providerId, String provider, String deviceToken) {
        this.providerId = providerId;
        this.provider = provider;
        this.deviceToken = deviceToken;
    }

    public UsernamePasswordAuthenticationToken toAuthentication(String password) {
        return new UsernamePasswordAuthenticationToken(provider + "_" + providerId, password);
    }
}

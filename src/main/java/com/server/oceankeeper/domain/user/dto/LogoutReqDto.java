package com.server.oceankeeper.domain.user.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import javax.validation.constraints.NotEmpty;

@Data
public class LogoutReqDto {
    @ApiModelProperty(value = "oauth provider id",example = "adfjanvjn1jnkjnvah", required = true)
    @NotEmpty
    private final String providerId;
    @ApiModelProperty(value = "oauth provider",example = "naver", required = true)
    @NotEmpty
    private final String provider;
    @ApiModelProperty(value = "기기 디바이스 토큰",example = "anvandsjkvnbh1bsaadvc", required = true)
    @NotEmpty
    private final String deviceToken;

    @Builder
    public LogoutReqDto(String providerId, String provider, String deviceToken) {
        this.providerId = providerId;
        this.provider = provider;
        this.deviceToken = deviceToken;
    }

    public UsernamePasswordAuthenticationToken toAuthentication(String password) {
        return new UsernamePasswordAuthenticationToken(provider + "_" + providerId, password);
    }
}

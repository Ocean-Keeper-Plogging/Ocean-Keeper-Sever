package com.server.oceankeeper.domain.user.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@Builder
@Data
public class TokenRequestDto {
    @NotEmpty
    @ApiModelProperty(value = "토큰 grant type", example = "Bearer", required = true)
    private final String grantType;

    @NotEmpty
    @ApiModelProperty(value = "access token",
            example = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJuYXZlcjIxMzIxX3Byb3ZpZGVyaWQxMjMxMjMiLC" +
                    "JhdXRoIjoiUk9MRV9VU0VSIiwiZXhwIjoxNjg1Mzc4NDc2fQ.cZca1Gq_062hMWQ9RIoxq8INtj7H" +
                    "1qpNmOUEG8uwr63pVOYvbDR093OcAkveL-o16kog-RXnjo1Gxc1Xd7lgzw",
            required = true)
    private final String accessToken;

    @NotEmpty
    @ApiModelProperty(value = "refresh token", notes = "access token 재발급시 사용",
            example = "eyJhbGciOiJIUzUxMiJ9.eyJleHAiOjE2ODU1NDk0NzZ9.QqvtgIcSgYtZlMKjK3JgR8K5" +
                    "m53_duw7YFs_glVLKK6KJZk2BrnagrM_27dKbkmCOKQOYxZTiczxdIFJ8nPK7w", required = true)
    private final String refreshToken;

    @NotNull
    @ApiModelProperty(value = "access token 만료 시각", example = "1685378476451", dataType = "long", required = true)
    private final Long accessTokenExpiresIn;
}

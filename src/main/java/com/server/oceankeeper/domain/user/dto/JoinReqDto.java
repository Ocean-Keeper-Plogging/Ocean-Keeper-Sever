package com.server.oceankeeper.domain.user.dto;

import com.server.oceankeeper.domain.user.entitiy.OUser;
import com.server.oceankeeper.domain.user.entitiy.UserRole;
import com.server.oceankeeper.domain.user.entitiy.UserStatus;
import com.server.oceankeeper.util.UUIDGenerator;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Data
public class JoinReqDto {
    @ApiModelProperty(
            value = "Oauth Provider",
            dataType = "string",
            example = "naver",
            required = true)
    @NotEmpty
    private final String provider;

    @ApiModelProperty(
            value = "Oauth Provider Id",
            dataType = "string",
            required = true
    )
    @NotEmpty
    private final String providerId;

    @Pattern(regexp = "^[a-zA-Z0-9가-힣]{2,20}$", message = "한글/영문/숫자로만 구성된 2~20자 이내의 닉네임을 사용해주세요")
    @ApiModelProperty(
            value = "닉네임",
            notes = "한글/영문/숫자로만 구성된 2~20자 이내의 닉네임",
            dataType = "string",
            required = true
    )
    @NotEmpty
    private final String nickname;

    @Pattern(regexp = "^(?:[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}|)$", message = "유효한 이메일 형식으로 작성해주세요")
    @ApiModelProperty(
            value = "이메일",
            dataType = "string"
    )
    private final String email;

    @NotEmpty
    @ApiModelProperty(
            value = "S3 profile 이미지 경로",
            dataType = "string",
            required = true
    )
    private final String profile;

    @NotEmpty
    @ApiModelProperty(
            value = "디바이스 토큰",
            dataType = "string",
            required = true
    )
    private final String deviceToken;

    @Builder
    public JoinReqDto(String provider, String providerId, String nickname, String email, String profile, String deviceToken) {
        this.provider = provider;
        this.providerId = providerId;
        this.nickname = nickname;
        this.email = email;
        this.profile = profile;
        this.deviceToken = deviceToken;
    }

    public OUser toEntity() {
        return OUser.builder()
                .uuid(UUIDGenerator.createUuid())
                .provider(provider)
                .providerId(providerId)
                .nickname(nickname)
                .email(email == null ? "" : email)
                .profile(profile)
                .status(UserStatus.ACTIVE)
                .role(UserRole.USER)
                .deviceToken(deviceToken)
                .alarm(true)
                .build();
    }
}
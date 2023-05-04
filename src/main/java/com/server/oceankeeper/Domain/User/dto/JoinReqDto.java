package com.server.oceankeeper.Domain.User.dto;

import com.server.oceankeeper.Domain.User.User;
import com.server.oceankeeper.Domain.User.UserEnum.UserRole;
import com.server.oceankeeper.Domain.User.UserEnum.UserStatus;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Data
public class JoinReqDto{
    @ApiModelProperty(
            value = "Oauth Provider",
            dataType = "String",
            example = "naver")
    @NotEmpty
    private final String provider;
    @ApiModelProperty(
            value = "Oauth Provider Id",
            dataType = "String"
    )
    @NotEmpty
    private final String providerId;

    @Pattern(regexp = "^[a-zA-Z0-9]{2,20}$", message="영문과 숫자로만 구성된 2~20자 이내의 닉네임을 사용해주세요")
    @NotEmpty
    private final String nickname;



    @Pattern(regexp = "^[a-zA-Z0-9]{2,10}@[a-zA-Z0-9]{2,6}\\.[a-zA-Z]{2,3}$", message = "유효한 이메일 형식으로 작성해주세요")
    @NotEmpty
    private final String email;
    @NotEmpty
    private final String profile;

    @NotEmpty
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


    public User toEntity(){
        return User.builder()
                .provider(provider)
                .providerId(providerId)
                .nickname(nickname)
                .email(email)
                .profile(profile)
                .status(UserStatus.ACTIVE)
                .role(UserRole.USER)
                .deviceToken(deviceToken)
                .build();
    }
}
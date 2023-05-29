package com.server.oceankeeper.domain.user.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Data
public class UserIdAndNicknameReqDto {
    @ApiModelProperty(
            value = "유저 아이디",
            dataType = "String",
            example = "e17555251202464fb5ce0435312f1841")
    @NotEmpty
    private final String userId;
    @ApiModelProperty(
            value = "변경할 신규 닉네임",
            dataType = "String",
            example = "NewNickName")
    @Pattern(regexp = "^[a-zA-Z0-9가-힣]{2,20}$", message="한글/영문/숫자로만 구성된 2~20자 이내의 닉네임을 사용해주세요")
    @NotEmpty
    private final String nickname;
}

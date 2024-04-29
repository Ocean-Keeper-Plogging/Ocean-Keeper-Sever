package com.server.oceankeeper.domain.user.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Data
public class UserIdAndNicknameReqDto {
    @ApiModelProperty(
            value = "유저 아이디",
            dataType = "string",
            example = "e17555251202464fb5ce0435312f1841",
            required = true)
    @NotEmpty
    private final String userId;
    @ApiModelProperty(
            value = "변경할 신규 닉네임",
            dataType = "string",
            notes = "한글/영문/숫자로만 구성된 2~20자 이내의 닉네임 필요",
            example = "NewNickName",
            required = true)
    @Pattern(regexp = "^[a-zA-Z0-9가-힣ㄱ-ㅎㅏ-ㅣ_\\-:/()#,@\\[\\]=&;{}!$*+ ]{2,8}$",
            message = "한글/영문/숫자/공백/특수문자 _-:/()#,@[]+=&;{}!$*로만 구성된 2~8자 이내의 닉네임을 사용해주세요")
    @NotEmpty
    private final String nickname;
}

package com.server.oceankeeper.domain.user.dto;

import com.server.oceankeeper.domain.user.entitiy.OUser;
import com.server.oceankeeper.util.UUIDGenerator;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class JoinResDto{
    @ApiModelProperty(
            value = "유저 아이디",
            example = "11ee03f57b24205d895965850c8e95ef"
    )
    private final String id;
    @ApiModelProperty(
            value = "유저 닉네임",
            example = "park"
    )
    private final String nickname;

    public JoinResDto(OUser user) {
        this.id = UUIDGenerator.changeUuidToString(user.getUuid());
        this.nickname = user.getNickname();
    }
}

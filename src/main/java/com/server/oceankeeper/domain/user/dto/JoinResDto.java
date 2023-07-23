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
            example = "11ee2962ed293b2a869b0f30e7d4f7c1"
    )
    private final String id;
    @ApiModelProperty(
            value = "유저 닉네임",
            example = "user1"
    )
    private final String nickname;

    public JoinResDto(OUser user) {
        this.id = UUIDGenerator.changeUuidToString(user.getUuid());
        this.nickname = user.getNickname();
    }
}

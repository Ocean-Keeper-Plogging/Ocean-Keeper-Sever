package com.server.oceankeeper.domain.user.dto;

import com.server.oceankeeper.domain.user.entitiy.OUser;
import com.server.oceankeeper.util.UUIDGenerator;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class JoinResDto{
    private final String id;
    private final String nickname;

    public JoinResDto(OUser user) {
        this.id = UUIDGenerator.changeUuidToString(user.getUuid());
        this.nickname = user.getNickname();
    }
}

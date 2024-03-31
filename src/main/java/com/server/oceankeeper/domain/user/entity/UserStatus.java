package com.server.oceankeeper.domain.user.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum UserStatus {
    ACTIVE("활동중"),WITHDRAW("탈퇴함");

    private String value;

}

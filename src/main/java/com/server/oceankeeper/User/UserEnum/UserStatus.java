package com.server.oceankeeper.User.UserEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum UserStatus {
    ACTIVE("활동중"),WITHDRAW("탈퇴함");

    private String value;

}

package com.server.oceankeeper.Domain.User.UserEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum UserRole {
    ADMIN("관리자"), USER("일반 사용자");

    private String value;
}

package com.server.oceankeeper.domain.user.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum UserRole {
    ADMIN("관리자"), USER("일반 사용자");

    private String value;
}

package com.server.oceankeeper.domain.activity.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LocationTag {
    WEST("서해번쩍"),
    EAST("동해번쩍"),
    SOUTH("남해번쩍"),
    JEJU("제주번쩍"),
    ETC("기타"),
    EMPTY("제거됨");

    private final String value;
}

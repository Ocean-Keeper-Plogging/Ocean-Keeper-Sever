package com.server.oceankeeper.domain.activity.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GarbageCategory {
    COASTAL("연안쓰레기"),
    FLOATING("부유쓰레기"),
    DEPOSITED("침적쓰레기"),
    ETC("기타");

    private final String value;
}

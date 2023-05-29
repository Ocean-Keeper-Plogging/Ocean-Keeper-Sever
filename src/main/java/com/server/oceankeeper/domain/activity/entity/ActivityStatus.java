package com.server.oceankeeper.domain.activity.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ActivityStatus {
    OPEN("모집중"),
    PARTICIPATED("참여중"),
    CANCEL("활동취소"),
    CLOSE("활동종료");

    private final String value;
}

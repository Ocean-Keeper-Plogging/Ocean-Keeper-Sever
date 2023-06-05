package com.server.oceankeeper.domain.crew;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CrewStatus {
    APPLY("신청"),
    IN_PROGRESS("참여중"),
    CANCEL("참여취소"),
    CLOSED("정상종료"),
    EXPIRED("방출");

    private final String value;
}

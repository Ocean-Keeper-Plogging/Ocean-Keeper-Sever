package com.server.oceankeeper.domain.crew.entitiy;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum CrewStatus {
    IN_PROGRESS("IN_PROGRESS", "참여중"),
    CLOSED("CLOSED", "정상종료"),
    NO_SHOW("NO_SHOW", "노쇼"),
    REJECT("REJECT", "방출"),
    CANCEL("CANCEL", "취소");

    private final String value;
    private final String meaning;

    //There is only one case to set crew status as "reject","no-show"
    public static CrewStatus getLimitedStatus(String status) {
        return Stream.of(REJECT, NO_SHOW, IN_PROGRESS)
                .filter(st -> st.value.equalsIgnoreCase(status))
                .findAny()
                .orElse(null);
    }
}

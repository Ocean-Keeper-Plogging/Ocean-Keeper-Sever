package com.server.oceankeeper.domain.activity.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ActivityStatus {
    OPEN("모집중"),
    CLOSED("활동종료"),
    RECRUITMENT_CLOSE("모집종료");

    private final String value;

    public static ActivityStatus getStatus(String status) {
        if (status == null) return null;
        switch (status) {
            case "open":
                return OPEN;
            case "closed":
                return CLOSED;
            case "recruitment-closed":
                return RECRUITMENT_CLOSE;
            default:
                return null;
        }
    }
}

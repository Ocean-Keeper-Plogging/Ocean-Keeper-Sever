package com.server.oceankeeper.domain.activity.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public enum ActivityStatus {
    OPEN("모집중"),
    CLOSED("활동종료"),
    RECRUITMENT_CLOSE("모집종료"),
    CANCEL("활동취소");

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
            case "cancel":
                return CANCEL;
            default:
                return null;
        }
    }

    public static ActivityStatus getActivityStatus(LocalDate recruitEndAt, LocalDateTime startAt) {
        if (LocalDateTime.now().isBefore(recruitEndAt.atStartOfDay())) {
            if (LocalDateTime.now().isBefore(startAt))
                return ActivityStatus.OPEN;
            else
                return ActivityStatus.RECRUITMENT_CLOSE;
        }
        return ActivityStatus.CLOSED;
    }
}

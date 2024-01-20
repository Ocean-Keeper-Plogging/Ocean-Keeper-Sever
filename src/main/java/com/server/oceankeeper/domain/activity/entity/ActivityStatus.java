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
            case "close":
                return CLOSED;
            case "recruitment-closed":
            case "recruitment-close":
            case "recruitment_closed":
            case "recruitment_close":
                return RECRUITMENT_CLOSE;
            case "cancel":
                return CANCEL;
            default:
                return null;
        }
    }

    public static ActivityStatus getActivityStatus(LocalDate recruitEndAt, LocalDateTime startAt) {
        if (LocalDateTime.now().isBefore(recruitEndAt.plusDays(1).atStartOfDay()))
            return ActivityStatus.OPEN;
        if (LocalDateTime.now().isBefore(startAt))
            return ActivityStatus.RECRUITMENT_CLOSE;
        return ActivityStatus.CLOSED;
    }
}

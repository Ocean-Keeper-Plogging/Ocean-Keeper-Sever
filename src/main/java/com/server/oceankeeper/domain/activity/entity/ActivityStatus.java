package com.server.oceankeeper.domain.activity.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ActivityStatus {
    OPEN("모집중"),
    CLOSE("활동종료"),
    CANCEL("활동취소"),
    ALL("전체");

    private final String value;

    public static ActivityStatus getStatus(String status){
        switch (status){
            case "open":return OPEN;
            case "closed":return CLOSE;
            case "cancel":return CANCEL;
            case "all":return ALL;
            default:return null;
        }
    }
}

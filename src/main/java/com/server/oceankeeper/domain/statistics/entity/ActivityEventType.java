package com.server.oceankeeper.domain.statistics.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ActivityEventType {
    USER_JOINED_EVENT("회원 가입"),
    ACTIVITY_REGISTRATION_EVENT("활동 등록"),
    ACTIVITY_REGISTRATION_CANCEL_EVENT("활동 모집 취소"),
    ACTIVITY_PARTICIPATION_EVENT("활동 참여"),
    ACTIVITY_PARTICIPATION_CANCEL_EVENT("활동 참여 취소"),
    ACTIVITY_NO_SHOW_EVENT("활동 노쇼");

    private final String value;
}
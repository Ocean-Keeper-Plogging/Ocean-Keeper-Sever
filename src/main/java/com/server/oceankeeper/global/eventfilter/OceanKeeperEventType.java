package com.server.oceankeeper.global.eventfilter;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OceanKeeperEventType {
    USER_JOINED_EVENT("회원 가입"),
    MESSAGE_SENT_EVENT("메세지 발송"),
    ACTIVITY_REGISTRATION_EVENT("활동 등록"),
    ACTIVITY_REGISTRATION_CANCEL_EVENT("활동 모집 취소"),
    ACTIVITY_PARTICIPATION_EVENT("활동 참여"),
    ACTIVITY_PARTICIPATION_CANCEL_EVENT("활동 참여 취소"),
    ACTIVITY_NO_SHOW_EVENT("활동 노쇼");

    private final String value;
}
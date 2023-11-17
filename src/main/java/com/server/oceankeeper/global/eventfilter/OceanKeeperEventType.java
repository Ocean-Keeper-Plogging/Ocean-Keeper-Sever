package com.server.oceankeeper.global.eventfilter;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OceanKeeperEventType {
    USER_JOINED_EVENT("회원 가입"),
    NICKNAME_CHANGE_EVENT("닉네임 변경 이벤트"),
    MESSAGE_SENT_EVENT("새로운 쪽지가 도착했습니다."),

    ACTIVITY_RECRUITMENT_CLOSED_EVENT("키퍼님이 신청한 프로젝트의 모집이 종료됐습니다."),
    ACTIVITY_START_SOON_EVENT("키퍼님이 신청한 프로젝트가 1시간 뒤 시작됩니다."),
    ACTIVITY_REGISTRATION_EVENT("활동 등록"),
    ACTIVITY_REGISTRATION_CANCEL_EVENT("키퍼님이 신청한 프로젝트가 취소됐습니다."),
    ACTIVITY_CHANGED_EVENT("키퍼님이 신청한 프로젝트에 변경내역이 있습니다."),
    ACTIVITY_PARTICIPATION_EVENT("활동 참여"),
    ACTIVITY_PARTICIPATION_CANCEL_EVENT("활동 참여 취소"),
    ACTIVITY_CLOSE_EVENT("활동 종료"),
    ACTIVITY_NO_SHOW_EVENT("활동 노쇼"),

    TERMS_CHANGED_EVENT("이용약관 변경내역이 있습니다."),
    NEW_NOTICE_EVENT("운영자의 새로운 공지가 올라왔습니다.");

    private final String value;
}
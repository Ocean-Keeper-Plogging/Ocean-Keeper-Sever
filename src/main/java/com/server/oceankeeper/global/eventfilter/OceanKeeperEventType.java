package com.server.oceankeeper.global.eventfilter;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OceanKeeperEventType {
    MESSAGE_SENT_EVENT("새로운 쪽지",0),
    ACTIVITY_RECRUITMENT_CLOSED_EVENT("프로젝트 모집종료",10),
    ACTIVITY_START_SOON_EVENT("1시간뒤 프로젝트 시작",11),
    ACTIVITY_REGISTRATION_CANCEL_EVENT("프로젝트 취소",12),
    ACTIVITY_CHANGED_EVENT("프로젝트 변경",13),
    ACTIVITY_REGISTRATION_EVENT("프로젝트 등록",14),
    ACTIVITY_CLOSE_EVENT("활동 종료",15),

    TERMS_CHANGED_EVENT("이용약관 변경",20),
    NEW_NOTICE_EVENT("새로운 공지",30),

    USER_JOINED_EVENT("회원 가입",40),
    USER_WITHDRAWAL_EVENT("회원 탈퇴",41),
    NICKNAME_CHANGE_EVENT("닉네임 변경 이벤트",50),

    ACTIVITY_PARTICIPATION_EVENT("활동 참여",60),
    ACTIVITY_PARTICIPATION_CANCEL_EVENT("활동 참여 취소",61),
    ACTIVITY_NO_SHOW_EVENT("활동 노쇼",62),
    ACTIVITY_INFO_DELETE_EVENT("활동 정보 삭제",63),
    ;

    private final String value;
    private final Integer no;
}
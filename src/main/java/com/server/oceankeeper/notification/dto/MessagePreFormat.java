package com.server.oceankeeper.notification.dto;

public enum MessagePreFormat {
    NEW_MESSAGE("새로운 쪽지가 도착했습니다.")
    ,ACTIVITY_END("키퍼님이 신청한 프로젝트의 모집이 종료됐습니다.")
    ,ACTIVITY_STANDBY("키퍼님이 신청한 프로젝트가 1시간 뒤 시작됩니다.")
    ,ACTIVITY_CANCEL("키퍼님이 신청한 프로젝트가 취소됐습니다.")
    ,ACTIVITY_EDIT("키퍼님이 신청한 프로젝트에 변경내역이 있습니다.")
    ,GUIDE_EDIT("이용약관 변경내역이 있습니다.")
    ,NEW_NOTICE("운영자의 새로운 공지가 올라왔습니다.")
    ;

    private final String value;
    public String get(){
        return value;
    }
    MessagePreFormat(String value) {
        this.value = value;
    }
}

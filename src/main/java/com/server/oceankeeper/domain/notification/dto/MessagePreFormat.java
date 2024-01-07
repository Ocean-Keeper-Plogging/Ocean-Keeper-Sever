package com.server.oceankeeper.domain.notification.dto;

import com.server.oceankeeper.global.eventfilter.OceanKeeperEventType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Getter
public enum MessagePreFormat {
    NEW_MESSAGE("새로운 쪽지가 도착했습니다.",0)
    ,ACTIVITY_END("키퍼님이 신청한 활동의 모집이 종료됐습니다.",10)
    ,ACTIVITY_STANDBY("키퍼님이 신청한 활동이 1시간 뒤 시작됩니다.",11)
    ,ACTIVITY_CANCEL("키퍼님이 신청한 활동이 취소됐습니다.",12)
    ,ACTIVITY_EDIT("키퍼님이 신청한 활동에 변경내역이 있습니다.",13)
    ,GUIDE_EDIT("이용약관 변경내역이 있습니다.",20)
    ,NEW_NOTICE("운영자의 새로운 공지가 올라왔습니다.",30)
    ;

    private final String value;
    private final Integer no;
    public static MessagePreFormat get(OceanKeeperEventType eventType){
        return Arrays.stream(MessagePreFormat.class.getEnumConstants())
                .filter(st -> st.no.equals(eventType.getNo()))
                .findAny()
                .orElse(null);
    }
}

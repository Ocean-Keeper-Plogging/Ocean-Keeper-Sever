package com.server.oceankeeper.domain.notification.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
public class NotificationResDto {
    private final List<NotificationData> data;
    private final Meta meta;

    @RequiredArgsConstructor
    @Getter
    public static class NotificationData {
        @ApiModelProperty(
                value = "알림 개수",
                example = "3",
                required = true
        )
        private final Long id;
        @ApiModelProperty(
                value = "알림 내용",
                example = "새로운 쪽지가 도착했습니다.",
                required = true
        )
        private final String contents;
        @ApiModelProperty(
                value = "생성일자",
                example = "1시간전은 분단위. 하루 전은 시간 단위. 그 이후로는 날짜",
                required = true
        )
        private final String createdAt;
        @ApiModelProperty(
                value = "읽음 여부",
                example = "true,false 한번 조회한 이후로는 무조건 true",
                required = true
        )
        private final boolean isRead;
    }

    @Getter
    @RequiredArgsConstructor
    public static class Meta {
        @ApiModelProperty(
                value = "알림 개수",
                example = "3",
                required = true
        )
        private final Integer size;
        @ApiModelProperty(
                value = "남은 페이지가 있는지 여부",
                example = "false",
                required = true
        )
        private final boolean isLast;
    }
}

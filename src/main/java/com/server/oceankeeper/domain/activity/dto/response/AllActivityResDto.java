package com.server.oceankeeper.domain.activity.dto.response;

import com.server.oceankeeper.domain.activity.entity.ActivityStatus;
import com.server.oceankeeper.domain.activity.entity.GarbageCategory;
import com.server.oceankeeper.domain.activity.entity.LocationTag;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;

@ToString
@Getter
@Setter
@RequiredArgsConstructor
public class AllActivityResDto {
    @ApiModelProperty(
            value = "추가 조회 activity id",
            required = true
    )
    private final String activityId;

    @ApiModelProperty(
            value = "활동명",
            required = true
    )
    private final String title;

    @ApiModelProperty(
            value = "지역태그",
            required = true
    )
    private final LocationTag locationTag;

    @ApiModelProperty(
            value = "쓰레기 카테고리",
            required = true
    )
    private final GarbageCategory garbageCategory;

    @ApiModelProperty(
            value = "활동 개설자 닉네임",
            required = true
    )
    private final String hostNickname;

    @ApiModelProperty(
            value = "정원",
            example = "20",
            dataType = "integer",
            required = true
    )
    private final Integer quota;

    @ApiModelProperty(
            value = "참가자 수",
            example = "10",
            dataType = "integer",
            required = true
    )
    private final Integer participants;

    @ApiModelProperty(
            value = "활동 이미지 링크",
            required = true
    )
    private final String activityImageUrl;


    @ApiModelProperty(
            value = "활동 모집 시작일",
            required = true
    )
    private final String recruitStartAt;

    @ApiModelProperty(
            value = "활동 모집 종료일",
            required = true
    )
    private final String recruitEndAt;

    @ApiModelProperty(
            value = "활동 시작 시각",
            required = true
    )
    private final String startAt;

    @ApiModelProperty(
            value = "활동 상태",
            required = true
    )
    private final ActivityStatus activityStatus;

    @ApiModelProperty(
            value = "활동 위치",
            example = "제주 제주시 한림읍 협재리 2497-1",
            required = true
    )
    private final String location;

    @ApiModelProperty(
            value = "리워드 여부 없으면 empty string",
            example = "점심 제공",
            required = true
    )
    private final String rewards;

    @ApiModelProperty(
            value = "모집 시작 여부",
            example = "시작했으면 true,시작 안했으면 false",
            required = true
    )
    private final boolean recruitmentStarted;
}

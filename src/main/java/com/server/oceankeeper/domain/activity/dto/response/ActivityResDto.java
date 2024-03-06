package com.server.oceankeeper.domain.activity.dto.response;

import com.server.oceankeeper.domain.activity.entity.GarbageCategory;
import com.server.oceankeeper.domain.activity.entity.LocationTag;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@RequiredArgsConstructor
public class ActivityResDto {
    @ApiModelProperty(
            value = "activity id",
            required = true
    )
    private final String activityId;

    @ApiModelProperty(
            value = "활동 타이틀",
            required = true
    )
    private final String title;

    @ApiModelProperty(
            value = "location 태그",
            required = true
    )
    private final LocationTag locationTag;

    @ApiModelProperty(
            value = "쓰레기 태그",
            required = true
    )
    private final GarbageCategory garbageCategory;

    @ApiModelProperty(
            value = "활동 호스트 닉네임",
            required = true
    )
    private final String hostNickname;

    @ApiModelProperty(
            value = "활동 정원",
            required = true
    )
    private final Integer quota;

    @ApiModelProperty(
            value = "현재 참여 인원",
            required = true
    )
    private final Integer participants;

    @ApiModelProperty(
            value = "활동 이미지 url",
            required = true
    )
    private final String activityImageUrl;
}

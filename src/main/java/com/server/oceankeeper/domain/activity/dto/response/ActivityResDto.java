package com.server.oceankeeper.domain.activity.dto.response;

import com.server.oceankeeper.domain.activity.entity.GarbageCategory;
import com.server.oceankeeper.domain.activity.entity.LocationTag;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@RequiredArgsConstructor
public class ActivityResDto {
    private final String activityId;

    private final String title;
    private final LocationTag locationTag;
    private final GarbageCategory garbageCategory;

    private final String hostNickname;
    private final Integer quota;
    private final Integer participants;

    private final String activityImageUrl;
}

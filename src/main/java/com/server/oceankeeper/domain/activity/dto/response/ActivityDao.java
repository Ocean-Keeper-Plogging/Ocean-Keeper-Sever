package com.server.oceankeeper.domain.activity.dto.response;

import com.server.oceankeeper.domain.activity.entity.GarbageCategory;
import com.server.oceankeeper.domain.activity.entity.LocationTag;
import lombok.*;

import java.util.UUID;

@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ActivityDao {
    private UUID activityId;

    private String title;
    private LocationTag locationTag;
    private GarbageCategory garbageCategory;

    private String hostNickname;
    private Integer quota;
    private Integer participants;

    private String activityImageUrl;
}

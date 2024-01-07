package com.server.oceankeeper.domain.activity.dao;

import com.server.oceankeeper.domain.activity.entity.GarbageCategory;
import com.server.oceankeeper.domain.activity.entity.LocationTag;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AllActivityDao {
    private UUID activityId;

    private String title;
    private LocationTag locationTag;
    private GarbageCategory garbageCategory;

    private String hostNickname;
    private Integer quota;
    private Integer participants;
    private String rewards;

    private String activityImageUrl;

    private LocalDate recruitStartAt;
    private LocalDate recruitEndAt;
    private LocalDateTime startAt;

    private String location;
}

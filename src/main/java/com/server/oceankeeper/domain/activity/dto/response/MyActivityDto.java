package com.server.oceankeeper.domain.activity.dto.response;

import com.server.oceankeeper.domain.activity.entity.ActivityStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class MyActivityDto {
    private final List<MyActivityDetail> activities;

    @Data
    @AllArgsConstructor
    public static class MyActivityDetail {
        private final String activityId;

        private final String title;

        private final String hostNickname;
        private final Integer quota;
        private final Integer participants;

        private String activityImageUrl;

        private final LocalDate recruitStartAt;
        private final LocalDate recruitEndAt;

        private final LocalDateTime startAt;

        private final ActivityStatus status;

        private String address;
    }
}

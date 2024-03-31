package com.server.oceankeeper.domain.activity.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.server.oceankeeper.domain.activity.entity.ActivityStatus;
import com.server.oceankeeper.domain.crew.entity.CrewStatus;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class MyActivityDto {
    private final List<MyActivityDetail> activities;
    private final Meta meta;

    @Data
    @AllArgsConstructor
    public static class MyActivityDetail {
        @ApiModelProperty(
                value = "활동 아이디",
                required = true
        )
        private final String activityId;

        @ApiModelProperty(
                value = "활동명",
                required = true
        )
        private final String title;

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
                value = "활동 참여인원",
                required = true
        )
        private final Integer participants;

        @ApiModelProperty(
                value = "활동 이미지 url"
        )
        private String activityImageUrl;

        @ApiModelProperty(
                value = "활동 모집 시작일",
                required = true
        )
        private final LocalDate recruitStartAt;
        @ApiModelProperty(
                value = "활동 모집 종료일",
                required = true
        )
        private final LocalDate recruitEndAt;

        @ApiModelProperty(
                value = "활동 시작 시각",
                required = true
        )
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm", timezone = "Asia/Seoul")
        private final LocalDateTime startAt;

        @ApiModelProperty(
                value = "활동의 상태. OPEN:모집 진행, RECRUITMENT_CLOSE:모집 종료, CLOSE:활동 종료",
                required = true
        )
        private final ActivityStatus status;

        @ApiModelProperty(
                value = "활동 위치"
        )
        private String address;

        @ApiModelProperty(
                value = "크루원일 경우 지원서 아이디. 호스트일경우 빈 문자열",
                required = true
        )
        private final String applicationId;
        private final String role;
        @ApiModelProperty(
                value = "crew 상태. IN_PROGRESS: 참여중, CANCEL:취소, CLOSED:참여, REJECT:방출 ",
                required = true
        )
        private final CrewStatus crewStatus;

        @ApiModelProperty(
                value = "거절사유. 없을경우 빈 문자열",
                example = "너무 많은 미참여로 거절합니다."
        )
        private String rejectReason;
    }

    @Getter
    @RequiredArgsConstructor
    public static class Meta {
        @ApiModelProperty(
                value = "activity 개수",
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

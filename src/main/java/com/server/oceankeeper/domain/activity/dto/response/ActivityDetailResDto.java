package com.server.oceankeeper.domain.activity.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.server.oceankeeper.domain.activity.dto.request.LocationDto;
import com.server.oceankeeper.domain.activity.entity.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

@ToString
@Getter
@Setter
@RequiredArgsConstructor
@Builder
public class ActivityDetailResDto {
    @ApiModelProperty(
            value = "활동명",
            required = true
    )
    @NotEmpty
    private final String title;

    @ApiModelProperty(
            value = "위치",
            required = true
    )
    @NotNull
    private final LocationDto location;

    @ApiModelProperty(
            value = "교통 안내 여부",
            example = "카셰어링 연결 예정",
            required = true
    )
    @NotEmpty
    private final String transportation;

    @ApiModelProperty(
            value = "모집 카테고리 선택",
            example = "연안 쓰레기",
            required = true
    )
    @NotNull
    private final GarbageCategory garbageCategory;

    @ApiModelProperty(
            value = "모집 지역 선택",
            example = "제주번쩍",
            required = true
    )
    @NotNull
    private final LocationTag locationTag;

    @ApiModelProperty(
            value = "모집 기간 시작일",
            example = "2023-07-11T00:00:00",
            required = true
    )
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private final LocalDate recruitStartAt;

    @ApiModelProperty(
            value = "모집 기간 종료",
            example = "2023-07-13T23:59:59",
            required = true
    )
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private final LocalDate recruitEndAt;

    @ApiModelProperty(
            value = "활동 시작일",
            example = "2023-07-20T13:00:00",
            required = true
    )
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'hh:mm:ss", timezone = "Asia/Seoul")
    private final LocalDateTime startAt;

    @ApiModelProperty(
            value = "썸네일 사진 s3 링크",
            example = "https://s3-backend-git.s3.ap-northeast-2.amazonaws.com/profile/5a13e770-ed8c-42ad-b19d-2d0f756aa2adawesomeface.png",
            required = true
    )
    private final String thumbnailUrl;

    @ApiModelProperty(
            value = "모집 키퍼 소개글",
            example = "안녕하세요.",
            required = true
    )
    @NotEmpty
    private final String keeperIntroduction;

    @ApiModelProperty(
            value = "모집 키퍼 사진 s3 링크",
            example = "https://s3-backend-git.s3.ap-northeast-2.amazonaws.com/profile/5a13e770-ed8c-42ad-b19d-2d0f756aa2adawesomeface.png",
            required = true
    )
    private final String keeperImageUrl;

    @ApiModelProperty(
            value = "활동 스토리 글",
            example = "안녕하세요. 00 활동입니다.",
            required = true
    )
    @Length(max = 10)
    @NotEmpty
    private final String activityStory;

    @ApiModelProperty(
            value = "활동 스토리 사진 s3 링크",
            example = "https://s3-backend-git.s3.ap-northeast-2.amazonaws.com/profile/5a13e770-ed8c-42ad-b19d-2d0f756aa2adawesomeface.png",
            required = true
    )
    private final String storyImageUrl;

    @ApiModelProperty(
            value = "정원",
            example = "10",
            dataType = "integer",
            required = true
    )
    @NotNull
    private final Integer quota;

    @ApiModelProperty(
            value = "활동 프로그램 안내",
            example = "12시 집결",
            required = true
    )
    private final String programDetails;

    @ApiModelProperty(
            value = "준비물",
            example = "긴 상하의"
    )
    private final String preparation;

    @ApiModelProperty(
            value = "제공 리워드",
            example = "점심 제공"
    )
    private final String rewards;

    @ApiModelProperty(
            value = "기타 안내사항",
            example = "기타 사항입니다."
    )
    private final String etc;

    @ApiModelProperty(
            value = "신청 인원",
            example = "4"
    )
    private final Integer participants;

    @ApiModelProperty(
            value = "활동 진행 상황",
            example = "OPEN,CLOSE,CANCEL"
    )
    private final ActivityStatus activityStatus;

//    public Activity toActivityEntity() {
//        return Activity.builder()
//                .uuid(UUIDGenerator.createUuid())
//                .location(location.toEntity())
//                .locationTag(locationTag)
//                .title(title)
//                .thumbnail(thumbnailUrl)
//                .garbageCategory(garbageCategory)
//                .quota(quota)
//                .participants(0)
//                .recruitStartAt(recruitStartAt)
//                .recruitEndAt(recruitEndAt)
//                .startAt(startAt)
//                .activityStatus(ActivityStatus.OPEN)
//                .build();
//    }
//
//    public ActivityDetail toActivityDetailEntity() {
//        return ActivityDetail.builder()
//                .uuid(UUIDGenerator.createUuid())
//                .activityStory(activityStory)
//                .storyImage(storyImageUrl)
//                .keeperIntroduction(keeperIntroduction)
//                .keeperImage(keeperImageUrl)
//                .transportation(transportation)
//                .programDetails(programDetails)
//                .preparation(preparation)
//                .rewards(rewards)
//                .etc(etc)
//                .build();
//    }

    public ActivityDetailResDto(Activity activity, ActivityDetail activityDetail) {
        this.location = new LocationDto(activity.getLocation());
        this.locationTag = activity.getLocationTag();
        this.title = activity.getTitle();
        this.thumbnailUrl = activity.getThumbnail();
        this.garbageCategory = activity.getGarbageCategory();
        this.quota = activity.getQuota();
        this.participants = activity.getParticipants();
        this.recruitStartAt = activity.getRecruitStartAt();
        this.recruitEndAt = activity.getRecruitEndAt();
        this.startAt = activity.getStartAt();
        this.activityStatus = activity.getActivityStatus();
        this.activityStory = activityDetail.getActivityStory();
        this.storyImageUrl = activityDetail.getStoryImage();
        this.keeperIntroduction = activityDetail.getKeeperIntroduction();
        this.keeperImageUrl = activityDetail.getKeeperImage();
        this.transportation = activityDetail.getTransportation();
        this.programDetails = activityDetail.getProgramDetails();
        this.preparation = activityDetail.getPreparation();
        this.rewards = activityDetail.getRewards();
        this.etc = activityDetail.getEtc();
    }
}

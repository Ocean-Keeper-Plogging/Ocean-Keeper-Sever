package com.server.oceankeeper.domain.activity.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.server.oceankeeper.domain.activity.entity.*;
import com.server.oceankeeper.util.UUIDGenerator;
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
public class RegisterActivityReqDto {
    @ApiModelProperty(
            value = "활동 등록 유저 아이디",
            example = "11ee2962ed293b2a869b0f30e7d4f7c1",
            required = true
    )
    @NotEmpty
    private final String userId;

    @ApiModelProperty(
            value = "활동명",
            required = true,
            example = "my 활동"
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
            value = "모집 카테고리 선택. COASTAL,FLOATING,DEPOSITED,ETC 중 하나",
            example = "COASTAL",
            required = true
    )
    @NotNull
    private final GarbageCategory garbageCategory;
    @ApiModelProperty(
            value = "모집 지역 선택 WEST,EAST,SOUTH,JEJU,ETC 중 하나",
            example = "WEST",
            required = true
    )
    @NotNull
    private final LocationTag locationTag;

    @ApiModelProperty(
            value = "모집 기간 시작일",
            example = "2023-07-11",
            required = true
    )
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private final LocalDate recruitStartAt;

    @ApiModelProperty(
            value = "모집 기간 종료",
            example = "2023-07-13",
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
            example = "https://oceankeeper-test.s3.ap-northeast-2.amazonaws.com/thumbnail/098638dd-6a9f-42ae-89ce-7cb431670b45thumbnail.jpg",
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
            example = "https://oceankeeper-test.s3.ap-northeast-2.amazonaws.com/keeper/6e3896a9-041f-4387-b9ea-31ddb95f0479awesomeface.png"
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
            example = "https://oceankeeper-test.s3.ap-northeast-2.amazonaws.com/story/b8846440-348c-463f-a356-4945eeadea02awesomeface.png"
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

    public Activity toActivityEntity() {
        return Activity.builder()
                .uuid(UUIDGenerator.createUuid())
                .location(location.toEntity())
                .locationTag(locationTag)
                .title(title)
                .thumbnail(thumbnailUrl)
                .garbageCategory(garbageCategory)
                .quota(quota)
                .participants(0)
                .recruitStartAt(recruitStartAt)
                .recruitEndAt(recruitEndAt)
                .startAt(startAt)
                .activityStatus(ActivityStatus.OPEN)
                .build();
    }

    public ActivityDetail toActivityDetailEntity() {
        return ActivityDetail.builder()
                .uuid(UUIDGenerator.createUuid())
                .activityStory(activityStory)
                .storyImage(storyImageUrl)
                .keeperIntroduction(keeperIntroduction)
                .keeperImage(keeperImageUrl)
                .transportation(transportation)
                .programDetails(programDetails)
                .preparation(preparation)
                .rewards(rewards)
                .etc(etc)
                .build();
    }
}

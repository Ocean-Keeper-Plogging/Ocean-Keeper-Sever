package com.server.oceankeeper.domain.activity.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.server.oceankeeper.domain.activity.entity.*;
import com.server.oceankeeper.util.UUIDGenerator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;
import java.time.LocalDateTime;

@ToString
@Getter
@Setter
@RequiredArgsConstructor
public class RegisterActivityReqDto {
    @NotEmpty
    private final String userId;
    @NotEmpty
    private final String title;
    @NotEmpty
    private final Location location;
    @NotEmpty
    private final String transportation;
    @NotEmpty
    private final GarbageCategory garbageCategory;
    @NotEmpty
    private final LocationTag locationTag;
    @NotEmpty
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'hh:mm:ss", timezone = "Asia/Seoul")
    private final LocalDate recruitStartAt;
    @NotEmpty
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'hh:mm:ss", timezone = "Asia/Seoul")
    private final LocalDate recruitEndAt;
    @NotEmpty
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'hh:mm:ss", timezone = "Asia/Seoul")
    private final LocalDateTime startAt;

    private final String thumbnailUrl;
    @NotEmpty
    private final String keeperIntroduction;
    private final String keeperImageUrl;

    @NotEmpty
    private final String activityStory;
    private final String storyImageUrl;

    @NotEmpty
    private final Integer quota;

    private final String programDetails;

    private final String preparation;

    private final String rewards;

    private final String etc;

    public Activity toActivityEntity() {
        return Activity.builder()
                .uuid(UUIDGenerator.createUuid())
                .location(location)
                .locationTag(locationTag)
                .title(title)
                .thumbnail(thumbnailUrl)
                .garbageCategory(garbageCategory)
                .quota(quota)
                .participants(1)
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

package com.server.oceankeeper.domain.statistics.dto;

import com.server.oceankeeper.domain.statistics.entity.ActivityInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
public class ActivityInfoResDto {
    @ApiModelProperty(value = "참여 활동 횟수")
    private final Integer activity;
    @ApiModelProperty(value = "활동 모집 횟수")
    private final Integer hosting;
    @ApiModelProperty(value = "활동 취소 횟수")
    private final Integer noShow;

    public ActivityInfoResDto(ActivityInfo activityInfo) {
        this.activity = activityInfo.getCountActivity();
        this.hosting = activityInfo.getCountHosting();
        this.noShow = activityInfo.getCountNoShow();
    }

    public ActivityInfoResDto(Integer activity, Integer hosting, Integer noShow) {
        this.activity = activity;
        this.hosting = hosting;
        this.noShow = noShow;
    }
}
package com.server.oceankeeper.domain.statistics.dto;

import com.server.oceankeeper.domain.statistics.entity.ActivityInfo;
import lombok.Data;

@Data
public class ActivityInfoResDto {
    private final Integer activity;
    private final Integer hosting;
    private final Integer noShow;

    public ActivityInfoResDto(ActivityInfo activityInfo) {
        this.activity = activityInfo.getCountActivity();
        this.hosting = activityInfo.getCountHosting();
        this.noShow = activityInfo.getCountNoShow();
    }
}
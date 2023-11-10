package com.server.oceankeeper.domain.activity.dto;

import com.server.oceankeeper.domain.activity.dto.response.ApplicationDto;
import com.server.oceankeeper.domain.statistics.dto.ActivityInfoResDto;
import com.server.oceankeeper.domain.user.dto.UserInfoDto;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class FullApplicationReqDto {
    private final UserInfoDto userInfo;
    private final ApplicationDto application;
    private final ActivityInfoResDto activityInfo;
}

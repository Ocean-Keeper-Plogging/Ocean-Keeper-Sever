package com.server.oceankeeper.domain.user.dto;

import com.server.oceankeeper.domain.activity.dto.inner.UserListDto;
import com.server.oceankeeper.domain.activity.entity.Activity;
import com.server.oceankeeper.domain.user.entitiy.OUser;
import lombok.Data;

@Data
public class UserAndActivityDto {
    private final OUser user;
    private final Activity activity;
    private final UserListDto crews;
}

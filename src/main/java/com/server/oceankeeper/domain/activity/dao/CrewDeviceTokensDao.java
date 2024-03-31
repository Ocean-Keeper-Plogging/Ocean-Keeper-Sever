package com.server.oceankeeper.domain.activity.dao;

import com.server.oceankeeper.domain.user.entity.OUser;
import lombok.Data;

@Data
public class CrewDeviceTokensDao {
    private final OUser user;
}

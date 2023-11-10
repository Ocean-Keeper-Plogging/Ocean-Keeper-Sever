package com.server.oceankeeper.domain.activity.dao;

import com.server.oceankeeper.domain.crew.entitiy.CrewStatus;
import lombok.Data;

import java.util.UUID;

@Data
public class CrewInfoDetailDao {
    private final String username;
    private final String nickname;
    private final CrewStatus crewStatus;
    private final UUID applicationId;
}

package com.server.oceankeeper.domain.activity.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class CrewInfoDao {
    private final UUID uuid;
    private final String title;
    private final String nickname;
}

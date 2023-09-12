package com.server.oceankeeper.domain.activity.dto;

import com.server.oceankeeper.domain.crew.entitiy.CrewRole;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class HostActivityDao {
    private final UUID uuid;
    private final String title;
}

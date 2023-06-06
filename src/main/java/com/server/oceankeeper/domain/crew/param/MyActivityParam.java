package com.server.oceankeeper.domain.crew.param;

import com.server.oceankeeper.domain.crew.entitiy.CrewStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class MyActivityParam {
    private final LocalDateTime now;
    private final UUID uuid;
    private final CrewStatus crewStatus;
}

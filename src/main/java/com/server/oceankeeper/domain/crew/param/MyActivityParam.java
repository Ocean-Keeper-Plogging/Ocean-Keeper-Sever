package com.server.oceankeeper.domain.crew.param;

import com.server.oceankeeper.domain.crew.entitiy.CrewStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
public class MyActivityParam {
    private final LocalDate time;
    private final UUID userUuid;
    private final CrewStatus crewStatus;
}

package com.server.oceankeeper.domain.crew.param;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class MyActivityParam {
    private final LocalDateTime time;
    private final UUID userUuid;
}

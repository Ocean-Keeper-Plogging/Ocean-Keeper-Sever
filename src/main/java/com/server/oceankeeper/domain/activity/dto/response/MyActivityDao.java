package com.server.oceankeeper.domain.activity.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@ToString
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MyActivityDao {
    private UUID uuid;
    private String title;
    private LocalDateTime startAt;
    private String location;
}

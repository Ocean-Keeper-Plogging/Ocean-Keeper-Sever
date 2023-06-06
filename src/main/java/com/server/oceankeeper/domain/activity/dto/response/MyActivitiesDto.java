package com.server.oceankeeper.domain.activity.dto.response;

import lombok.*;

import java.util.List;

@ToString
@Getter
@AllArgsConstructor
public class MyActivitiesDto {
    private final List<MyActivityDto> activities;
}

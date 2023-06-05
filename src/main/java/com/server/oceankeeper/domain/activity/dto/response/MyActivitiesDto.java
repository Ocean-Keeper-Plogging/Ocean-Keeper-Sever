package com.server.oceankeeper.domain.activity.dto.request;

import lombok.*;

import java.util.List;

@ToString
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MyActivitiesDto {
    private List<MyActivityDto> activities;
}

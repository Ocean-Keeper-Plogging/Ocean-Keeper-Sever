package com.server.oceankeeper.domain.activity.dto.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@ToString
@Getter
@Setter
@RequiredArgsConstructor
public class MyActivitiesDto {
    private final List<MyActivityDto> activities;
}

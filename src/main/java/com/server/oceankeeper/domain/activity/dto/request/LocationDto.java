package com.server.oceankeeper.domain.activity.dto.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@RequiredArgsConstructor
public class LocationDto {
    private final String location;
    private final String detail;
}

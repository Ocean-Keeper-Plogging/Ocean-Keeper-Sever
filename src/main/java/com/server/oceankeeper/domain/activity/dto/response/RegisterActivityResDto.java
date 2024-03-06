package com.server.oceankeeper.domain.activity.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@RequiredArgsConstructor
public class RegisterActivityResDto {
    private final String activityId;
}

package com.server.oceankeeper.domain.activity.dto.request;

import com.server.oceankeeper.domain.activity.entity.GarbageCategory;
import com.server.oceankeeper.domain.activity.entity.LocationTag;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;

@RequiredArgsConstructor
@Getter
@Setter
@ToString
public class AllActivityReqDto {
    @NotEmpty
    private final String status;
    private final LocationTag locationTag;
    private final GarbageCategory garbageCategory;
}

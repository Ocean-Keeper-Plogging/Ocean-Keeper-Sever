package com.server.oceankeeper.domain.activity.dto.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@ToString
@Getter
@Setter
@RequiredArgsConstructor
public class GetActivityResDto {
    private final List<AllActivityResDto> activities;
    private final Meta meta;

    @Getter
    @RequiredArgsConstructor
    public static class Meta {
        @ApiModelProperty(
                value = "activity 개수",
                example = "3",
                required = true
        )
        private final Integer size;
        @ApiModelProperty(
                value = "남은 페이지가 있는지 여부",
                example = "false",
                required = true
        )
        private final boolean isLast;
    }
}

package com.server.oceankeeper.domain.guide.dto.response;

import com.server.oceankeeper.domain.guide.dto.GuideDao;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
public class GuideListResDto {
    private final List<GuideDao> guides;
    private final Meta meta;
    @Getter
    @RequiredArgsConstructor
    public static class Meta {
        @ApiModelProperty(
                value = "guide 개수",
                example = "3",
                required = true
        )
        private final Integer size;
        @ApiModelProperty(
                value = "남은 이용가이드 페이지가 있는지 여부",
                example = "false",
                required = true
        )
        private final boolean isLast;
    }
}

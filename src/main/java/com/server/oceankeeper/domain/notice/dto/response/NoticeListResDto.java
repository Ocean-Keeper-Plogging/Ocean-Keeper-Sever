package com.server.oceankeeper.domain.notice.dto.response;

import com.server.oceankeeper.domain.notice.dto.NoticeDao;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
public class NoticeListResDto {
    private final List<NoticeDto> notices;
    private final Meta meta;
    @Getter
    @RequiredArgsConstructor
    public static class Meta {
        @ApiModelProperty(
                value = "notice 개수",
                example = "3",
                required = true
        )
        private final Integer size;
        @ApiModelProperty(
                value = "남은 공지사항 페이지가 있는지 여부",
                example = "false",
                required = true
        )
        private final boolean isLast;
    }
}

package com.server.oceankeeper.domain.notice.dto.request;

import com.server.oceankeeper.domain.notice.entity.Notice;
import lombok.Data;

@Data
public class NoticeReqDto {
    private final String contents;
    private final String title;

    public Notice toEntity(){
        return Notice.builder()
                .contents(contents)
                .title(title)
                .build();
    }
}

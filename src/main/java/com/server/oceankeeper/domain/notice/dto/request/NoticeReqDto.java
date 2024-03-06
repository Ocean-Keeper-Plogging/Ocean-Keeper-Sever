package com.server.oceankeeper.domain.notice.dto.request;

import com.server.oceankeeper.domain.notice.entity.Notice;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NoticeReqDto {
    private final String contents;
    private final String title;

    public Notice toEntity(){
        String newContents = contents.replaceAll("\\\\", "");
        return Notice.builder()
                .contents(newContents)
                .title(title)
                .createdAt(LocalDateTime.now())
                .build();
    }
}

package com.server.oceankeeper.domain.guide.dto.request;

import com.server.oceankeeper.domain.guide.entity.Guide;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GuideReqDto {
    private final String videoLink;
    private final String videoName;
    private final String title;

    public Guide toEntity(){
        return Guide.builder()
                .videoLink(videoLink)
                .videoName(videoName)
                .title(title)
                .createdAt(LocalDateTime.now())
                .build();
    }
}

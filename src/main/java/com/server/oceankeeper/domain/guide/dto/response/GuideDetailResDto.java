package com.server.oceankeeper.domain.guide.dto.response;

import com.server.oceankeeper.domain.guide.entity.Guide;
import lombok.Data;

import java.time.LocalDate;

@Data
public class GuideDetailResDto {
    private final Long id;
    private final String title;
    private final String contents;
    private final LocalDate createdAt;
    private final LocalDate modifiedAt;

    public static GuideDetailResDto fromEntity(Guide notice){
        return new GuideDetailResDto(
                notice.getId(),
                notice.getTitle(),
                notice.getVideoLink(),
                notice.getCreatedAt().toLocalDate(),
                notice.getUpdatedAt().toLocalDate());
    }
}

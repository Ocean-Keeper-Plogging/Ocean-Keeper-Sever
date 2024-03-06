package com.server.oceankeeper.domain.guide.dto.response;

import com.server.oceankeeper.domain.guide.entity.Guide;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Data
@RequiredArgsConstructor
public class GuideResDto {
    private final Long id;
    private final String title;
    private final LocalDate createdAt;

    public static GuideResDto fromEntity(Guide notice){
        return new GuideResDto(
                notice.getId(),
                notice.getTitle(),
                notice.getCreatedAt().toLocalDate()
                );
    }
}

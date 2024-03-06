package com.server.oceankeeper.domain.notice.dto.response;

import com.server.oceankeeper.domain.notice.entity.Notice;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Data
@RequiredArgsConstructor
public class NoticeResDto {
    private final Long id;
    private final String title;
    private final LocalDate createdAt;

    public static NoticeResDto fromEntity(Notice notice){
        return new NoticeResDto(
                notice.getId(),
                notice.getTitle(),
                notice.getCreatedAt().toLocalDate()
                );
    }
}

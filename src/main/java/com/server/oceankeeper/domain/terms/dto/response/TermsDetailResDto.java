package com.server.oceankeeper.domain.terms.dto.response;

import com.server.oceankeeper.domain.terms.entity.Terms;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TermsDetailResDto {
    private final Long id;
    private final String contents;
    private final LocalDate createdAt;

    public static TermsDetailResDto fromEntity(Terms terms){
        return new TermsDetailResDto(
                terms.getId(),
                terms.getContents(),
                terms.getCreatedAt().toLocalDate());
    }
}

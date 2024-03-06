package com.server.oceankeeper.domain.terms.dto.response;

import com.server.oceankeeper.domain.terms.entity.Terms;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Data
@RequiredArgsConstructor
public class TermsResDto {
    private final Long id;
    private final String contents;
    private final LocalDate createdAt;

    public static TermsResDto fromEntity(Terms terms){
        return new TermsResDto(
                terms.getId(),
                terms.getContents(),
                terms.getCreatedAt().toLocalDate()
                );
    }
}

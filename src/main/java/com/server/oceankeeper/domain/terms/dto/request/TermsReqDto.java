package com.server.oceankeeper.domain.terms.dto.request;

import com.server.oceankeeper.domain.terms.entity.Terms;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TermsReqDto {
    private final String contents;

    public Terms toEntity(){
        return Terms.builder()
                .contents(contents)
                .createdAt(LocalDateTime.now())
                .build();
    }
}

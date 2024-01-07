package com.server.oceankeeper.domain.terms.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.server.oceankeeper.domain.terms.entity.Terms;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class TermsReqDto {
    private String contents;

    public Terms toEntity(){
        return Terms.builder()
                .contents(contents)
                .createdAt(LocalDateTime.now())
                .build();
    }
}

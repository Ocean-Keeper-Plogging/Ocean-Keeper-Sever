package com.server.oceankeeper.domain.terms.dto.response;

import com.server.oceankeeper.domain.terms.entity.Terms;
import lombok.Data;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

@Data
public class TermsDetailResDto {
    private final Long id;
    private final String contents;
    private final LocalDate createdAt;

    public static TermsDetailResDto fromEntity(Terms terms){
        ByteBuffer buffer = StandardCharsets.UTF_8.encode(terms.getContents());

        String utf8EncodedString = StandardCharsets.UTF_8.decode(buffer).toString();
        return new TermsDetailResDto(
                terms.getId(),
                utf8EncodedString,
                terms.getCreatedAt().toLocalDate());
    }
}

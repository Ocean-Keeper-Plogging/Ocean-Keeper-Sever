package com.server.oceankeeper.domain.privacy.dto.response;

import com.server.oceankeeper.domain.privacy.entity.PrivacyPolicy;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

@Data
@Slf4j
public class PrivacyPolicyDetailResDto {
    private final Long id;
    private final String contents;
    private final LocalDate createdAt;

    public static PrivacyPolicyDetailResDto fromEntity(PrivacyPolicy terms) {
        log.info("JBJB term utf8로 처리함");
        ByteBuffer buffer = StandardCharsets.UTF_8.encode(terms.getContents());

        String utf8EncodedString = StandardCharsets.UTF_8.decode(buffer).toString();
        return new PrivacyPolicyDetailResDto(
                terms.getId(),
                utf8EncodedString,
                terms.getCreatedAt().toLocalDate());
    }
}

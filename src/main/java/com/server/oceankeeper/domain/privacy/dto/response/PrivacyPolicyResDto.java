package com.server.oceankeeper.domain.privacy.dto.response;

import com.server.oceankeeper.domain.privacy.entity.PrivacyPolicy;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Data
@RequiredArgsConstructor
public class PrivacyPolicyResDto {
    private final Long id;
    private final String contents;
    private final LocalDate createdAt;

    public static PrivacyPolicyResDto fromEntity(PrivacyPolicy terms){
        return new PrivacyPolicyResDto(
                terms.getId(),
                terms.getContents(),
                terms.getCreatedAt().toLocalDate()
                );
    }
}

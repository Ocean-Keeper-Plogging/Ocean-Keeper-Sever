package com.server.oceankeeper.domain.privacy.dto.response;

import com.server.oceankeeper.domain.privacy.entity.PrivacyPolicy;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PrivacyPolicyDetailResDto {
    private final Long id;
    private final String contents;
    private final LocalDate createdAt;

    public static PrivacyPolicyDetailResDto fromEntity(PrivacyPolicy terms){
        return new PrivacyPolicyDetailResDto(
                terms.getId(),
                terms.getContents(),
                terms.getCreatedAt().toLocalDate());
    }
}

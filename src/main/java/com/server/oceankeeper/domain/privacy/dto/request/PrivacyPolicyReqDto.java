package com.server.oceankeeper.domain.privacy.dto.request;

import com.server.oceankeeper.domain.privacy.entity.PrivacyPolicy;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PrivacyPolicyReqDto {
    private final String contents;

    public PrivacyPolicy toEntity(){
        return PrivacyPolicy.builder()
                .contents(contents)
                .createdAt(LocalDateTime.now())
                .build();
    }
}

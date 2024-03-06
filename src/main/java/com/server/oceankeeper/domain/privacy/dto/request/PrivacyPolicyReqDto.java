package com.server.oceankeeper.domain.privacy.dto.request;

import com.server.oceankeeper.domain.privacy.entity.PrivacyPolicy;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class PrivacyPolicyReqDto {
    private String contents;

    public PrivacyPolicy toEntity(){
        return PrivacyPolicy.builder()
                .contents(contents)
                .createdAt(LocalDateTime.now())
                .build();
    }
}

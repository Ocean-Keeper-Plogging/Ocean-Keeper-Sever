package com.server.oceankeeper.domain.privacy.service;

import com.server.oceankeeper.domain.privacy.dto.request.PrivacyPolicyReqDto;
import com.server.oceankeeper.domain.privacy.dto.response.PrivacyPolicyDetailResDto;
import com.server.oceankeeper.domain.privacy.dto.response.PrivacyPolicyResDto;
import com.server.oceankeeper.domain.privacy.entity.PrivacyPolicy;
import com.server.oceankeeper.domain.privacy.repository.PrivacyPolicyRepository;
import com.server.oceankeeper.global.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class PrivacyPolicyService {
    private final PrivacyPolicyRepository repository;

    @Transactional
    public PrivacyPolicyResDto post(PrivacyPolicyReqDto request) {
        String policyStr = request.getContents().replaceAll("\\\\", "");
        PrivacyPolicy privacyPolicy = new PrivacyPolicy(null, policyStr, LocalDateTime.now());
        repository.save(privacyPolicy);
        return PrivacyPolicyResDto.fromEntity(privacyPolicy);
    }

    @Transactional
    public PrivacyPolicyDetailResDto get() {
        PrivacyPolicy terms = repository.findFirstByOrderByIdDesc()
                .orElseThrow(() -> new ResourceNotFoundException("개인정보 처리방침 아이디에 해당하는 개인정보 처리방침가 없습니다."));
        return PrivacyPolicyDetailResDto.fromEntity(terms);
    }

    @Transactional
    public boolean delete(Long termsId) {
        PrivacyPolicy terms = repository.findById(termsId)
                .orElseThrow(() -> new ResourceNotFoundException("개인정보 처리방침 아이디에 해당하는 개인정보 처리방침가 없습니다."));
        repository.delete(terms);
        return true;
    }
}

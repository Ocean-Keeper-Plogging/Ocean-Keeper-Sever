package com.server.oceankeeper.domain.terms.service;

import com.server.oceankeeper.domain.terms.dto.response.TermsDetailResDto;
import com.server.oceankeeper.domain.terms.dto.response.TermsResDto;
import com.server.oceankeeper.domain.terms.entity.Terms;
import com.server.oceankeeper.domain.terms.repository.TermsRepository;
import com.server.oceankeeper.global.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class TermsService {
    private final TermsRepository repository;

    @Transactional
    public TermsResDto post(String request) {
        Terms terms = new Terms(null, request, LocalDateTime.now());
        repository.save(terms);
        return TermsResDto.fromEntity(terms);
    }

    @Transactional
    public TermsDetailResDto get() {
        Terms terms = repository.findFirstByOrderByIdDesc()
                .orElseThrow(() -> new ResourceNotFoundException("이용약관 아이디에 해당하는 이용약관이 없습니다."));
        return TermsDetailResDto.fromEntity(terms);
    }

    @Transactional
    public boolean delete(Long termsId) {
        Terms terms = repository.findById(termsId)
                .orElseThrow(() -> new ResourceNotFoundException("이용약관 아이디에 해당하는 이용약관이 없습니다."));
        repository.delete(terms);
        return true;
    }
}

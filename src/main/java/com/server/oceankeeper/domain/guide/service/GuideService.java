package com.server.oceankeeper.domain.guide.service;

import com.server.oceankeeper.domain.guide.dto.response.GuideDetailResDto;
import com.server.oceankeeper.domain.guide.dto.response.GuideResDto;
import com.server.oceankeeper.domain.guide.dto.GuideDao;
import com.server.oceankeeper.domain.guide.dto.request.GuideModifyReqDto;
import com.server.oceankeeper.domain.guide.dto.request.GuideReqDto;
import com.server.oceankeeper.domain.guide.dto.response.GuideListResDto;
import com.server.oceankeeper.domain.guide.entity.Guide;
import com.server.oceankeeper.domain.guide.repository.GuideRepository;
import com.server.oceankeeper.global.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class GuideService {
    private final GuideRepository repository;

    @Transactional
    public GuideListResDto get(Long noticeId, Integer size) {
        Slice<GuideDao> noticeDaoSlice = repository.getData(noticeId, Pageable.ofSize(size != null ? size : 10));
        log.debug("get response :{}", noticeDaoSlice);

        return new GuideListResDto(noticeDaoSlice.toList(),
                new GuideListResDto.Meta(noticeDaoSlice.getSize(), !noticeDaoSlice.hasNext()));
    }

    @Transactional
    public GuideResDto post(GuideReqDto request) {
        Guide notice = request.toEntity();
        repository.save(notice);
        return GuideResDto.fromEntity(notice);
    }

    @Transactional
    public GuideResDto put(GuideModifyReqDto request) {
        Guide notice = repository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("이용 가이드 아이디에 해당하는 이용 가이드가 없습니다."));
        Guide modifiedGuide = Guide.builder()
                .id(notice.getId())
                .title(request.getTitle() == null ? notice.getTitle() : request.getTitle())
                .contents(request.getContents() == null ? notice.getContents() : request.getContents())
                .createdAt(notice.getCreatedAt())
                .build();
        modifiedGuide = repository.save(modifiedGuide);
        return GuideResDto.fromEntity(modifiedGuide);
    }

    @Transactional
    public GuideDetailResDto getDetail(Long noticeId) {
        Guide notice = repository.findById(noticeId)
                .orElseThrow(() -> new ResourceNotFoundException("이용 가이드 아이디에 해당하는 이용 가이드가 없습니다."));
        return GuideDetailResDto.fromEntity(notice);
    }

    @Transactional
    public boolean delete(Long noticeId) {
        Guide notice = repository.findById(noticeId)
                .orElseThrow(() -> new ResourceNotFoundException("이용 가이드 아이디에 해당하는 이용 가이드가 없습니다."));
        repository.delete(notice);
        return true;
    }
}

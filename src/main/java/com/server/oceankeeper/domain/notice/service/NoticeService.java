package com.server.oceankeeper.domain.notice.service;

import com.server.oceankeeper.domain.notice.dto.NoticeDao;
import com.server.oceankeeper.domain.notice.dto.request.NoticeModifyReqDto;
import com.server.oceankeeper.domain.notice.dto.request.NoticeReqDto;
import com.server.oceankeeper.domain.notice.dto.response.NoticeDetailResDto;
import com.server.oceankeeper.domain.notice.dto.response.NoticeListResDto;
import com.server.oceankeeper.domain.notice.dto.response.NoticeResDto;
import com.server.oceankeeper.domain.notice.entity.Notice;
import com.server.oceankeeper.domain.notice.repository.NoticeRepository;
import com.server.oceankeeper.domain.statistics.entity.ActivityEvent;
import com.server.oceankeeper.global.eventfilter.EventPublisher;
import com.server.oceankeeper.global.eventfilter.OceanKeeperEventType;
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
public class NoticeService {
    private final NoticeRepository repository;
    private final EventPublisher publisher;

    @Transactional
    public NoticeListResDto get(Long noticeId, Integer size) {
        Slice<NoticeDao> noticeDaoSlice = repository.getData(noticeId, Pageable.ofSize(size != null ? size : 10));
        log.debug("getNotice response :{}", noticeDaoSlice);

        return new NoticeListResDto(noticeDaoSlice.toList(),
                new NoticeListResDto.Meta(noticeDaoSlice.getSize(), !noticeDaoSlice.hasNext()));
    }

    @Transactional
    public NoticeResDto post(NoticeReqDto request) {
        Notice notice = request.toEntity();
        repository.save(notice);

        publisher.emit(new ActivityEvent(this, null, OceanKeeperEventType.NEW_NOTICE_EVENT));
        return NoticeResDto.fromEntity(notice);
    }

    @Transactional
    public NoticeResDto put(NoticeModifyReqDto request) {
        Notice notice = repository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("공지사항 아이디에 해당하는 공지사항이 없습니다."));
        Notice modifiedNotice = Notice.builder()
                .id(notice.getId())
                .title(request.getTitle() == null ? notice.getTitle() : request.getTitle())
                .contents(request.getContents() == null ? notice.getContents() : request.getContents())
                .createdAt(notice.getCreatedAt())
                .build();
        modifiedNotice = repository.save(modifiedNotice);
        return NoticeResDto.fromEntity(modifiedNotice);
    }

    @Transactional
    public NoticeDetailResDto getDetail(Long noticeId) {
        Notice notice = repository.findById(noticeId)
                .orElseThrow(() -> new ResourceNotFoundException("공지사항 아이디에 해당하는 공지사항이 없습니다."));
        return NoticeDetailResDto.fromEntity(notice);
    }

    @Transactional
    public boolean delete(Long noticeId) {
        Notice notice = repository.findById(noticeId)
                .orElseThrow(() -> new ResourceNotFoundException("공지사항 아이디에 해당하는 공지사항이 없습니다."));
        repository.delete(notice);
        return true;
    }
}

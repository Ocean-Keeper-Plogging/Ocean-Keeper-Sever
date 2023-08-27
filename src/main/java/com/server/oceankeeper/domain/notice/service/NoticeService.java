package com.server.oceankeeper.domain.notice.service;

import com.server.oceankeeper.domain.activity.dto.response.GetActivityResDto;
import com.server.oceankeeper.domain.notice.dto.NoticeDao;
import com.server.oceankeeper.domain.notice.dto.request.NoticeModifyReqDto;
import com.server.oceankeeper.domain.notice.dto.request.NoticeReqDto;
import com.server.oceankeeper.domain.notice.dto.response.NoticeDetailResDto;
import com.server.oceankeeper.domain.notice.dto.response.NoticeListResDto;
import com.server.oceankeeper.domain.notice.dto.response.NoticeResDto;
import com.server.oceankeeper.domain.notice.entity.Notice;
import com.server.oceankeeper.domain.notice.repository.NoticeRepository;
import com.server.oceankeeper.global.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NoticeService {
    private final NoticeRepository repository;

    @Transactional
    public NoticeListResDto getNotice(Long noticeId, Integer size) {
        Slice<NoticeDao> noticeDaoSlice= repository.getNotices(noticeId, Pageable.ofSize(size !=null? size:10));
        log.debug("getNotice response :{}", noticeDaoSlice);

        return new NoticeListResDto(noticeDaoSlice.toList(),
                new NoticeListResDto.Meta(noticeDaoSlice.getSize(), !noticeDaoSlice.hasNext()));
    }

    @Transactional
    public NoticeResDto postNotice(NoticeReqDto request) {
        Notice notice = request.toEntity();
        repository.save(notice);
        return NoticeResDto.fromEntity(notice);
    }

    @Transactional
    public NoticeDetailResDto getNoticeDetail(Long noticeId) {
        Notice notice = repository.findById(noticeId)
                .orElseThrow(()->new ResourceNotFoundException("공지사항 아이디에 해당하는 공지사항이 없습니다."));
        return NoticeDetailResDto.fromEntity(notice);
    }

    public NoticeResDto putNotice(NoticeModifyReqDto request) {
        Notice notice = repository.findById(request.getId())
                .orElseThrow(()->new ResourceNotFoundException("공지사항 아이디에 해당하는 공지사항이 없습니다."));
        Notice modifiedNotice = Notice.builder()
                .id(notice.getId())
                .title(request.getTitle())
                .contents(request.getContents())
                .build();
        modifiedNotice = repository.save(modifiedNotice);
        return NoticeResDto.fromEntity(modifiedNotice);
    }
}

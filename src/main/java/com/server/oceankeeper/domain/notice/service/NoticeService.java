package com.server.oceankeeper.domain.notice.service;

import com.server.oceankeeper.domain.message.entity.MessageEvent;
import com.server.oceankeeper.domain.notice.dto.NoticeDao;
import com.server.oceankeeper.domain.notice.dto.request.NoticeModifyReqDto;
import com.server.oceankeeper.domain.notice.dto.request.NoticeReqDto;
import com.server.oceankeeper.domain.notice.dto.response.NoticeDetailResDto;
import com.server.oceankeeper.domain.notice.dto.response.NoticeDto;
import com.server.oceankeeper.domain.notice.dto.response.NoticeListResDto;
import com.server.oceankeeper.domain.notice.dto.response.NoticeResDto;
import com.server.oceankeeper.domain.notice.entity.Notice;
import com.server.oceankeeper.domain.notice.repository.NoticeRepository;
import com.server.oceankeeper.domain.statistics.entity.ActivityEvent;
import com.server.oceankeeper.global.eventfilter.EventPublisher;
import com.server.oceankeeper.global.eventfilter.OceanKeeperEventType;
import com.server.oceankeeper.global.exception.ResourceNotFoundException;
import com.server.oceankeeper.global.markdown.HtmlToMarkDownUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NoticeService {
    private final NoticeRepository repository;
    private final EventPublisher publisher;

    @Transactional
    public NoticeListResDto get(Long noticeId, Integer size, boolean markdown) {
        Slice<NoticeDao> noticeDaoSlice = repository.getData(noticeId, Pageable.ofSize(size != null ? size : 10));
        log.debug("getNotice response :{}", noticeDaoSlice);

        if (markdown) {
            List<NoticeDto> li = new ArrayList<>();
            for (NoticeDao dao : noticeDaoSlice) {
                String contents = HtmlToMarkDownUtil.convertToMarkdown(dao.getContents());
                NoticeDto dto = new NoticeDto(dao, contents);
                li.add(dto);
            }
            return new NoticeListResDto(li, new NoticeListResDto.Meta(noticeDaoSlice.getSize(), !noticeDaoSlice.hasNext()));
        }
        return new NoticeListResDto(noticeDaoSlice.toList().stream().map(NoticeDto::new).collect(Collectors.toList()),
                new NoticeListResDto.Meta(noticeDaoSlice.getSize(), !noticeDaoSlice.hasNext()));
    }

    @Transactional
    public NoticeResDto post(NoticeReqDto request) {
        Notice notice = request.toEntity();
        repository.save(notice);
        log.info("JBJB post notice ={}",notice);
        publisher.emit(new MessageEvent(this, null, OceanKeeperEventType.NEW_NOTICE_EVENT));
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
    public NoticeDetailResDto getDetail(Long noticeId, boolean markdown) {
        Notice notice = repository.findById(noticeId)
                .orElseThrow(() -> new ResourceNotFoundException("공지사항 아이디에 해당하는 공지사항이 없습니다."));
        if (markdown) {
            String contents = HtmlToMarkDownUtil.convertToMarkdown(notice.getContents());
            return NoticeDetailResDto.fromEntity(notice, contents);
        }
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

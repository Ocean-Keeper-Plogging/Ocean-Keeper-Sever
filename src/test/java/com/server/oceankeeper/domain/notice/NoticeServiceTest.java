package com.server.oceankeeper.domain.notice;

import com.server.oceankeeper.domain.notice.dto.NoticeDao;
import com.server.oceankeeper.domain.notice.dto.request.NoticeModifyReqDto;
import com.server.oceankeeper.domain.notice.dto.request.NoticeReqDto;
import com.server.oceankeeper.domain.notice.dto.response.NoticeDetailResDto;
import com.server.oceankeeper.domain.notice.dto.response.NoticeListResDto;
import com.server.oceankeeper.domain.notice.dto.response.NoticeResDto;
import com.server.oceankeeper.domain.notice.entity.Notice;
import com.server.oceankeeper.domain.notice.repository.NoticeRepository;
import com.server.oceankeeper.domain.notice.service.NoticeService;
import com.server.oceankeeper.global.eventfilter.EventPublisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class NoticeServiceTest {
    @InjectMocks
    private NoticeService service;
    @Mock
    private NoticeRepository repository;
    @Mock
    private EventPublisher publisher;

    @Test
    void getNotice() {
        Slice<NoticeDao> expected = new SliceImpl<>(
                List.of(new NoticeDao(2L, "공지사항2", "contents2",LocalDateTime.now(), LocalDateTime.now()),
                        new NoticeDao(1L, "공지사항", "contents",LocalDateTime.now(), LocalDateTime.now())),
                Pageable.ofSize(2),
                false);
        when(repository.getData(any(), any())).thenReturn(expected);

        NoticeListResDto result = service.get(null, 5);
        assertThat(result.getNotices().get(0).getId()).isEqualTo(2L);
        assertThat(result.getNotices().size()).isEqualTo(2L);
        assertThat(result.getMeta().isLast()).isEqualTo(true);
    }

    private Notice makeData() {
        return Notice.builder()
                .id(1L)
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .contents("공지사항입니다.")
                .title("공지사항")
                .build();
    }

    @Test
    void postNotice() {
        NoticeReqDto request = new NoticeReqDto("공지사항입니다.", "공지사항");
        Notice expected = request.toEntity();
        when(repository.save(any())).thenReturn(expected);
        NoticeResDto result = service.post(request);
        assertThat(result.getId()).isEqualTo(expected.getId());
        assertThat(result.getTitle()).isEqualTo(expected.getTitle());
        assertThat(result.getCreatedAt()).isNotNull();
    }

    @Test
    void putNotice() {
        Notice notice = makeData();
        NoticeModifyReqDto request = new NoticeModifyReqDto(1L,"공지사항","공지사항 타이틀");
        when(repository.findById(any())).thenReturn(Optional.ofNullable(notice));
        when(repository.save(any())).thenReturn(new Notice(1L,"공지사항","공지사항 타이틀",notice.getCreatedAt(),notice.getUpdatedAt()));

        NoticeResDto result = service.put(request);
        assertThat(result.getId()).isEqualTo(request.getId());
        assertThat(result.getTitle()).isEqualTo(request.getTitle());
        assertThat(result.getCreatedAt()).isNotNull();
    }

    @Test
    void getNoticeDetail() {
        Notice notice = makeData();
        when(repository.findById(any())).thenReturn(Optional.ofNullable(notice));

        NoticeDetailResDto result = service.getDetail(notice.getId());
        assertThat(result.getId()).isEqualTo(notice.getId());
        assertThat(result.getTitle()).isEqualTo(notice.getTitle());
        assertThat(result.getContents()).isEqualTo(notice.getContents());
        assertThat(result.getCreatedAt()).isEqualTo(notice.getCreatedAt().toLocalDate());
    }

    @Test
    void deleteNotice() {
        //given
        Notice notice = makeData();
        when(repository.findById(any())).thenReturn(Optional.ofNullable(notice));
        doNothing().when(repository).delete(any());

        boolean result = service.delete(notice.getId());
        assertThat(result).isTrue();
    }
}
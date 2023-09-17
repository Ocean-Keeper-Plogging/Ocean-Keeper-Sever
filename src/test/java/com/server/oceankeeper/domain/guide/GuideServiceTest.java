package com.server.oceankeeper.domain.guide;

import com.server.oceankeeper.domain.guide.dto.GuideDao;
import com.server.oceankeeper.domain.guide.dto.request.GuideModifyReqDto;
import com.server.oceankeeper.domain.guide.dto.request.GuideReqDto;
import com.server.oceankeeper.domain.guide.dto.response.GuideDetailResDto;
import com.server.oceankeeper.domain.guide.dto.response.GuideListResDto;
import com.server.oceankeeper.domain.guide.dto.response.GuideResDto;
import com.server.oceankeeper.domain.guide.entity.Guide;
import com.server.oceankeeper.domain.guide.repository.GuideRepository;
import com.server.oceankeeper.domain.guide.service.GuideService;
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
class GuideServiceTest {
    @InjectMocks
    private GuideService service;
    @Mock
    private GuideRepository repository;

    @Test
    void getGuide() {
        Slice<GuideDao> expected = new SliceImpl<>(
                List.of(new GuideDao(2L, "공지사항2", LocalDateTime.now(), LocalDateTime.now()),
                        new GuideDao(1L, "공지사항", LocalDateTime.now(), LocalDateTime.now())),
                Pageable.ofSize(2),
                false);
        when(repository.getData(any(), any())).thenReturn(expected);

        GuideListResDto result = service.get(null, 5);
        assertThat(result.getGuides().get(0).getId()).isEqualTo(2L);
        assertThat(result.getGuides().size()).isEqualTo(2L);
        assertThat(result.getMeta().isLast()).isEqualTo(true);
    }

    private Guide makeData() {
        return Guide.builder()
                .id(1L)
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .contents("공지사항입니다.")
                .title("공지사항")
                .build();
    }

    @Test
    void postGuide() {
        GuideReqDto request = new GuideReqDto("공지사항입니다.", "공지사항");
        Guide expected = request.toEntity();
        when(repository.save(any())).thenReturn(expected);
        GuideResDto result = service.post(request);
        assertThat(result.getId()).isEqualTo(expected.getId());
        assertThat(result.getTitle()).isEqualTo(expected.getTitle());
        assertThat(result.getCreatedAt()).isNotNull();
    }

    @Test
    void putGuide() {
        Guide notice = makeData();
        GuideModifyReqDto request = new GuideModifyReqDto(1L, "공지사항", "공지사항 타이틀");
        when(repository.findById(any())).thenReturn(Optional.ofNullable(notice));
        when(repository.save(any())).thenReturn(new Guide(1L, "공지사항", "공지사항 타이틀", notice.getCreatedAt(), notice.getUpdatedAt()));

        GuideResDto result = service.put(request);
        assertThat(result.getId()).isEqualTo(request.getId());
        assertThat(result.getTitle()).isEqualTo(request.getTitle());
        assertThat(result.getCreatedAt()).isNotNull();
    }

    @Test
    void getGuideDetail() {
        Guide notice = makeData();
        when(repository.findById(any())).thenReturn(Optional.ofNullable(notice));

        GuideDetailResDto result = service.getDetail(notice.getId());
        assertThat(result.getId()).isEqualTo(notice.getId());
        assertThat(result.getTitle()).isEqualTo(notice.getTitle());
        assertThat(result.getContents()).isEqualTo(notice.getContents());
        assertThat(result.getCreatedAt()).isEqualTo(notice.getCreatedAt().toLocalDate());
    }

    @Test
    void deleteGuide() {
        //given
        Guide notice = makeData();
        when(repository.findById(any())).thenReturn(Optional.ofNullable(notice));
        doNothing().when(repository).delete(any());

        boolean result = service.delete(notice.getId());
        assertThat(result).isTrue();
    }
}
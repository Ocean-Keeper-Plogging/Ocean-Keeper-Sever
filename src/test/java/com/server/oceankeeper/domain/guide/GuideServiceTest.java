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
class GuideServiceTest {
    @InjectMocks
    private GuideService service;
    @Mock
    private GuideRepository repository;

    @Test
    void getGuide() {
        Slice<GuideDao> expected = new SliceImpl<>(
                List.of(new GuideDao(2L, "가이드1", "가이드1", "youtube.com/1", LocalDateTime.now(), LocalDateTime.now()),
                        new GuideDao(1L, "공지사항2", "가이드2", "youtube.com/2", LocalDateTime.now(), LocalDateTime.now())),
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
                .videoLink("youtube.com")
                .videoName("비디오 이름1")
                .title("이용가이드임")
                .build();
    }

    @Test
    void postGuide() {
        GuideReqDto request = new GuideReqDto("video link1", "비디오 이름1", "title");
        Guide expected = request.toEntity();
        when(repository.save(any())).thenReturn(expected);
        GuideResDto result = service.post(request);
        assertThat(result.getId()).isEqualTo(expected.getId());
        assertThat(result.getTitle()).isEqualTo(expected.getTitle());
        assertThat(result.getCreatedAt()).isNotNull();
    }

    @Test
    void putGuide() {
        Guide guide = makeData();
        GuideModifyReqDto request = new GuideModifyReqDto(1L, "new 비디오 이름", "https://youtube.com", "이용가이드임");
        when(repository.findById(any())).thenReturn(Optional.ofNullable(guide));
        when(repository.save(any())).thenReturn(new Guide(1L, "https://youtube.com", "new 비디오 이름",
                "이용가이드임", guide.getCreatedAt(), guide.getUpdatedAt()));

        GuideResDto result = service.put(request);
        assertThat(result.getId()).isEqualTo(request.getId());
        assertThat(result.getTitle()).isEqualTo(request.getTitle());
        assertThat(result.getCreatedAt()).isNotNull();
    }

    @Test
    void getGuideDetail() {
        Guide guide = makeData();
        when(repository.findById(any())).thenReturn(Optional.ofNullable(guide));

        GuideDetailResDto result = service.getDetail(guide.getId());
        assertThat(result.getId()).isEqualTo(guide.getId());
        assertThat(result.getTitle()).isEqualTo(guide.getTitle());
        assertThat(result.getContents()).isEqualTo(guide.getVideoLink());
        assertThat(result.getCreatedAt()).isEqualTo(guide.getCreatedAt().toLocalDate());
    }

    @Test
    void deleteGuide() {
        //given
        Guide guide = makeData();
        when(repository.findById(any())).thenReturn(Optional.ofNullable(guide));
        doNothing().when(repository).delete(any());

        boolean result = service.delete(guide.getId());
        assertThat(result).isTrue();
    }
}
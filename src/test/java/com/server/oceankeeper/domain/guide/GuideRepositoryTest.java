package com.server.oceankeeper.domain.guide;

import com.server.oceankeeper.domain.guide.dto.GuideDao;
import com.server.oceankeeper.domain.guide.entity.Guide;
import com.server.oceankeeper.domain.guide.repository.GuideRepository;
import com.server.oceankeeper.global.config.QuerydslConfig;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(QuerydslConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Disabled("auto increment 공유 초기화")
class GuideRepositoryTest {
    @Autowired
    private GuideRepository repository;

    @BeforeEach
    void setUp() {
        makeNewGuide(1L);
        makeNewGuide(2L);
        makeNewGuide(3L);
    }

    @AfterEach
    void teardown() {
        repository.deleteAll();
    }

    private void makeNewGuide(Long id) {
        Guide notice = Guide.builder()
                .id(id)
                .videoName(String.format("Guide %d 입니다.", id))
                .videoLink(String.format("youtube.com/%d", id))
                .title(String.format("Guide %d", id))
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build();
        repository.save(notice);
    }

    @Test
    @DisplayName("Guide 최초 조회")
    @Order(1)
    void getFirstGuides() {
        Slice<GuideDao> response = repository.getData(null, Pageable.ofSize(5));
        assertThat(response.getContent().size()).isEqualTo(3);
        assertThat(response.getContent().get(0).getId()).isEqualTo(3L);
        assertThat(response.hasNext()).isEqualTo(false);
    }

    @Test
    @DisplayName("다음 Guide 있는지 확인")
    @Order(2)
    void getGuidesNext() {
        Slice<GuideDao> response = repository.getData(null, Pageable.ofSize(1));
        assertThat(response.getContent().size()).isEqualTo(1);
        assertThat(response.getContent().get(0).getId()).isEqualTo(6L);
        assertThat(response.hasNext()).isEqualTo(true);
    }

    @Test
    @DisplayName("Guide 조회")
    @Order(3)
    void getGuides() {
        Slice<GuideDao> response = repository.getData(9L, Pageable.ofSize(1));
        assertThat(response.getContent().size()).isEqualTo(1);
        assertThat(response.getContent().get(0).getId()).isEqualTo(8L);
    }
}
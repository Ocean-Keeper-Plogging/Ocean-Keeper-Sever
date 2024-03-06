package com.server.oceankeeper.domain.notice;

import com.server.oceankeeper.domain.notice.dto.NoticeDao;
import com.server.oceankeeper.domain.notice.entity.Notice;
import com.server.oceankeeper.domain.notice.repository.NoticeRepository;
import com.server.oceankeeper.global.config.QuerydslConfig;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(QuerydslConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class NoticeRepositoryTest {
    @Autowired
    private NoticeRepository repository;
    @Autowired
    private EntityManager em;

    @BeforeEach
    void setUp() {
        makeNewNotice(1L);
        makeNewNotice(2L);
        makeNewNotice(3L);
    }

    @AfterEach
    void teardown() {
        repository.deleteAll();
    }

    private void makeNewNotice(Long id) {
        Notice notice = Notice.builder()
                .id(id)
                .contents(String.format("공지사항 %d 입니다.", id))
                .title(String.format("공지사항 %d", id))
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build();
        repository.save(notice);
    }

    @Test
    @DisplayName("공지사항 최초 조회")
    @Order(1)
    void getFirstNotices() {
        var data = repository.findAll();
        Slice<NoticeDao> response = repository.getData(null, Pageable.ofSize(5));
        assertThat(response.getContent().size()).isEqualTo(3);
        assertThat(response.getContent().get(0).getId()).isEqualTo(3L);
        assertThat(response.hasNext()).isEqualTo(false);
    }

    @Test
    @DisplayName("다음 공지사항 있는지 확인")
    @Order(2)
    void getNoticesNext() {
        var data = repository.findAll();
        Slice<NoticeDao> response = repository.getData(null, Pageable.ofSize(1));
        assertThat(response.getContent().size()).isEqualTo(1);
        assertThat(response.getContent().get(0).getId()).isEqualTo(6L);
        assertThat(response.hasNext()).isEqualTo(true);
    }

    @Test
    @DisplayName("공지사항 조회")
    @Order(3)
    void getNotices() {
        var data = repository.findAll();
        Slice<NoticeDao> response = repository.getData(9L, Pageable.ofSize(1));
        assertThat(response.getContent().size()).isEqualTo(1);
        assertThat(response.getContent().get(0).getId()).isEqualTo(8L);
    }
}
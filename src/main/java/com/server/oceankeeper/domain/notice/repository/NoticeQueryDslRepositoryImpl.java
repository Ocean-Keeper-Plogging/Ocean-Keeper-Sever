package com.server.oceankeeper.domain.notice.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.server.oceankeeper.domain.notice.dto.NoticeDao;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.util.List;
import java.util.stream.Collectors;

import static com.server.oceankeeper.domain.notice.entity.QNotice.notice;

@RequiredArgsConstructor
public class NoticeQueryDslRepositoryImpl implements NoticeQueryDslRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<NoticeDao> getNotices(Long noticeId, Pageable pageable) {
        List<NoticeDao> result = queryFactory.select(
                        Projections.fields(NoticeDao.class,
                                notice.id.as("noticeId"),
                                notice.title,
                                notice.createdAt,
                                notice.updatedAt.as("modifiedAt")))
                .from(notice).where(lessThan(noticeId)) //for no offset scrolling, use notice id
                .orderBy(notice.id.desc()).limit(pageable.getPageSize() + 1)
                .fetch().stream().distinct().collect(Collectors.toList());
        return checkLastPage(pageable, result);
    }

    private <T> Slice<T> checkLastPage(Pageable pageable, List<T> result) {
        boolean hasNext = false;

        if (result.size() > pageable.getPageSize()) {
            hasNext = true;
            result.remove(pageable.getPageSize());
        }

        return new SliceImpl<T>(result, pageable, hasNext);
    }

    private BooleanExpression lessThan(Long id) {
        return id == null ? null : notice.id.lt(id);
    }
}

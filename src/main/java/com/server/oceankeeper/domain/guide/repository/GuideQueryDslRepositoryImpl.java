package com.server.oceankeeper.domain.guide.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.server.oceankeeper.domain.guide.dto.GuideDao;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.util.List;
import java.util.stream.Collectors;

import static com.server.oceankeeper.domain.guide.entity.QGuide.guide;

@RequiredArgsConstructor
public class GuideQueryDslRepositoryImpl implements GuideQueryDslRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<GuideDao> getData(Long id, Pageable pageable) {
        List<GuideDao> result = queryFactory.select(
                        Projections.constructor(GuideDao.class,
                                guide.id,
                                guide.title,
                                guide.createdAt,
                                guide.updatedAt.as("modifiedAt")))
                .from(guide).where(lessThan(id)) //for no offset scrolling, use notice id
                .orderBy(guide.id.desc())
                .limit(pageable.getPageSize() + 1)
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
        return id == null ? null : guide.id.lt(id);
    }
}

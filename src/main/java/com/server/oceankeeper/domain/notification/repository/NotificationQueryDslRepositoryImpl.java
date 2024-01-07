package com.server.oceankeeper.domain.notification.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.server.oceankeeper.domain.guide.dto.GuideDao;
import com.server.oceankeeper.domain.notification.entity.Notification;
import com.server.oceankeeper.domain.user.entitiy.OUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.util.List;
import java.util.stream.Collectors;

import static com.server.oceankeeper.domain.notification.entity.QNotification.notification;

@RequiredArgsConstructor
public class NotificationQueryDslRepositoryImpl implements NotificationQueryDslRepository {
    private final JPAQueryFactory queryFactory;
    @Override
    public Slice<Notification> getNotification(OUser user, Long id, Pageable pageable) {
        List<Notification> result = queryFactory.selectFrom(notification)
                .where(lessThan(id)) //for no offset scrolling, use notice id
                .orderBy(notification.id.desc())
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
        return id == null ? null : notification.id.lt(id);
    }
}
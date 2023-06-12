package com.server.oceankeeper.domain.activity.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.server.oceankeeper.domain.activity.dto.response.ActivityDao;
import com.server.oceankeeper.domain.activity.entity.ActivityStatus;
import com.server.oceankeeper.domain.activity.entity.GarbageCategory;
import com.server.oceankeeper.domain.activity.entity.LocationTag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static com.server.oceankeeper.domain.activity.entity.QActivity.activity;
import static com.server.oceankeeper.domain.crew.entitiy.QCrews.crews;
import static com.server.oceankeeper.domain.user.entitiy.QOUser.oUser;

@RequiredArgsConstructor
@Slf4j
public class ActivityQueryDslRepositoryImpl implements ActivityQueryDslRepository {
    private final JPAQueryFactory queryFactory;

    public List<ActivityDao> findActivities(ActivityStatus status, LocationTag tag, GarbageCategory category, Pageable pageable) {
        return queryFactory.select(
                        Projections.fields(ActivityDao.class,
                                activity.uuid.as("activityId"),
                                activity.title.as("title"),
                                activity.locationTag.as("locationTag"),
                                activity.garbageCategory.as("garbageCategory"),
                                oUser.nickname.as("hostNickname"),
                                activity.quota.as("quota"),
                                activity.participants.as("participants"),
                                activity.thumbnail.as("activityImageUrl")))
                .from(crews)
                .innerJoin(crews.activity, activity)
                .innerJoin(crews.user, oUser)
                .where(condition(status, activity.activityStatus::eq).and(activity.activityStatus.ne(ActivityStatus.ALL))
                                .or(condition(ActivityStatus.OPEN, activity.activityStatus::eq)
                                        .or(condition(ActivityStatus.CLOSE, activity.activityStatus::eq))),
                        condition(tag, activity.locationTag::eq),
                        condition(category, activity.garbageCategory::eq))
                .fetch();
    }

    private <T> BooleanExpression condition(T value, Function<T, BooleanExpression> function) {
        return Optional.ofNullable(value).map(function).orElse(null);
    }
}

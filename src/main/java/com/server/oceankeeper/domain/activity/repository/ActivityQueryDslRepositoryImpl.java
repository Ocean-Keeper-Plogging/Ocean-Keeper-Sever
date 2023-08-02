package com.server.oceankeeper.domain.activity.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.server.oceankeeper.domain.activity.dto.ActivityDao;
import com.server.oceankeeper.domain.activity.dto.MyActivityDao;
import com.server.oceankeeper.domain.activity.dto.AllActivityDao;
import com.server.oceankeeper.domain.activity.entity.ActivityStatus;
import com.server.oceankeeper.domain.activity.entity.GarbageCategory;
import com.server.oceankeeper.domain.activity.entity.LocationTag;
import com.server.oceankeeper.domain.crew.entitiy.CrewRole;
import com.server.oceankeeper.domain.crew.param.MyActivityParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.server.oceankeeper.domain.activity.entity.QActivity.activity;
import static com.server.oceankeeper.domain.crew.entitiy.QCrews.crews;
import static com.server.oceankeeper.domain.user.entitiy.QOUser.oUser;

@RequiredArgsConstructor
@Slf4j
public class ActivityQueryDslRepositoryImpl implements ActivityQueryDslRepository {
    private final JPAQueryFactory queryFactory;

    public Slice<AllActivityDao> getAllActivities(UUID activityId, ActivityStatus status, LocationTag tag, GarbageCategory category, Pageable pageable) {
        List<AllActivityDao> result = queryFactory.select(
                        Projections.fields(AllActivityDao.class,
                                activity.uuid.as("activityId"),
                                activity.title.as("title"),
                                activity.locationTag.as("locationTag"),
                                activity.garbageCategory.as("garbageCategory"),
                                oUser.nickname.as("hostNickname"),
                                activity.quota.as("quota"),
                                activity.participants.as("participants"),
                                activity.thumbnail.as("activityImageUrl"),
                                activity.recruitStartAt,
                                activity.recruitEndAt,
                                activity.startAt
                                ))
                .from(crews)
                .innerJoin(crews.activity, activity)
                .innerJoin(crews.user, oUser)
                .where(condition(status, activity.activityStatus::eq),
                        condition(tag, activity.locationTag::eq),
                        condition(category, activity.garbageCategory::eq),
                        condition(CrewRole.HOST, crews.activityRole::eq),
                        ltUuid(activityId)
                ) //for no offset scrolling, use activity id
                .orderBy(activity.uuid.desc())
                .limit(pageable.getPageSize() + 1)
                .fetch()
                .stream().distinct().collect(Collectors.toList());
        return checkLastPage(pageable, result);
    }

    @Override
    public Slice<ActivityDao> getMyActivities(UUID userId, UUID activityId, ActivityStatus activityStatus, CrewRole crewRole, Pageable pageable) {
        List<ActivityDao> result = queryFactory.select(
                        Projections.constructor(ActivityDao.class,
                                activity.uuid.as("activityId"),
                                activity.title.as("title"),
                                oUser.nickname.as("hostNickname"),
                                activity.quota.as("quota"),
                                activity.participants.as("participants"),
                                activity.thumbnail.as("activityImageUrl"),
                                activity.recruitStartAt.as("recruitStartAt"),
                                activity.recruitEndAt.as("recruitEndAt"),
                                activity.startAt.as("startAt"),
                                activity.activityStatus.as("status"),
                                activity.location.address.as("address")
                        ))
                .from(crews)
                .innerJoin(crews.activity, activity)
                .innerJoin(crews.user, oUser)
                .where(condition(userId, oUser.uuid::eq)
                        , condition(activityStatus, activity.activityStatus::eq)
                        , condition(crewRole, crews.activityRole::eq)
                        , ltUuid(activityId) //for no offset scrolling, use activity id
                )
                .orderBy(activity.uuid.desc())
                .limit(pageable.getPageSize() + 1)
                .fetch();
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

    private BooleanExpression ltUuid(UUID uuid) {
        return uuid == null ? null : activity.uuid.lt(uuid);
    }

    private <T> BooleanExpression condition(T value, Function<T, BooleanExpression> function) {
        return Optional.ofNullable(value).map(function).orElse(null);
    }

    @Override
    public List<MyActivityDao> getMyActivitiesLimit5(MyActivityParam myActivityParam) {
        return queryFactory
                .select(Projections.fields(MyActivityDao.class,
                        crews.activity.uuid.as("uuid"),
                        crews.activity.title.as("title"),
                        crews.activity.startAt.as("startAt"),
                        crews.activity.location.address.as("address")))
                .from(crews)
                .join(crews.user, oUser)
                .join(crews.activity, activity)
                .where(
                        oUser.uuid.eq(myActivityParam.getUserUuid()),
                        crews.activity.recruitStartAt.loe(myActivityParam.getTime()),
                        crews.activity.recruitEndAt.goe(myActivityParam.getTime()),
                        condition(myActivityParam.getCrewStatus(), crews.crewStatus::eq))
                .orderBy(crews.activity.startAt.asc())
                .limit(5)
                .fetch();
    }
}

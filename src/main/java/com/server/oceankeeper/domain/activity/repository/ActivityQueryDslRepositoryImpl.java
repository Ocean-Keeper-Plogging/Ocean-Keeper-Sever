package com.server.oceankeeper.domain.activity.repository;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.server.oceankeeper.domain.activity.dao.*;
import com.server.oceankeeper.domain.activity.entity.ActivityStatus;
import com.server.oceankeeper.domain.activity.entity.GarbageCategory;
import com.server.oceankeeper.domain.activity.entity.LocationTag;
import com.server.oceankeeper.domain.crew.entitiy.CrewRole;
import com.server.oceankeeper.domain.crew.entitiy.CrewStatus;
import com.server.oceankeeper.domain.crew.param.MyActivityParam;
import com.server.oceankeeper.domain.user.entitiy.OUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.server.oceankeeper.domain.activity.entity.QActivity.activity;
import static com.server.oceankeeper.domain.crew.entitiy.QCrews.crews;
import static com.server.oceankeeper.domain.statistics.entity.QActivityInfo.activityInfo;
import static com.server.oceankeeper.domain.user.entitiy.QOUser.oUser;

@RequiredArgsConstructor
@Slf4j
public class ActivityQueryDslRepositoryImpl implements ActivityQueryDslRepository {
    private final JPAQueryFactory queryFactory;

    public Slice<AllActivityDao> getAllActivities(UUID activityId, ActivityStatus activityStatus, LocationTag tag, GarbageCategory category, LocalDateTime startAt, Pageable pageable) {
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
                                activity.startAt,
                                activity.location.address.as("location")
                        ))
                .from(crews)
                .innerJoin(crews.activity, activity)
                .innerJoin(crews.user, oUser)
                .where(
                        checkActivityStatus(activityStatus, startAt),
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
    public Slice<ActivityDao> getMyActivities(UUID userId, UUID activityId, ActivityStatus activityStatus, CrewRole crewRole, LocalDateTime startAt, Pageable pageable) {
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
                                activity.location.address.as("address"),
                                crews.uuid.as("applicationId"),
                                crews.activityRole.as("role"),
                                crews.crewStatus.as("crewStatus")
                        ))
                .from(crews)
                .innerJoin(crews.activity, activity)
                .innerJoin(crews.user, oUser)
                .where(condition(userId, oUser.uuid::eq),
                        checkActivityStatus(activityStatus, startAt),
                        condition(crewRole, crews.activityRole::eq),
                        ltUuid(activityId) //for no offset scrolling, use activity id
                )
                .orderBy(activity.uuid.desc())
                .limit(pageable.getPageSize() + 1)
                .fetch();
        return checkLastPage(pageable, result);
    }

    private Predicate checkActivityStatus(ActivityStatus activityStatus, LocalDateTime startAt) {
        return activityStatus == null ? null : activityStatus == ActivityStatus.CLOSED ?
                condition(startAt, activity.startAt::lt) : condition(startAt, activity.startAt::goe);
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
                        condition(myActivityParam.getTime(), activity.startAt::goe),
                        oUser.uuid.eq(myActivityParam.getUserUuid()))
                .orderBy(crews.activity.startAt.asc())
                .limit(5)
                .fetch();
    }

    @Override
    public List<HostActivityDao> getHostActivityNameFromUser(OUser user) {
        return queryFactory
                .select(Projections.constructor(HostActivityDao.class,
                        crews.activity.uuid.as("uuid"),
                        crews.activity.title.as("title")))
                .from(crews)
                .join(crews.user, oUser)
                .join(crews.activity, activity)
                .where(
                        oUser.uuid.eq(user.getUuid()),
                        crews.activityRole.eq(CrewRole.HOST),
                        crews.crewStatus.eq(CrewStatus.IN_PROGRESS))
                .orderBy(crews.activity.title.asc())
                .fetch();
    }

    @Override
    public List<CrewInfoDao> getCrewInfoFromHostUser(OUser user, UUID activityId) {
        return queryFactory
                .select(Projections.constructor(CrewInfoDao.class,
                        crews.activity.uuid.as("uuid"),
                        crews.activity.title.as("title"),
                        crews.user.nickname.as("nickname")
                ))
                .from(crews)
                .join(crews.user, oUser)
                .join(crews.activity, activity)
                .where(
                        crews.user.eq(user),
                        crews.activityRole.eq(CrewRole.HOST),
                        crews.activity.uuid.eq(activityId),
                        crews.crewStatus.eq(CrewStatus.IN_PROGRESS))
                .orderBy(crews.activity.title.asc())
                .fetch();
    }

    @Override
    public List<CrewInfoDetailDao> getCrewInfo(UUID activityId) {
        List<CrewInfoDetailDao> result = queryFactory.select(
                        Projections.constructor(CrewInfoDetailDao.class,
                                crews.name.as("username"),
                                crews.user.nickname,
                                crews.crewStatus,
                                crews.uuid.as("applicationId"))
                )
                .from(crews)
                .leftJoin(crews.activity, activity)
                .leftJoin(crews.user, oUser)
                .where(
                        crews.activity.uuid.eq(activityId),
                        crews.activityRole.ne(CrewRole.HOST)
                )
                .orderBy(crews.id.asc())
                .fetch();
        return result;
    }

    @Override
    public FullApplicationDao getApplicationAndUserInfo(UUID applicationId) {
        FullApplicationDao result = queryFactory.select(Projections.constructor(FullApplicationDao.class,
                        oUser.as("host"),
                        crews.user.profile.as("profileUrl"),
                        crews.user.nickname,
                        activityInfo.countHosting,
                        activityInfo.countActivity,
                        activityInfo.countNoShow,
                        crews.name.as("username"),
                        crews.phoneNumber,
                        crews.id1365,
                        crews.startPoint,
                        crews.transportation,
                        crews.question))
                .from(crews)
                .innerJoin(crews.user, oUser)
                .innerJoin(activityInfo.user, oUser)
                .where(crews.uuid.eq(applicationId))
                .fetchFirst();
        return result;
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
}

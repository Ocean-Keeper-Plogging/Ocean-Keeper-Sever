package com.server.oceankeeper.domain.activity.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.server.oceankeeper.domain.activity.dao.*;
import com.server.oceankeeper.domain.activity.entity.ActivityStatus;
import com.server.oceankeeper.domain.activity.entity.GarbageCategory;
import com.server.oceankeeper.domain.activity.entity.LocationTag;
import com.server.oceankeeper.domain.crew.entity.CrewRole;
import com.server.oceankeeper.domain.crew.entity.CrewStatus;
import com.server.oceankeeper.domain.crew.param.MyActivityParam;
import com.server.oceankeeper.domain.user.entity.OUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
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
import static com.server.oceankeeper.domain.crew.entity.QCrews.crews;
import static com.server.oceankeeper.domain.user.entity.QOUser.oUser;
import static com.server.oceankeeper.domain.blockUser.entity.QBlockUser.blockUser;

@RequiredArgsConstructor
@Slf4j
public class ActivityQueryDslRepositoryImpl implements ActivityQueryDslRepository {
    private final JPAQueryFactory queryFactory;

    public Slice<AllActivityDao> getAllActivities(UUID activityId, ActivityStatus activityStatus,
                                                  LocationTag tag, GarbageCategory category,
                                                  Pageable pageable, OUser requestUser) {

        List<AllActivityDao> result = queryFactory.select(
                        Projections.fields(AllActivityDao.class,
                                activity.uuid.as("activityId"),
                                activity.title.as("title"),
                                activity.locationTag.as("locationTag"),
                                activity.garbageCategory.as("garbageCategory"),
                                activity.host.nickname.as("hostNickname"),
                                activity.quota.as("quota"),
                                activity.participants.as("participants"),
                                activity.rewards.coalesce("").as("rewards"),
                                activity.thumbnail.as("activityImageUrl"),
                                activity.recruitStartAt,
                                activity.recruitEndAt,
                                activity.startAt,
                                activity.location.address.as("location")
                        ))
                .from(activity)
                .innerJoin(activity.host, oUser)
                .leftJoin(oUser.blockedUser, blockUser)
                .where(
                        condition(activityStatus, activity.activityStatus::eq),
                        activity.activityStatus.ne(ActivityStatus.CANCEL),
                        condition(tag, activity.locationTag::eq),
                        condition(category, activity.garbageCategory::eq),
                        getNotBlockedUser(requestUser),
                        ltUuid(activityId)
                ) //for no offset scrolling, use activity id
                .orderBy(activity.id.desc())
                .limit(pageable.getPageSize() + 1)
                .fetch()
                .stream().distinct().collect(Collectors.toList());
        return checkLastPage(pageable, result);
    }

    @NotNull
    private BooleanBuilder getNotBlockedUser(OUser userParam) {
        BooleanBuilder whereClause = new BooleanBuilder();
        List<Long> blockedUserIds = queryFactory
                .select(blockUser.blockedUser.id)
                .from(blockUser)
                .where(blockUser.blocker.eq(userParam))
                .fetch();
        whereClause.and(activity.host.id.notIn(blockedUserIds));
        return whereClause;
    }

    @Override
    public Slice<ActivityDao> getMyActivitiesWithoutCancel(UUID userId, UUID activityId, ActivityStatus activityStatus, CrewRole crewRole, LocalDateTime startAt, Pageable pageable) {
        List<ActivityDao> result = queryFactory.select(
                        Projections.constructor(ActivityDao.class,
                                activity.uuid.as("activityId"),
                                activity.title.as("title"),
                                crews.host.nickname.as("host"),
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
                        crews.activity.activityStatus.ne(ActivityStatus.CANCEL),
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
                        crews.activity.activityStatus.ne(ActivityStatus.CANCEL),
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
                        crews.activity.activityStatus.ne(ActivityStatus.CANCEL),
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
                        crews.host.eq(user),
                        crews.activityRole.eq(CrewRole.CREW),
                        crews.activity.uuid.eq(activityId),
                        crews.activity.activityStatus.ne(ActivityStatus.CANCEL)
                )
                .orderBy(crews.activity.title.asc())
                .fetch();
    }

    @Override
    public List<CrewInfoDetailDao> getCrewInfo(UUID activityId) {
        List<CrewInfoDetailDao> result = queryFactory.select(
                        Projections.constructor(CrewInfoDetailDao.class,
                                crews.activity.activityStatus,
                                crews.name.as("username"),
                                crews.user.nickname,
                                crews.crewStatus,
                                crews.uuid.as("applicationId"))
                )
                .from(crews)
                .leftJoin(crews.activity, activity)
                .leftJoin(crews.user, oUser)
                .where(
                        crews.activity.activityStatus.ne(ActivityStatus.CANCEL),
                        crews.activity.uuid.eq(activityId),
                        crews.activityRole.eq(CrewRole.CREW)
                )
                .orderBy(crews.id.asc())
                .fetch();
        return result;
    }

    @Override
    public List<CrewDeviceTokensDao> getUserFromActivityId(UUID activityId, CrewRole crewRole) {
        List<CrewDeviceTokensDao> result = queryFactory.select(
                        Projections.constructor(CrewDeviceTokensDao.class,
                                crews.user.as("user"))
                )
                .from(crews)
                .leftJoin(crews.activity, activity)
                .leftJoin(crews.user, oUser)
                .where(
                        crews.activity.uuid.eq(activityId),
                        condition(crewRole, crews.activityRole::eq)
                )
                .orderBy(crews.id.asc())
                .fetch();
        return result;
    }

    public long selectByCrewStatusAndStartAtAndUpdateCrewStatusAsDeleted(CrewStatus status, long days) {
        List<Long> applicationIdList = queryFactory
                .select(crews.id)
                .from(crews)
                .leftJoin(crews.activity, activity)
                .where(
                        condition(status, crews.crewStatus::eq),
                        crews.activity.startAt.loe(LocalDateTime.now().minusDays(days))
                ).fetch();
        long count = queryFactory.update(crews)
                .set(crews.name, Expressions.nullExpression())
                .set(crews.phoneNumber, Expressions.nullExpression())
                .set(crews.id1365, Expressions.nullExpression())
                .set(crews.email, Expressions.nullExpression())
                .set(crews.startPoint, Expressions.nullExpression())
                .set(crews.transportation, Expressions.nullExpression())
                .set(crews.question, Expressions.nullExpression())
                .set(crews.dayOfBirth, Expressions.nullExpression())
                .set(crews.expiredAt, Expressions.constant(LocalDateTime.now()))
                .where(crews.id.in(applicationIdList))
                .execute();
        return count;
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

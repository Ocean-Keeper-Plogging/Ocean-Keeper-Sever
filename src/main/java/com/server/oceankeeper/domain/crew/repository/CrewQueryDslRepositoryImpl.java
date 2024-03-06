package com.server.oceankeeper.domain.crew.repository;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
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
import static com.server.oceankeeper.domain.activity.entity.QActivityDetail.activityDetail;
import static com.server.oceankeeper.domain.crew.entitiy.QCrews.crews;
import static com.server.oceankeeper.domain.statistics.entity.QActivityInfo.activityInfo;
import static com.server.oceankeeper.domain.user.entitiy.QOUser.oUser;

@RequiredArgsConstructor
@Slf4j
public class CrewQueryDslRepositoryImpl implements CrewQueryDslRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<FullApplicationDao> getApplicationAndActivityInfoAndCrewInfo(UUID applicationId) {
        return queryFactory.select(
                        Projections.constructor(FullApplicationDao.class,
                                crews.host.id.as("hostId"),

                                oUser.profile.as("profileUrl"),
                                oUser.nickname,

                                activityInfo.countHosting,
                                activityInfo.countActivity,
                                activityInfo.countNoShow,

                                crews.name.as("crewName"),
                                crews.phoneNumber,
                                crews.id1365,
                                crews.startPoint,
                                crews.transportation,
                                crews.question,
                                crews.email,

                                activityDetail.transportation.as("supportedTransportation")
                        ))
                .from(crews)
                .join(oUser).on(crews.user.id.eq(oUser.id))
                .join(activityDetail).on(crews.activity.id.eq(activityDetail.activity.id))
                .join(activityInfo).on(crews.user.id.eq(activityInfo.user.id))
                .where(
                        crews.activity.activityStatus.ne(ActivityStatus.CANCEL),
                        condition(applicationId, crews.uuid::eq)
                ) //for no offset scrolling, use activity id
                .fetch()
                .stream().distinct().collect(Collectors.toList());
    }

    private <T> BooleanExpression condition(T value, Function<T, BooleanExpression> function) {
        return Optional.ofNullable(value).map(function).orElse(null);
    }
}

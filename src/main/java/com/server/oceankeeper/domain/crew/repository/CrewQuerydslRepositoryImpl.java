package com.server.oceankeeper.domain.crew.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.server.oceankeeper.domain.activity.dto.response.MyActivityDao;
import com.server.oceankeeper.domain.crew.param.MyActivityParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static com.server.oceankeeper.domain.activity.entity.QActivity.activity;
import static com.server.oceankeeper.domain.crew.entitiy.QCrews.crews;
import static com.server.oceankeeper.domain.user.entitiy.QOUser.oUser;

@RequiredArgsConstructor
@Slf4j
public class CrewQuerydslRepositoryImpl implements CrewQuerydslRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<MyActivityDao> getMyActivities(MyActivityParam myActivityParam) {
        return queryFactory
                .select(Projections.fields(MyActivityDao.class,
                        crews.uuid.as("uuid"),
                        crews.activity.title.as("title"),
                        crews.activity.startAt.as("startAt"),
                        crews.activity.location.name.as("location")))
                .from(crews)
                .innerJoin(crews.user, oUser)
                .innerJoin(crews.activity, activity)
                .where(
                        oUser.uuid.eq(myActivityParam.getUuid()),
                        crews.activity.startAt.goe(myActivityParam.getNow()),
                        crews.crewStatus.eq(myActivityParam.getCrewStatus()))
                .orderBy(crews.activity.startAt.asc())
                .fetch();
    }

    private <T> BooleanExpression condition(T value, Function<T, BooleanExpression> function) {
        return Optional.ofNullable(value).map(function).orElse(null);
    }
}
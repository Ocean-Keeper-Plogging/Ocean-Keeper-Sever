package com.server.oceankeeper.domain.message.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.server.oceankeeper.domain.message.dto.MessageDao;
import com.server.oceankeeper.domain.message.entity.MessageType;
import com.server.oceankeeper.domain.user.entitiy.OUser;
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
import static com.server.oceankeeper.domain.message.entity.QOMessage.oMessage;
import static com.server.oceankeeper.domain.user.entitiy.QOUser.oUser;

@RequiredArgsConstructor
@Slf4j
public class MessageQueryDslRepositoryImpl implements MessageQueryDslRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<MessageDao> findByUserAndMessageType(Long lastId, OUser user, MessageType type, UUID activityId, Pageable pageable) {
        List<MessageDao> result = queryFactory.select(
                        Projections.constructor(MessageDao.class,
                                oMessage.id.as("id"),
                                oMessage.messageType.as("type"),
                                oMessage.messageFrom.as("from"),
                                activity.uuid.as("activityId"),
                                activity.title.as("activityTitle"),
                                oMessage.contents.as("messageBody"),
                                activity.garbageCategory.as("garbageCategory"),
                                oMessage.createdAt.as("messageSentAt"),
                                activity.startAt.as("activityStartAt"),
                                oMessage.isRead.as("read")
                        ))
                .from(oMessage)
                .innerJoin(oMessage.activity, activity)
                .innerJoin(oMessage.sender, oUser)
                .where(type == MessageType.ALL ? (oMessage.messageType.eq(MessageType.NOTICE))
                                .or(oMessage.messageType.eq(MessageType.PRIVATE)) : nullCondition(type, oMessage.messageType::eq),
                        oMessage.messageTo.eq(user.getNickname()),
                        oMessage.isDeleteFromReceiver.eq(false),
                        nullCondition(activityId, activity.uuid::eq),
                        lessThanId(lastId)
                ) //for no offset scrolling, use message sent time
                .orderBy(oMessage.createdAt.desc())
                .limit(pageable.getPageSize() + 1)
                .fetch()
                .stream().distinct().collect(Collectors.toList());
        return checkLastPage(pageable, result);
    }

    @Override
    public Slice<MessageDao> findBySenderAndMessageType(Long lastId, OUser user, Pageable pageable) {
        List<MessageDao> result = queryFactory.select(
                        Projections.constructor(MessageDao.class,
                                oMessage.id.as("id"),
                                oMessage.messageType.as("type"),
                                oMessage.messageFrom.as("from"),
                                activity.uuid.as("activityId"),
                                activity.title.as("activityTitle"),
                                oMessage.contents.as("messageBody"),
                                activity.garbageCategory.as("garbageCategory"),
                                oMessage.createdAt.as("messageSentAt"),
                                activity.startAt.as("activityStartAt"),
                                oMessage.isRead.as("read")
                        ))
                .from(oMessage)
                .innerJoin(oMessage.activity, activity)
                .innerJoin(oMessage.sender, oUser)
                .where(nullCondition(user.getNickname(), oMessage.messageFrom::eq),
                        oMessage.isDeleteFromSender.eq(false),
                        oMessage.messageType.eq(MessageType.PRIVATE),
                        nullCondition(user, oMessage.sender::eq),
                        lessThanId(lastId)
                ) //for no offset scrolling, use message sent time
                .orderBy(oMessage.createdAt.desc())
                .limit(pageable.getPageSize() + 1)
                .fetch()
                .stream().distinct().collect(Collectors.toList());
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

    private <T> BooleanExpression nullCondition(T value, Function<T, BooleanExpression> function) {
        return Optional.ofNullable(value).map(function).orElse(null);
    }

    private BooleanExpression lessThanId(Long id) {
        return id == null ? null : oMessage.id.lt(id);
    }
}

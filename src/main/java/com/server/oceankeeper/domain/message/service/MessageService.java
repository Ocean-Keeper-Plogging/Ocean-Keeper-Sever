package com.server.oceankeeper.domain.message.service;

import com.server.oceankeeper.domain.activity.entity.Activity;
import com.server.oceankeeper.domain.activity.service.ActivityService;
import com.server.oceankeeper.domain.message.dto.MessageDao;
import com.server.oceankeeper.domain.message.dto.request.MessageReadReqDto;
import com.server.oceankeeper.domain.message.dto.request.MessageSendReqDto;
import com.server.oceankeeper.domain.message.dto.response.MessageSendResDto;
import com.server.oceankeeper.domain.message.dto.response.PostResDto;
import com.server.oceankeeper.domain.message.entity.MessageDetail;
import com.server.oceankeeper.domain.message.entity.MessageEvent;
import com.server.oceankeeper.domain.message.entity.MessageType;
import com.server.oceankeeper.domain.message.entity.OMessage;
import com.server.oceankeeper.domain.message.repository.MessageDetailRepository;
import com.server.oceankeeper.domain.message.repository.MessageRepository;
import com.server.oceankeeper.domain.statistics.entity.ActivityEvent;
import com.server.oceankeeper.domain.user.entitiy.OUser;
import com.server.oceankeeper.global.eventfilter.EventPublisher;
import com.server.oceankeeper.global.eventfilter.OceanKeeperEventType;
import com.server.oceankeeper.global.exception.IllegalRequestException;
import com.server.oceankeeper.global.exception.ResourceNotFoundException;
import com.server.oceankeeper.util.TokenUtil;
import com.server.oceankeeper.util.UUIDGenerator;
import com.server.oceankeeper.util.UserAccessValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class MessageService {
    private final MessageRepository messageRepository;
    private final MessageDetailRepository messageDetailRepository;
    private final ActivityService activityService;
    private final UserAccessValidationUtil userAccessValidationUtil;
    private final TokenUtil tokenUtil;

    @Transactional
    public PostResDto getInbox(String userId, Long id, String type, Integer size, HttpServletRequest request) {
        userAccessValidationUtil.validate(request);

        if (type != null && type.equalsIgnoreCase("SENT")) {
            return getSentMessageInbox(userId, id, size, request);
        }

        MessageType messageType = MessageType.toClass(type);
        Slice<MessageDao> result = getMessageDao(id, size, userId, null, messageType);

        return new PostResDto(result.stream()
                .map(m -> new PostResDto.MessageDto(
                        m.getId(),
                        m.getType(),
                        m.getFrom(),
                        UUIDGenerator.changeUuidToString(m.getActivityId()),
                        m.getActivityTitle(),
                        m.getMessageBody(),
                        m.getGarbageCategory(),
                        m.getMessageSentAt(),
                        m.getActivityStartAt(),
                        m.isRead()))
                .collect(Collectors.toList()),
                new PostResDto.Meta(result.getSize(), result.isLast()));
    }

    @Transactional
    public Slice<MessageDao> getMessageDao(Long id, Integer size, String userId, UUID activityId, MessageType messageType) {
        OUser user = activityService.getUser(userId);
        Slice<MessageDao> result = messageRepository.findByUserAndMessageType(
                id,
                user,
                messageType,
                activityId,
                size != null ? PageRequest.ofSize(size) : PageRequest.ofSize(5));
        return result;
    }

    private PostResDto getSentMessageInbox(String userId, Long id, Integer size, HttpServletRequest request) {
        userAccessValidationUtil.validate(request);

        OUser user = activityService.getUser(userId);

        Slice<MessageDao> result = messageRepository.findBySenderAndMessageType(
                id,
                user,
                size != null ? PageRequest.ofSize(size) : PageRequest.ofSize(5));

        return new PostResDto(result.stream()
                .map(m -> new PostResDto.MessageDto(
                        m.getId(),
                        m.getType(),
                        m.getFrom(),
                        UUIDGenerator.changeUuidToString(m.getActivityId()),
                        m.getActivityTitle(),
                        m.getMessageBody(),
                        m.getGarbageCategory(),
                        m.getMessageSentAt(),
                        m.getActivityStartAt(),
                        m.isRead()))
                .collect(Collectors.toList()),
                new PostResDto.Meta(result.getSize(), result.isLast()));
    }

    @Transactional
    public OMessage findByActivityAndMessageType(Activity activity, MessageType messageType) {
        return messageRepository.findByActivityAndMessageType(activity, messageType)
                .orElseThrow(() -> new ResourceNotFoundException("해당 메세지가 없습니다."));
    }

    @Transactional
    public MessageSendResDto sendMessage(MessageSendReqDto req, HttpServletRequest request) {
        OUser user = tokenUtil.getUserFromHeader(request);
        Activity activity = null;

        activity = activityService.getActivity(req.getActivityId());

        List<Long> messageIdList = new ArrayList<>();
        for (String nickname : req.getTargetNicknames()) {
            OMessage message = OMessage.builder()
                    .read(false)
                    .type(MessageType.valueOf(req.getType().name()))
                    .activity(activity)
                    .messageFrom(user.getNickname())
                    .to(nickname)
                    .sender(user)
                    .contents(req.getContents())
                    .isDeleteFromReceiver(false)
                    .isDeleteFromSender(false)
                    .build();
            messageRepository.save(message);

            MessageDetail messageDetail = MessageDetail.builder()
                    .id(message.getId())
                    .detail(req.getContents())
                    .build();
            messageDetailRepository.save(messageDetail);
            messageIdList.add(message.getId());

            EventPublisher.emit(new MessageEvent(this, nickname, OceanKeeperEventType.MESSAGE_SENT_EVENT));
        }

        return new MessageSendResDto(messageIdList);
    }

    @Transactional
    public boolean changeReadFlag(MessageReadReqDto data, HttpServletRequest request) {
        OMessage message = messageRepository.findById(data.getMessageId())
                .orElseThrow(() -> new ResourceNotFoundException("메세지 아이디에 해당하는 메세지가 없습니다"));

        OUser user = tokenUtil.getUserFromHeader(request);

        boolean bUserIsSender = userIsSender(user, message);
        boolean bUserIsReceiver = userIsReceiver(user, message);
        if (!bUserIsSender && !bUserIsReceiver) {
            throw new IllegalRequestException("요청자가 받거나 보낸 메세지만 확인할 수 있습니다.");
        }

        message.messageRead(data.isRead());
        messageRepository.save(message);
        return true;
    }

    @Transactional
    public boolean delete(Long messageId, HttpServletRequest request) {
        OUser user = tokenUtil.getUserFromHeader(request);

        OMessage message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("메세지 아이디에 해당하는 메세지가 없습니다"));

        boolean bUserIsSender = userIsSender(user, message);
        boolean bUserIsReceiver = userIsReceiver(user, message);

        if (bUserIsSender) {
            log.debug("SENDER{} delete message", user.getNickname());
            message.checkDeletionFromSender(true);
            messageRepository.save(message);
        }

        if (bUserIsReceiver) {
            log.debug("RECEIVER{} delete message", user.getNickname());
            message.checkDeletionFromReceiver(true);
            messageRepository.save(message);
        }

        if (message.getIsDeleteFromReceiver() && message.getIsDeleteFromSender()) {
            log.debug("message id({}) will be deleted", message.getId());
            messageRepository.delete(message);
        }

        return true;
    }

    private boolean userIsSender(OUser user, OMessage message) {
        return message.getMessageFrom().equals(user.getNickname());
    }

    private boolean userIsReceiver(OUser user, OMessage message) {
        return message.getMessageTo().equals(user.getNickname());
    }

    //(HACK) Message entity doesn't control the user entity, so force change the sender and recipient of the message
    @EventListener
    public void handleEvent(ActivityEvent event) {
        if (event.getEvent().equals(OceanKeeperEventType.NICKNAME_CHANGE_EVENT)) {
            log.debug("닉네임 변경 이벤트 처리");
            String newNickname = event.getUser().getNickname();
            List<OMessage> messages = messageRepository.findByMessageFrom(newNickname);
            for (OMessage message : messages) {
                message.changeMessageFrom(newNickname);
            }

            messages = messageRepository.findByMessageTo(newNickname);
            for (OMessage message : messages) {
                message.changeMessageTo(newNickname);
            }
        }
    }
}

package com.server.oceankeeper.domain.message.service;

import com.server.oceankeeper.domain.activity.entity.Activity;
import com.server.oceankeeper.domain.activity.service.ActivityService;
import com.server.oceankeeper.domain.message.dto.MessageDao;
import com.server.oceankeeper.domain.message.dto.request.MessageSendReqDto;
import com.server.oceankeeper.domain.message.dto.response.MessageDetailResDto;
import com.server.oceankeeper.domain.message.dto.response.MessageSendResDto;
import com.server.oceankeeper.domain.message.dto.response.PostResDto;
import com.server.oceankeeper.domain.message.entity.MessageDetail;
import com.server.oceankeeper.domain.message.entity.MessageEvent;
import com.server.oceankeeper.domain.message.entity.MessageType;
import com.server.oceankeeper.domain.message.entity.OMessage;
import com.server.oceankeeper.domain.message.repository.MessageDetailRepository;
import com.server.oceankeeper.domain.message.repository.MessageRepository;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
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
        OUser user = activityService.getUser(userId);

        if (type != null && type.equals("SENT")) {
            return getSentMessageInbox(userId, id, size, request);
        }

        MessageType messageType = MessageType.toClass(type);
        Slice<MessageDao> result = messageRepository.findByUserAndMessageType(
                id,
                user,
                messageType,
                size != null ? PageRequest.ofSize(size) : PageRequest.ofSize(5));

        return new PostResDto(result.stream()
                .map(m -> new PostResDto.MessageDto(
                        m.getId(),
                        m.getType(),
                        m.getFrom(),
                        UUIDGenerator.changeUuidToString(m.getActivityId()),
                        m.getTitle(),
                        m.getGarbageCategory(),
                        m.getTime(),
                        m.isRead()))
                .collect(Collectors.toList()),
                new PostResDto.Meta(result.getSize(), result.isLast()));
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
                        m.getTitle(),
                        m.getGarbageCategory(),
                        m.getTime(),
                        m.isRead()))
                .collect(Collectors.toList()),
                new PostResDto.Meta(result.getSize(), result.isLast()));
    }

    @Transactional
    public MessageSendResDto sendMessage(MessageSendReqDto req, HttpServletRequest request) {
        OUser user = tokenUtil.getUserFromHeader(request);
        Activity activity = null;

        String title = parseTitle(req);
        activity = activityService.getActivity(req.getActivityId());

        List<Long> messageIdList = new ArrayList<>();
        for (String nickname : req.getTargetNicknames()) {
            OMessage message = OMessage.builder()
                    .read(false)
                    .type(MessageType.valueOf(req.getType().name()))
                    .activity(activity)
                    .messageFrom(user.getNickname())
                    .to(nickname)
                    .user(user)
                    .title(title)
                    .isDeleteFromReceiver(false)
                    .isDeleteFromSender(false)
                    .build();
            messageRepository.save(message);
            EventPublisher.emit(new MessageEvent(this, nickname, OceanKeeperEventType.MESSAGE_SENT_EVENT));

            MessageDetail messageDetail = MessageDetail.builder()
                    .id(message.getId())
                    .detail(req.getContents())
                    .build();
            messageDetailRepository.save(messageDetail);
            messageIdList.add(message.getId());
        }

        return new MessageSendResDto(messageIdList);
    }

    private String parseTitle(MessageSendReqDto req) {
        if (req.getContents().matches("\n")) {
            return req.getContents().split("\n")[0];
        }
        if (req.getContents().contains(".")) {
            return req.getContents().split("\\.")[0];
        }

        //fallback
        return req.getContents().substring(0, req.getContents().length() / 2) + "...";
    }

    @Transactional
    public MessageDetailResDto getMessage(Long messageId, HttpServletRequest request) {
        OMessage message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("메세지 아이디에 해당하는 메세지가 없습니다"));
        MessageDetail messageDetail = messageDetailRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("메세지 아이디에 해당하는 상세 메세지가 없습니다"));

        OUser user = tokenUtil.getUserFromHeader(request);

        boolean bUserIsSender = userIsSender(user, message);
        boolean bUserIsReceiver = userIsReceiver(user, message);
        if (!bUserIsSender && !bUserIsReceiver) {
            throw new IllegalRequestException("요청자가 받거나 보낸 메세지만 확인할 수 있습니다.");
        }

        MessageDetailResDto response = MessageDetailResDto.fromEntity(message, messageDetail);

        message.messageRead(true);
        messageRepository.save(message);
        return response;
    }

    @Transactional
    public boolean delete(Long messageId, HttpServletRequest request) {
        OUser user = tokenUtil.getUserFromHeader(request);

        OMessage message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("메세지 아이디에 해당하는 메세지가 없습니다"));
        MessageDetail messageDetail = messageDetailRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("메세지 아이디에 해당하는 상세 메세지가 없습니다"));

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
            messageDetailRepository.delete(messageDetail);
        }

        return true;
    }

    private boolean userIsSender(OUser user, OMessage message) {
        return message.getMessageFrom().equals(user.getNickname());
    }

    private boolean userIsReceiver(OUser user, OMessage message) {
        return message.getMessageTo().equals(user.getNickname());
    }
}

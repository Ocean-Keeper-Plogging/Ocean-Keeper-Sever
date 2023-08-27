package com.server.oceankeeper.domain.message.service;

import com.server.oceankeeper.domain.activity.entity.Activity;
import com.server.oceankeeper.domain.activity.service.ActivityService;
import com.server.oceankeeper.domain.message.dto.MessageDao;
import com.server.oceankeeper.domain.message.dto.request.MessageSendReqDto;
import com.server.oceankeeper.domain.message.dto.request.PrivateMessageSendReqDto;
import com.server.oceankeeper.domain.message.dto.response.MessageDetailResDto;
import com.server.oceankeeper.domain.message.dto.response.MessageSendResDto;
import com.server.oceankeeper.domain.message.dto.response.PostResDto;
import com.server.oceankeeper.domain.message.dto.response.PrivateMessageSendResDto;
import com.server.oceankeeper.domain.message.entity.MessageType;
import com.server.oceankeeper.domain.message.entity.OMessage;
import com.server.oceankeeper.domain.message.repository.MessageRepository;
import com.server.oceankeeper.domain.user.entitiy.OUser;
import com.server.oceankeeper.global.exception.ResourceNotFoundException;
import com.server.oceankeeper.util.TokenUtil;
import com.server.oceankeeper.util.UUIDGenerator;
import com.server.oceankeeper.util.UserAccessValidationUtil;
import lombok.RequiredArgsConstructor;
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
public class MessageService {
    private final MessageRepository messageRepository;
    private final ActivityService activityService;
    private final UserAccessValidationUtil userAccessValidationUtil;
    private final TokenUtil tokenUtil;

    @Transactional
    public PostResDto getMailing(String userId, Long id, MessageType type, Integer size, HttpServletRequest request) throws Exception {
        if (!userAccessValidationUtil.validate(request))
            throw new RuntimeException("호출 에러");

        OUser user = activityService.getUser(userId);
        Slice<MessageDao> result = messageRepository.findByUserAndMessageType(
                id,
                user,
                type != null ? type : MessageType.ALL,
                size != null ? PageRequest.ofSize(size) : PageRequest.ofSize(5));

        PostResDto response = new PostResDto(result.stream()
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
        return response;
    }

    @Transactional
    public MessageSendResDto sendMessage(MessageSendReqDto req, HttpServletRequest request) {
        OUser user = tokenUtil.getUserFromHeader(request);

        String title = parseTitle(req);
        Activity activity = activityService.getActivity(req.getActivityId());

        List<Long> messageIdList = new ArrayList<>();
        for (String nickname : req.getTargetNicknames()) {
            OMessage message = OMessage.builder()
                    .read(false)
                    .type(req.getType())
                    .activity(activity)
                    .messageFrom(user.getNickname())
                    .to(nickname)
                    .user(user)
                    .title(title)
                    .detail(req.getContents())
                    .build();
            messageRepository.save(message);
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
        return req.getContents().substring(0,50);
    }

    public PrivateMessageSendResDto sendPrivateMessage(PrivateMessageSendReqDto userId) {
        return null;
    }

    @Transactional
    public MessageDetailResDto getMessage(Long messageId) {
        OMessage message = messageRepository.findById(messageId)
                .orElseThrow(()->new ResourceNotFoundException("메세지 아이디에 해당하는 메세지가 없습니다"));
        return MessageDetailResDto.fromEntity(message);
    }
}

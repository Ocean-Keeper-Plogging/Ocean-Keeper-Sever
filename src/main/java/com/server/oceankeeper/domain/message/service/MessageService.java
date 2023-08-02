package com.server.oceankeeper.domain.message.service;

import com.server.oceankeeper.domain.activity.service.ActivityService;
import com.server.oceankeeper.domain.message.dto.request.MessageSendReqDto;
import com.server.oceankeeper.domain.message.dto.request.PrivateMessageSendReqDto;
import com.server.oceankeeper.domain.message.dto.response.MessageSendResDto;
import com.server.oceankeeper.domain.message.dto.response.PostResDto;
import com.server.oceankeeper.domain.message.dto.response.PrivateMessageSendResDto;
import com.server.oceankeeper.domain.message.entity.MessageType;
import com.server.oceankeeper.domain.message.entity.OMessage;
import com.server.oceankeeper.domain.message.repository.MessageRepository;
import com.server.oceankeeper.domain.user.entitiy.OUser;
import com.server.oceankeeper.util.UUIDGenerator;
import com.server.oceankeeper.util.UserAccessValidationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class MessageService {
    private final MessageRepository messageRepository;
    private final ActivityService activityService;
    private final UserAccessValidationUtil userAccessValidationUtil;

    public PostResDto getMailing(String userId, MessageType type, HttpServletRequest request) throws Exception {
        userAccessValidationUtil.validate(request);

        OUser user = activityService.getUser(userId);
        List<OMessage> messages = messageRepository.findByUserAndMessageType(user, type);

        return new PostResDto(messages.stream()
                .map(m -> new PostResDto.MessageDto(
                        m.getId(),
                        m.getMessageType(),
                        m.getMessageFrom(),
                        UUIDGenerator.changeUuidToString(m.getActivity().getUuid()),
                        m.getTitle(),
                        m.getTime(),
                        m.isRead()))
                .collect(Collectors.toList()));
    }

    public MessageSendResDto sendMessage(MessageSendReqDto message) {
        return null;
    }

    public PrivateMessageSendResDto sendPrivateMessage(PrivateMessageSendReqDto userId) {
        return null;
    }
}

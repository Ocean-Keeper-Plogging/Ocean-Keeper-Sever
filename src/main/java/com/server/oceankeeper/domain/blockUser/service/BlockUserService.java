package com.server.oceankeeper.domain.blockUser.service;

import com.server.oceankeeper.domain.blockUser.repository.BlockUserRepository;
import com.server.oceankeeper.domain.blockUser.entity.BlockUser;
import com.server.oceankeeper.domain.user.entity.OUser;
import com.server.oceankeeper.domain.user.service.UserService;
import com.server.oceankeeper.util.TokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@Service
public class BlockUserService {
    private final BlockUserRepository blockUserRepository;
    private final UserService userService;
    private final TokenUtil tokenUtil;

    @Transactional
    public void blockUser(String hostNickname, HttpServletRequest servletRequest) {
        OUser host = userService.findByNickname(hostNickname);
        OUser blocker = tokenUtil.getUserFromHeader(servletRequest);

        BlockUser blockedUser = BlockUser.builder()
                .blocker(blocker)
                .blockedUser(host)
                .build();
        blocker.addBlockedUser(blockedUser);
        blockUserRepository.save(blockedUser);
    }
}

package com.server.oceankeeper.util;

import com.server.oceankeeper.domain.user.entitiy.OUser;
import com.server.oceankeeper.global.exception.IllegalRequestException;
import com.server.oceankeeper.global.exception.ResourceNotFoundException;
import com.server.oceankeeper.global.exception.UuidValidException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserAccessValidationUtil {
    private final TokenUtil tokenUtil;
    private static final String USERID_PREFIX = "user";

    @Transactional
    public void validate(HttpServletRequest request) throws UuidValidException {
        OUser user = tokenUtil.getUserFromHeader(request);
        String userIdFromPath = request.getParameter(USERID_PREFIX);
        if (userIdFromPath == null) {
            throw new ResourceNotFoundException("해당 URI는 \"user\"를 포함하지 않습니다.");
        }
        if (!user.getUuid().equals(UUIDGenerator.changeUuidFromString(userIdFromPath))) {
            throw new IllegalRequestException("본인 외의 계정 정보를 조회하였습니다. 본인의 계정 정보만 조회할 수 있습니다.");
        }
    }

    @Transactional
    public OUser getUser(HttpServletRequest request) {
        return tokenUtil.getUserFromHeader(request);
    }

    @Transactional
    public boolean validate(String userId, HttpServletRequest request) throws Exception {
        OUser user = tokenUtil.getUserFromHeader(request);

        if (!user.getUuid().equals(UUIDGenerator.changeUuidFromString(userId))) {
            throw new IllegalRequestException("본인 외의 계정 정보를 조회하였습니다. 본인의 계정 정보만 조회할 수 있습니다.");
        }
        return true;
    }
}

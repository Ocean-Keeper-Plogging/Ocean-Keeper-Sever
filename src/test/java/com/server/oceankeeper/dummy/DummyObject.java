package com.server.oceankeeper.dummy;

import com.server.oceankeeper.domain.user.entitiy.OUser;
import com.server.oceankeeper.domain.user.entitiy.UserRole;
import com.server.oceankeeper.domain.user.entitiy.UserStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.UUID;

public class DummyObject {
    static BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    static String encPassword = passwordEncoder.encode("-");

    //리포지토리까지 사용할 경우의 객체
    protected OUser newUserWithR(String nickname, String provider, String providerId) {
        return OUser.builder()
                .nickname(nickname)
                .provider(provider)
                .providerId(providerId)
                .email(nickname + "@" + provider + ".com")
                .profile("none")
                .status(UserStatus.ACTIVE)
                .password(encPassword)
                .role(UserRole.USER)
                .build();
    }

    //리포지토리를 사용하지 않을 경우의 Mock 객체 id와 날짜 정보까지 수동으로 넣어야한다.
    protected OUser newMockUser(Long id, String nickname, String provider, String providerId, UUID uuid) {
        return OUser.builder()
                .id(id)
                .nickname(nickname)
                .provider(provider)
                .provider(providerId)
                .email(nickname + "@" + provider + ".com")
                .profile("none")
                .status(UserStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .password("_")
                .role(UserRole.USER)
                .uuid(uuid)
                .build();
    }

}

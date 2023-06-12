package com.server.oceankeeper.dummy;

import com.server.oceankeeper.domain.activity.entity.*;
import com.server.oceankeeper.domain.crew.entitiy.CrewRole;
import com.server.oceankeeper.domain.crew.entitiy.CrewStatus;
import com.server.oceankeeper.domain.crew.entitiy.Crews;
import com.server.oceankeeper.domain.user.entitiy.OUser;
import com.server.oceankeeper.domain.user.entitiy.UserRole;
import com.server.oceankeeper.domain.user.entitiy.UserStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

public class DummyObject {
    static BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    static String encPassword = passwordEncoder.encode("-");

    //리포지토리까지 사용할 경우의 객체
    protected OUser newUserWithR(String nickname, String provider, String providerId,UUID uuid) {
        return OUser.builder()
                .uuid(uuid)
                .nickname(nickname)
                .provider(provider)
                .providerId(providerId)
                .email(nickname + "@" + provider + ".com")
                .profile("none")
                .status(UserStatus.ACTIVE)
                .password(encPassword)
                .deviceToken(genRandomString())
                .role(UserRole.USER)
                .updatedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now().minusMinutes(10))
                .build();
    }

    //리포지토리를 사용하지 않을 경우의 Mock 객체 id와 날짜 정보까지 수동으로 넣어야한다.
    protected OUser newMockUser(Long id, String nickname, String provider, String providerId, UUID uuid) {
        return OUser.builder()
                .id(id)
                .nickname(nickname)
                .provider(provider)
                .providerId(providerId)
                .email(nickname + "@" + provider + ".com")
                .profile("none")
                .status(UserStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deviceToken(genRandomString())
                .password("_")
                .role(UserRole.USER)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .uuid(uuid)
                .build();
    }

    protected Activity newMockActivity(int quota, ActivityStatus activityStatus) {
        return Activity.builder()
                .participants(1)
                .quota(5)
                .uuid(UUID.randomUUID())
                .activityStatus(activityStatus)
                .garbageCategory(GarbageCategory.COASTAL)
                .location(new Location(123.1, 123.2, "제주", "제주" + genRandomString()))
                .locationTag(LocationTag.JEJU)
                .recruitEndAt(LocalDate.now().plusDays(5))
                .recruitStartAt(LocalDate.now())
                .startAt(LocalDateTime.now().plusDays(10))
                .title("activity "+ genRandomString())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private String genRandomString() {
        return new Random().ints(97, 122 + 1)
                .limit(5)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    protected Crews newCrew(Activity activity, OUser user, CrewStatus crewStatus, CrewRole role) {
        return Crews.builder()
                .crewStatus(crewStatus)
                .activity(activity)
                .user(user)
                .uuid(UUID.randomUUID())
                .activityRole(role)
                .startPoint("서울")
                .privacyAgreement(true)
                .phoneNumber("01012341234")
                .name(user.getNickname())
                .email(user.getEmail())
                .applyAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}

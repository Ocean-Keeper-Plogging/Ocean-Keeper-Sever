package com.server.oceankeeper.dummy;

import com.server.oceankeeper.domain.activity.entity.*;
import com.server.oceankeeper.domain.crew.entity.CrewRole;
import com.server.oceankeeper.domain.crew.entity.CrewStatus;
import com.server.oceankeeper.domain.crew.entity.Crews;
import com.server.oceankeeper.domain.statistics.entity.ActivityInfo;
import com.server.oceankeeper.domain.user.entity.OUser;
import com.server.oceankeeper.domain.user.entity.UserRole;
import com.server.oceankeeper.domain.user.entity.UserStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

public class DummyObject {
    static BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    static String encPassword = passwordEncoder.encode("-9___1-");

    //리포지토리까지 사용할 경우의 객체
    protected OUser newUserWithR(String nickname, String provider, String providerId, UUID uuid) {
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

    protected ActivityInfo newUserInfoWithR(OUser user) {
        return ActivityInfo.builder()
                .user(user)
                .countCancel(0)
                .countHosting(0)
                .countActivity(0)
                .countNoShow(0)
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

    protected OUser newMockUser(Long id, UUID uuid) {
        String nickname = "nickname" + genRandomString();
        return OUser.builder()
                .id(id)
                .nickname(nickname)
                .provider("naver")
                .providerId(genRandomString() + genRandomString() + genRandomString())
                .email(nickname + "@" + "naver.com")
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

    protected Activity newMockActivity(OUser host, int quota, ActivityStatus activityStatus,
                                       LocationTag locationTag, GarbageCategory garbageCategory, int startAtPlusDay, UUID uuid) {
        return Activity.builder()
                .participants(100)
                .quota(quota)
                .uuid(uuid)
                .activityStatus(activityStatus)
                .garbageCategory(garbageCategory)
                .location(new Location(123.1, 123.2, "제주" + genRandomString()))
                .locationTag(locationTag)
                .recruitStartAt(LocalDate.now())
                .recruitEndAt(LocalDate.now().plusDays(5))
                .startAt(LocalDateTime.now().plusDays(startAtPlusDay))
                .title("activity " + genRandomString())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .rewards("")
                .host(host)
                .build();
    }

    protected Activity newMockActivity(OUser host, UUID uuid) {
        return newMockActivity(host, 100, ActivityStatus.OPEN, LocationTag.EAST, GarbageCategory.COASTAL, 10, uuid);
    }

    private String genRandomString() {
        return new Random().ints(97, 122 + 1)
                .limit(5)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    protected Crews newCrew(Activity activity, OUser user, OUser host, CrewStatus crewStatus, CrewRole role) {
        return Crews.builder()
                .crewStatus(crewStatus)
                .activity(activity)
                .user(user)
                .host(host)
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

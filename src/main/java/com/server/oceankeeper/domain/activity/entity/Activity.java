package com.server.oceankeeper.domain.activity.entity;

import com.server.oceankeeper.global.BaseEntity;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter //TODO : 개선
@Table(indexes = {
        @Index(name = "i_uuid", columnList = "uuid", unique = true),
        @Index(name = "i_recruitEndAt", columnList = "recruitEndAt"),
        @Index(name = "i_startAt", columnList = "startAt"),
        @Index(name = "i_garbage", columnList = "garbageCategory"),
        @Index(name = "i_location", columnList = "locationTag"),
        @Index(name = "i_reward", columnList = "rewards"),
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Activity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Location location;

    @Column(columnDefinition = "BINARY(16)", nullable = false)
    private UUID uuid;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LocationTag locationTag;

    @Column(length = 30, nullable = false)
    private String title;

    private String thumbnail;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GarbageCategory garbageCategory;

    @Column(nullable = false)
    private Integer quota;

    @Column(nullable = false)
    private Integer participants;

    @Column(nullable = false)
    private LocalDate recruitStartAt;
    @Column(nullable = false)
    private LocalDate recruitEndAt;

    @Column(nullable = false)
    private LocalDateTime startAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActivityStatus activityStatus;

    @Column(length = 1000)
    private String rewards;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Activity)) return false;

        Activity activity = (Activity) o;

        return uuid.equals(activity.uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    @Builder
    public Activity(UUID uuid, LocationTag locationTag, String title,
                    String thumbnail, GarbageCategory garbageCategory, Integer quota,
                    Integer participants, LocalDate recruitStartAt, LocalDate recruitEndAt,
                    LocalDateTime startAt, ActivityStatus activityStatus, Location location,
                    LocalDateTime createdAt, LocalDateTime updatedAt, String rewards) {
        this.uuid = uuid;
        this.locationTag = locationTag;
        this.title = title;
        this.thumbnail = thumbnail;
        this.garbageCategory = garbageCategory;
        this.quota = quota;
        this.participants = participants;
        this.recruitStartAt = recruitStartAt;
        this.recruitEndAt = recruitEndAt;
        this.startAt = startAt;
        this.activityStatus = activityStatus;
        this.location = location;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.rewards = rewards;
    }

    public void reset() {
        this.title = "";
        this.thumbnail = null;
        this.quota = -1;
        this.participants = -1;
        this.recruitStartAt = LocalDate.now().plusYears(999);
        this.recruitEndAt = LocalDate.now().plusYears(999);
        this.startAt = LocalDateTime.now().plusYears(999);
        this.activityStatus = ActivityStatus.CANCEL;
        this.location = null;
        this.rewards = null;
    }

    public void addParticipant() {
        participants++;
    }

    public void removeParticipant() {
        if (participants >= 1)
            participants--;
    }

    public void closeRecruitment() {
        activityStatus = ActivityStatus.RECRUITMENT_CLOSE;
    }

    public void closeActivity() {
        activityStatus = ActivityStatus.CLOSED;
    }
}

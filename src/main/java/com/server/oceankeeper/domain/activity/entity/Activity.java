package com.server.oceankeeper.domain.activity.entity;

import com.server.oceankeeper.domain.user.entitiy.OUser;
import com.server.oceankeeper.global.BaseEntity;
import com.server.oceankeeper.global.exception.IllegalRequestException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Table(indexes = @Index(name = "i_uuid", columnList = "uuid", unique = true))
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

    //@Column(columnDefinition = "default ''")
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
                    LocalDateTime startAt, ActivityStatus activityStatus, Location location) {
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
    }

    public void addParticipant() {
        if (quota > participants)
            participants++;
        else
            throw new IllegalRequestException("정원이 찼습니다.");
    }
}

package com.server.oceankeeper.domain.activity.entity;

import com.server.oceankeeper.domain.user.entitiy.OUser;
import com.server.oceankeeper.global.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@Table(indexes = @Index(name = "i_uuid", columnList = "uuid"))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Activity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @ManyToOne(optional = false, fetch = FetchType.LAZY)
//    @JoinColumn(name = "USER_ID")
//    private OUser user;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Activity activity = (Activity) o;
        return id != null && Objects.equals(id, activity.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Builder
    public Activity(OUser user, UUID uuid, LocationTag locationTag, String title,
                    String thumbnail, GarbageCategory garbageCategory, Integer quota,
                    Integer participants, LocalDate recruitStartAt, LocalDate recruitEndAt,
                    LocalDateTime startAt, ActivityStatus activityStatus, Location location) {
        //this.user = user;
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
}

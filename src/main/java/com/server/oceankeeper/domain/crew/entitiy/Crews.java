package com.server.oceankeeper.domain.crew.entitiy;

import com.server.oceankeeper.domain.activity.entity.Activity;
import com.server.oceankeeper.domain.user.entitiy.OUser;
import com.server.oceankeeper.global.BaseEntity;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(indexes = @Index(name = "i_uuid", columnList = "uuid", unique = true))
@ToString(exclude = {"activity", "user"})
public class Crews extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "BINARY(16)", nullable = false)
    private UUID uuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ACTIVITY_ID")
    private Activity activity;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    @JoinColumn(name = "USER_ID")
    private OUser user;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private CrewRole activityRole;

    @Column(length = 8)
    private String dayOfBirth;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private CrewStatus crewStatus;

    @Column(length = 30)
    private String name;

    @Column(length = 15)
    private String phoneNumber;

    private String id1365;

    @Column(length = 50)
    private String email;

    @Column(length = 50)
    private String startPoint;

    @Column(length = 15)
    private String transportation;

    @Column(length = 500)
    private String question;

    private LocalDateTime applyAt;
    private LocalDateTime cancelAt;

    private LocalDateTime expiredAt;
    private LocalDateTime finishAt;

    private boolean privacyAgreement;

    @Builder
    public Crews(Long id, UUID uuid, Activity activity, OUser user, CrewRole activityRole,
                 CrewStatus crewStatus, String name, String phoneNumber, String id1365, String email,
                 String startPoint, String transportation, String question, LocalDateTime applyAt,
                 LocalDateTime cancelAt, LocalDateTime expiredAt, LocalDateTime finishAt, String dayOfBirth,
                 boolean privacyAgreement, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.uuid = uuid;
        this.activity = activity;
        this.user = user;
        this.activityRole = activityRole;
        this.crewStatus = crewStatus;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.id1365 = id1365;
        this.email = email;
        this.startPoint = startPoint;
        this.transportation = transportation;
        this.question = question;
        this.applyAt = applyAt;
        this.cancelAt = cancelAt;
        this.expiredAt = expiredAt;
        this.finishAt = finishAt;
        this.privacyAgreement = privacyAgreement;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.dayOfBirth = dayOfBirth;
    }

    public void reset() {
        this.crewStatus = CrewStatus.CANCEL;
        this.name = null;
        this.phoneNumber = null;
        this.id1365 = null;
        this.email = null;
        this.startPoint = null;
        this.transportation = null;
        this.question = null;
        this.cancelAt = LocalDateTime.now();
        this.dayOfBirth = null;
        this.expiredAt = null;
        this.finishAt = null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Crews)) return false;

        Crews crews = (Crews) o;

        if (!activity.equals(crews.activity)) return false;
        if (!user.equals(crews.user)) return false;
        return crewStatus == crews.crewStatus;
    }

    @Override
    public int hashCode() {
        int result = activity.hashCode();
        result = 31 * result + user.hashCode();
        result = 31 * result + crewStatus.hashCode();
        return result;
    }

    public void closeApplication() {
        crewStatus = CrewStatus.CLOSED;
    }

    public void changeCrewStatus(CrewStatus status) {
        this.crewStatus = status;
    }
}

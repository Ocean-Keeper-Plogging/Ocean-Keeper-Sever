package com.server.oceankeeper.domain.crew;

import com.server.oceankeeper.domain.activity.entity.Activity;
import com.server.oceankeeper.domain.user.entitiy.OUser;
import com.server.oceankeeper.global.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(indexes = @Index(name = "i_uuid", columnList = "uuid"))
public class Crews extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "BINARY(16)", nullable = false)
    private UUID uuid;

    @ManyToOne
    @JoinColumn(name="ACTIVITY_ID")
    private Activity activity;

    @ManyToOne
    @JoinColumn(name="USER_ID")
    private OUser user;

    @Column(nullable = false)
    private CrewRole activityRole;

    @Column(nullable = false)
    private CrewStatus crewStatus;

    @Column(length = 30)
    private String name;

    @Column(length = 15)
    private String phoneNumber;

    private String Id1365;

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
}

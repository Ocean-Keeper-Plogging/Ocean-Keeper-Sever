package com.server.oceankeeper.domain.user.entitiy;


import com.server.oceankeeper.global.BaseEntity;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;


@Getter
@Entity
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode
@Table(name = "USERS", indexes = @Index(name = "i_uuid", columnList = "uuid"))
public class OUser extends BaseEntity {
    @Id // primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "BINARY(16)", nullable = false)
    private UUID uuid;

    @Column(nullable = false, length = 20)
    private String provider;
    @Column(nullable = false, length = 50)
    private String providerId;
    @Column(unique = true, nullable = false, length = 20)
    private String nickname;

    @Column(nullable = false, length = 40)
    private String email;
    @Column(length = 1000)
    @Setter
    private String profile;

    @Column(length = 1000)
    private String deviceToken;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Column(nullable = false)
    private String password;

    @Builder
    public OUser(long id, String nickname, String email, String profile,
                 UserStatus status, String provider, String providerId,
                 UserRole role, LocalDateTime createdAt, LocalDateTime updatedAt,
                 String password, String deviceToken, UUID uuid) {
        this.id = id;
        this.deviceToken = deviceToken;
        this.nickname = nickname;
        this.email = email;
        this.profile = profile;
        this.status = status;
        this.provider = provider;
        this.providerId = providerId;
        this.role = role;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.password = password;
        this.uuid = uuid;
    }

    public void changeNickname(String nickname) {
        this.nickname = nickname;
    }

    public void initializePassword(String password) {
        this.password = password;
    }
}



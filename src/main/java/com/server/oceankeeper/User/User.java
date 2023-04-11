package com.server.oceankeeper.User;


import com.server.oceankeeper.User.UserEnum.UserRole;
import com.server.oceankeeper.User.UserEnum.UserStatus;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.time.LocalDateTime;


@Data
@Entity
@NoArgsConstructor
public class User {
    @Id // primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, length = 20)
    private String provider;
    @Column(nullable = false, length = 20)
    private String providerId;
    @Column(unique = true, nullable = false, length = 20)
    private String nickname;

    @Column(unique = true, nullable = false, length = 20)
    private String email;
    @Column(length = 1000)
    private String profile;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;



    //todo
        //이미지 저장로직 만들어야한다.

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;


    @Builder
    public User(long id, String nickname, String email, String profile, UserStatus status, String provider, String providerId, UserRole role, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.nickname = nickname;
        this.email = email;
        this.profile = profile;
        this.status = status;
        this.provider = provider;
        this.providerId = providerId;
        this.role = role;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}



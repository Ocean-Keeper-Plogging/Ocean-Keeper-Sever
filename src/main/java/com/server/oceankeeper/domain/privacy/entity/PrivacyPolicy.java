package com.server.oceankeeper.domain.privacy.entity;

import com.server.oceankeeper.global.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PrivacyPolicy extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Lob
    @Column(length = 20000, nullable = false)
    private String contents;

    @Builder
    public PrivacyPolicy(Long id, String contents, LocalDateTime createdAt) {
        this.id = id;
        this.contents = contents;
        this.createdAt = createdAt;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return this.updatedAt;
    }
}

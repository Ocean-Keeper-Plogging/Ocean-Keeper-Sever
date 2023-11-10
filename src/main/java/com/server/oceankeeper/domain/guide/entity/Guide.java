package com.server.oceankeeper.domain.guide.entity;

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
public class Guide extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String videoLink;

    @Column
    private String videoName;

    @Column
    private String title;

    @Builder
    public Guide(Long id, String videoLink, String videoName, String title, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.id = id;
        this.videoLink = videoLink;
        this.videoName = videoName;
        this.title = title;
        this.createdAt = createdAt;
        this.updatedAt = modifiedAt;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return this.updatedAt;
    }
}

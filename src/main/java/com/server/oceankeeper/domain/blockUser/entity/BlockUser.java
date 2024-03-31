package com.server.oceankeeper.domain.blockUser.entity;

import com.server.oceankeeper.domain.user.entity.OUser;
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
public class BlockUser extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blocker_id")
    private OUser blocker;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blocked_id")
    private OUser blockedUser;

    @Builder
    public BlockUser(Long id, OUser blocker, OUser blockedUser, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.blocker = blocker;
        this.blockedUser = blockedUser;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void setBlocker(OUser blocker){
        this.blocker = blocker;
    }
}

package com.server.oceankeeper.domain.notification.entity;

import com.server.oceankeeper.domain.notification.dto.MessagePreFormat;
import com.server.oceankeeper.domain.user.entitiy.OUser;
import com.server.oceankeeper.global.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Boolean isRead;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private OUser user;

    @Enumerated
    private MessagePreFormat type;

    @Builder
    public Notification(Long id, Boolean isRead, OUser user, MessagePreFormat type) {
        this.id = id;
        this.isRead = isRead;
        this.user = user;
        this.type = type;
    }
}

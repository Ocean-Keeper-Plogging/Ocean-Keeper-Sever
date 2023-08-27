package com.server.oceankeeper.domain.message.entity;

import com.server.oceankeeper.domain.activity.entity.Activity;
import com.server.oceankeeper.domain.user.entitiy.OUser;
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
@Table(name = "omessages", indexes = {
        @Index(name = "i_type", columnList = "messageType"),
        @Index(name = "i_message_from", columnList = "messageFrom"),
        @Index(name = "i_message_to", columnList = "messageTo")})
public class OMessage extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(value = EnumType.STRING)
    private MessageType messageType;

    private String messageFrom;

    private String messageTo;

    public LocalDateTime getTime() {
        return createdAt;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ACTIVITY_MESSAGE_ID")
    private Activity activity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_MESSAGE_ID")
    private OUser user;

    private String title;

    private String detail;

    private boolean isRead;

    @Builder
    public OMessage(Long id, MessageType type, String messageFrom, String to, Activity activity, OUser user, String title, String detail, boolean read) {
        this.id = id;
        this.messageType = type;
        this.messageFrom = messageFrom;
        this.messageTo = to;
        this.activity = activity;
        this.user = user;
        this.title = title;
        this.detail = detail;
        this.isRead = read;
    }
}

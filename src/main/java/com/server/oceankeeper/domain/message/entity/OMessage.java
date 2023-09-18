package com.server.oceankeeper.domain.message.entity;

import com.server.oceankeeper.domain.activity.entity.Activity;
import com.server.oceankeeper.domain.user.entitiy.OUser;
import com.server.oceankeeper.global.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "omessages", indexes = {
        @Index(name = "i_type", columnList = "messageType"),
        @Index(name = "i_message_from", columnList = "messageFrom"),
        @Index(name = "i_message_to", columnList = "messageTo")})
@Slf4j
public class OMessage extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(value = EnumType.STRING)
    private MessageType messageType;

    @NotBlank
    private String messageFrom;

    @NotBlank
    private String messageTo;

    public LocalDateTime getTime() {
        return createdAt;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ACTIVITY_ID")
    private Activity activity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private OUser user;

    private String title;

    private boolean isRead;

    @NotNull
    private Boolean isDeleteFromSender;

    @NotNull
    private Boolean isDeleteFromReceiver;

    public void checkDeletionFromSender(boolean b) {
        this.isDeleteFromSender = b;
    }

    public void checkDeletionFromReceiver(boolean b) {
        this.isDeleteFromReceiver = b;
    }

    public void messageRead(boolean b) {
        this.isRead = b;
    }

    @Builder
    public OMessage(Long id, MessageType type, String messageFrom, String to,
                    Activity activity, OUser user, String title, boolean read,
                    Boolean isDeleteFromSender, Boolean isDeleteFromReceiver) {
        this.id = id;
        this.messageType = type;
        this.messageFrom = messageFrom;
        this.messageTo = to;
        this.activity = activity;
        this.user = user;
        this.title = title;
        this.isRead = read;
        this.isDeleteFromSender = isDeleteFromSender;
        this.isDeleteFromReceiver = isDeleteFromReceiver;
    }
}

package com.server.oceankeeper.domain.message.entity;

import com.server.oceankeeper.global.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "messages_detail")
public class MessageDetail extends BaseEntity {
    @Id
    private Long id;
    private String detail;

    @Builder
    public MessageDetail(Long id, String detail) {
        this.id = id;
        this.detail = detail;
    }
}

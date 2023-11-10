package com.server.oceankeeper.domain.statistics.entity;

import com.server.oceankeeper.domain.user.entitiy.OUser;
import com.server.oceankeeper.global.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Table
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ActivityInfo extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "user_id")
    private OUser user;

    @NotNull
    private Integer countActivity;

    @NotNull
    private Integer countHosting;

    @NotNull
    private Integer countNoShow;

    @NotNull
    private Integer countCancel;

    public void addActivityCount() {
        this.countActivity++;
    }
    public void subActivityCount() {
        this.countActivity--;
    }
    public void addHostingCount() {
        this.countHosting++;
    }
    public void addNoShowCount() {
        this.countNoShow++;
    }
    public void addCancelCount() {
        this.countCancel++;
    }

    @Builder
    public ActivityInfo(Long id, OUser user, Integer countActivity, Integer countHosting, Integer countNoShow,
                        Integer countCancel) {
        this.id = id;
        this.user = user;
        this.countActivity = countActivity;
        this.countHosting = countHosting;
        this.countNoShow = countNoShow;
        this.countCancel = countCancel;
    }
}

package com.server.oceankeeper.domain.statistics.entity;

import com.server.oceankeeper.domain.user.entity.OUser;
import com.server.oceankeeper.global.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Table
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ActivityInfo extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "USER_ID")
    private OUser user;

    @NotNull
    private Integer countActivity;

    @NotNull
    private Integer countHosting;

    @NotNull
    private Integer countNoShow;

    @NotNull
    private Integer countCancel;

    public void subActivityCount() {
        if (this.countActivity > 0)
            this.countActivity--;
    }

    public void subHostingCount() {
        if (this.countHosting > 0) this.countHosting--;
    }

    public void addActivityCount() {
        this.countActivity++;
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

    public void reset() {
        this.countActivity = -1;
        this.countHosting = -1;
        this.countNoShow = -1;
        this.countCancel = -1;
    }
}

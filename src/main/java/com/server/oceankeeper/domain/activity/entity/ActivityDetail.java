package com.server.oceankeeper.domain.activity.entity;

import lombok.*;

import javax.persistence.*;
import java.util.UUID;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter //TODO : 개선
@Table(indexes = @Index(name = "i_uuid", columnList = "uuid",unique = true))
public class ActivityDetail {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @MapsId
    @OneToOne(fetch=FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name="ACTIVITY_ID")
    @Setter
    private Activity activity;

    @Column(columnDefinition = "BINARY(16)", nullable = false)
    private UUID uuid;

    @Column(length = 1000)
    @Lob
    private String activityStory;

    private String storyImage;

    @Column(length = 1000)
    @Lob
    private String keeperIntroduction;

    private String keeperImage;

    @Column(length = 1000)
    private String transportation;

    @Column(length = 1000)
    @Lob
    private String programDetails;

    @Column(length = 1000)
    @Lob
    private String preparation;

    @Column(length = 1000)
    private String rewards;

    @Column(length = 1000)
    @Lob
    private String etc;

    @Builder
    public ActivityDetail(Long id, Activity activity, UUID uuid, String activityStory,
                          String storyImage, String keeperIntroduction, String keeperImage,
                          String transportation, String programDetails, String preparation, String rewards, String etc) {
        this.id = id;
        this.activity = activity;
        this.uuid = uuid;
        this.activityStory = activityStory;
        this.storyImage = storyImage;
        this.keeperIntroduction = keeperIntroduction;
        this.keeperImage = keeperImage;
        this.transportation = transportation;
        this.programDetails = programDetails;
        this.preparation = preparation;
        this.rewards = rewards;
        this.etc = etc;
    }
}

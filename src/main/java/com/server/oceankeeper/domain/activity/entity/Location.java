package com.server.oceankeeper.domain.activity.entity;

import javax.persistence.*;

@Embeddable
public class Location {
    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(length = 50)
    private String name;

    @Column(length = 50)
    private String detail;
}
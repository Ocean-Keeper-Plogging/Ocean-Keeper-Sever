package com.server.oceankeeper.domain.activity.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Location {
    @Column
    private Double latitude;

    @Column
    private Double longitude;

    @Column(length = 50)
    private String address;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Location)) return false;

        Location location = (Location) o;

        return address.equals(location.address);
    }

    @Override
    public int hashCode() {
        return address.hashCode();
    }
}
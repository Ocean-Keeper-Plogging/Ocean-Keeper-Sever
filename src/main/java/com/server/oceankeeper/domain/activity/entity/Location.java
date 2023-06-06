package com.server.oceankeeper.domain.activity.entity;

import com.google.common.base.Objects;
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

    @Column(length = 50, nullable = false)
    private String name;

    @Column(length = 50)
    private String detail;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Location)) return false;

        Location location = (Location) o;

        return name.equals(location.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
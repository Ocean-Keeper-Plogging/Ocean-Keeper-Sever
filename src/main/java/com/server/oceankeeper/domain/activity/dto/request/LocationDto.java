package com.server.oceankeeper.domain.activity.dto.request;

import com.server.oceankeeper.domain.activity.entity.Location;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@ToString
@Getter
@Setter
@Builder
@AllArgsConstructor
@EqualsAndHashCode
public class LocationDto {
    @ApiModelProperty(
            value = "위치",
            example = "제주 제주시 조천읍 조함해안로 525"
    )
    private final String address;

    @ApiModelProperty(
            value = "위도",
            example = "33.543108299999986"
    )
    private final Double latitude;

    @ApiModelProperty(
            value = "위치",
            example = "126.66969249999995"
    )
    private final Double longitude;

    public Location toEntity() {
        return Location.builder()
                .latitude(latitude)
                .longitude(longitude)
                .address(address)
                .build();
    }

    public LocationDto(Location entity) {
        this.latitude = entity.getLatitude();
        this.longitude = entity.getLongitude();
        this.address = entity.getAddress();
    }
}

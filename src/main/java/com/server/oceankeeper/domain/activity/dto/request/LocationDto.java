package com.server.oceankeeper.domain.activity.dto.request;

import com.server.oceankeeper.domain.activity.entity.Location;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotEmpty;

@ToString
@Getter
@Setter
@Builder
@AllArgsConstructor
public class LocationDto {
    @ApiModelProperty(
            value = "위치",
            example = "함덕 해수욕장",
            required = true
    )
    @NotEmpty
    private final String location;

    @ApiModelProperty(
            value = "위치",
            example = "제주 제주시 조천읍 조함해안로 525"
    )
    private final String detail;

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

    public Location toEntity(){
        return Location.builder()
                .latitude(latitude)
                .longitude(longitude)
                .detail(detail)
                .name(location)
                .build();
    }


}

package com.server.oceankeeper.domain.profile.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;


@Data
public class ProfileResDto {

    @ApiModelProperty(
            value = "s3 내 이미지 파일의 전체 url",
            dataType = "String",
            example = "https://oceankeeper-image.s3.ap-northeast-2.amazonaws.com/profile/cb1de9d3-b982-4b9f-a574-174b834bae2etest.png\"")
    @NotEmpty
    private String url;

    public ProfileResDto() {

    }

    @Builder
    public ProfileResDto(String fileFullPath, String url) {
        this.url = url;
    }
}

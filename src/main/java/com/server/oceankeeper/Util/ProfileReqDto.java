package com.server.oceankeeper.Util;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;


@Data
public class ProfileReqDto {
    @ApiModelProperty(
            value = "s3 내 이미지 파일의 경로",
            dataType = "String",
            example = "profile/cb1de9d3-b982-4b9f-a574-174b834bae2etest.png")
    private String fileFullPath;

    @ApiModelProperty(
            value = "s3 내 이미지 파일의 전체 url",
            dataType = "String",
            example = "https://oceankeeper-image.s3.ap-northeast-2.amazonaws.com/profile/cb1de9d3-b982-4b9f-a574-174b834bae2etest.png\"")
    private String url;

    public ProfileReqDto() {

    }

    @Builder
    public ProfileReqDto(String fileFullPath, String url) {
        this.fileFullPath = fileFullPath;
        this.url = url;
    }
}

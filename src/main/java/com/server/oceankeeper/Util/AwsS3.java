package com.server.oceankeeper.Util;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;


@Data
public class AwsS3 {
    @ApiModelProperty(
            value = "s3 내 이미지 파일의 경로",
            dataType = "String",
            example = "www.naver.com")
    private String fileFullPath;

    @ApiModelProperty(
            value = "s3 내 이미지 파일의 전체 url",
            dataType = "String",
            example = "www.naver.com")
    private String url;

    public AwsS3() {

    }

    @Builder
    public AwsS3(String fileFullPath, String url) {
        this.fileFullPath = fileFullPath;
        this.url = url;
    }
}

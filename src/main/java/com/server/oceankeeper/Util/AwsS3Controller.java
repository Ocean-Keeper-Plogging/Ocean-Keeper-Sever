package com.server.oceankeeper.Util;

import com.amazonaws.Response;
import com.server.oceankeeper.DTO.ResponseDto;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/thumbnail")
@RequiredArgsConstructor
public class AwsS3Controller {

    private final AwsS3Service awsS3Service;
    @ApiOperation(value = "썸네일 등록", notes = "썸네일 이미지를 S3에 저장 후 url을 반환합니다.", response = AwsS3.class)
    @PostMapping()
    public ResponseEntity<?> upload(@RequestPart("thumbnail") MultipartFile thumbnail) throws IOException {
        AwsS3 awsDto =awsS3Service.multipartUpload(thumbnail,"thumbnails");
        return new ResponseEntity<>(new ResponseDto<AwsS3>(1, "썸네일 업로드 성공", awsDto), HttpStatus.CREATED);
    }

    //헤더에 있는 user id 까보고 썸네일 삭제
    @DeleteMapping()
    public ResponseEntity<?> remove(AwsS3 awsS3) {
        awsS3Service.remove(awsS3);
        return new ResponseEntity<>(new ResponseDto<AwsS3>(1,"썸네일이 정상적으로 제거되었습니다.", awsS3), HttpStatus.NO_CONTENT);
    }


}
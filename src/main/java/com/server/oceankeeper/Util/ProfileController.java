package com.server.oceankeeper.Util;

import com.server.oceankeeper.Global.Jwt.JwtConfig;
import com.server.oceankeeper.Global.Jwt.JwtProcess;
import com.server.oceankeeper.DTO.ResponseDto;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@RequestMapping("")
@RequiredArgsConstructor
public class ProfileController {

    private final S3Service s3Service;
    private final ProfileService profileService;

    @ApiOperation(value = "썸네일 등록 [권한 필요 없음]", notes = "프로필 이미지를 S3에 저장 후 저장된 url을 반환합니다.", response = ProfileReqDto.class)
    @PostMapping("/profile")
    public ResponseEntity<?> upload(@RequestPart("profile") MultipartFile profile) throws IOException {
        ProfileReqDto profileImage = s3Service.multipartUpload(profile,"profile");
        return new ResponseEntity<>(new ResponseDto<ProfileReqDto>(1, "프로필 업로드 성공", profileImage), HttpStatus.CREATED);
    }


    //썸네일 수정
    @ApiOperation(value = "썸네일 수정 [권한 필요]", notes = "기존 프로필 이미지 s3 수정 후 저장된 url을 반환합니다.", response = ProfileReqDto.class)
    @PutMapping("/auth/profile")
    public ResponseEntity<?> edit(@RequestPart("profile") MultipartFile profile, HttpServletRequest request) throws IOException {
        String jwtToken = request.getHeader(JwtConfig.HEADER).replace(JwtConfig.TOKEN_PREFIX, "");
        Long id = JwtProcess.toUserId(jwtToken);
        //기존 파일 s3에서 삭제
        profileService.removeProfile(id);
        //s3에 업로드
        ProfileReqDto profileImage = s3Service.multipartUpload(profile,"profile");
        //db 정보 변경하기
        profileService.updateProfile(id, profileImage.getUrl());

        return new ResponseEntity<>(new ResponseDto<ProfileReqDto>(1, "프로필 수정 성공", profileImage), HttpStatus.CREATED);
    }



}
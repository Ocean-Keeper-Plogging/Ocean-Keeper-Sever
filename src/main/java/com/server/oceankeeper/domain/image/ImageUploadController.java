package com.server.oceankeeper.domain.image;

import com.server.oceankeeper.domain.image.dto.ProfileResDto;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/image")
@Slf4j
public class ImageUploadController {
    private final ImageService imageService;

    private static final String KEEPER = "keeper";
    private static final String PROFILE = "profile";
    private static final String STORY = "story";
    private static final String THUMBNAIL = "thumbnail";

    @ApiOperation(value = "썸네일 수정 [권한 필요]", notes = "기존 활동 키퍼 이미지 s3 수정 후 저장된 url을 반환합니다.", response = ProfileResDto.class)
    @PostMapping("/edit/keeper")
    public ResponseEntity<ProfileResDto> editKeeper(@RequestPart(KEEPER) MultipartFile file, HttpServletRequest request) throws IOException {
        return editFile(file, request, KEEPER);
    }

    @ApiOperation(value = "썸네일 등록 [권한 필요 없음]", notes = "활동 키퍼 이미지를 S3에 저장 후 저장된 url을 반환합니다.", response = ProfileResDto.class)
    @PostMapping("/keeper")
    public ResponseEntity<ProfileResDto> uploadKeeper(@RequestPart(KEEPER) MultipartFile file) throws IOException {
        return uploadFile(file, KEEPER);
    }

    @ApiOperation(value = "프로필 수정 [권한 필요]", notes = "기존 프로필 이미지 s3 수정 후 저장된 url을 반환합니다.", response = ProfileResDto.class)
    @PostMapping("/edit/profile")
    public ResponseEntity<ProfileResDto> editProfile(@RequestPart(PROFILE) MultipartFile file, HttpServletRequest request) throws IOException {
        log.info("ProfileImageUploadController edit :{}", request);
        return editFile(file, request, PROFILE);
    }

    @ApiOperation(value = "프로필 등록 [권한 필요 없음]", notes = "프로필 이미지를 S3에 저장 후 저장된 url을 반환합니다.", response = ProfileResDto.class)
    @PostMapping("/profile")
    public ResponseEntity<ProfileResDto> uploadProfile(@RequestPart(PROFILE) MultipartFile file) throws IOException {
        log.info("ProfileImageUploadController upload");
        return uploadFile(file, PROFILE);
    }

    @ApiOperation(value = "썸네일 수정 [권한 필요]", notes = "기존 활동 스토리 이미지 s3 수정 후 저장된 url을 반환합니다.", response = ProfileResDto.class)
    @PostMapping("/edit/story")
    public ResponseEntity<ProfileResDto> editStory(@RequestPart(STORY) MultipartFile file, HttpServletRequest request) throws IOException {
        return editFile(file, request, STORY);
    }

    @ApiOperation(value = "썸네일 등록 [권한 필요 없음]", notes = "활동 스토리 이미지를 S3에 저장 후 저장된 url을 반환합니다.", response = ProfileResDto.class)
    @PostMapping("/story")
    public ResponseEntity<ProfileResDto> uploadStory(@RequestPart(STORY) MultipartFile file) throws IOException {
        return uploadFile(file, STORY);
    }

    @ApiOperation(value = "썸네일 수정 [권한 필요]", notes = "기존 활동 썸네일 이미지 s3 수정 후 저장된 url을 반환합니다.", response = ProfileResDto.class)
    @PostMapping("/edit/thumbnail")
    public ResponseEntity<ProfileResDto> editThumbnail(@RequestPart(THUMBNAIL) MultipartFile file, HttpServletRequest request) throws IOException {
        return editFile(file, request, THUMBNAIL);
    }

    @ApiOperation(value = "썸네일 등록 [권한 필요 없음]", notes = "활동 썸네일 이미지를 S3에 저장 후 저장된 url을 반환합니다.", response = ProfileResDto.class)
    @PostMapping("/thumbnail")
    public ResponseEntity<ProfileResDto> uploadThumbnail(@RequestPart(THUMBNAIL) MultipartFile file) throws IOException {
        return uploadFile(file, THUMBNAIL);
    }

    private ResponseEntity<ProfileResDto> editFile(MultipartFile file, HttpServletRequest request, String dirName) throws IOException {
        ProfileResDto profileResDto = imageService.edit(file, request, dirName);

        return new ResponseEntity<>(profileResDto, HttpStatus.CREATED);
    }

    private ResponseEntity<ProfileResDto> uploadFile(MultipartFile file, String dirName) throws IOException {
        ProfileResDto profileResDto = imageService.upload(file, dirName);

        return ResponseEntity.ok(profileResDto);
    }
}

package com.server.oceankeeper.domain.guide.controller;

import com.server.oceankeeper.domain.guide.dto.request.GuideModifyReqDto;
import com.server.oceankeeper.domain.guide.dto.request.GuideReqDto;
import com.server.oceankeeper.domain.guide.dto.response.GuideDetailResDto;
import com.server.oceankeeper.domain.guide.dto.response.GuideResDto;
import com.server.oceankeeper.domain.guide.dto.response.GuideListResDto;
import com.server.oceankeeper.domain.guide.service.GuideService;
import com.server.oceankeeper.global.response.APIResponse;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class GuideController {
    private final GuideService service;

    @ApiOperation(value = "guide 리스트 조회[권한 필요]")
    @GetMapping("/guide")
    public ResponseEntity<APIResponse<GuideListResDto>> getGuide(@RequestParam(value = "guide-id",required = false) Long id,
                                                                 @RequestParam(value = "size",required = false) Integer size) {
        GuideListResDto response = service.get(id, size);
        return ResponseEntity.status(HttpStatus.OK).body(APIResponse.createGetResponse(response));
    }

    @ApiOperation(value = "guide 작성[권한 필요]")
    @PostMapping("/admin/guide")
    public ResponseEntity<APIResponse<GuideResDto>> postGuide(@RequestBody GuideReqDto request, BindingResult bindingResult) {
        GuideResDto response = service.post(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(APIResponse.createPostResponse(response));
    }

    @ApiOperation(value = "guide 수정[권한 필요]")
    @PutMapping("/admin/guide")
    public ResponseEntity<APIResponse<GuideResDto>> putGuide(@RequestBody GuideModifyReqDto request, BindingResult bindingResult) {
        GuideResDto response = service.put(request);
        return ResponseEntity.status(HttpStatus.OK).body(APIResponse.createPutResponse(response));
    }

    @ApiOperation(value = "guide 상세 조회[권한 필요]")
    @GetMapping("/guide/detail")
    public ResponseEntity<APIResponse<GuideDetailResDto>> getGuideDetail(@RequestParam("guide-id") Long id) {
        GuideDetailResDto response = service.getDetail(id);
        return ResponseEntity.status(HttpStatus.OK).body(APIResponse.createGetResponse(response));
    }

    @ApiOperation(value = "guide 삭제[권한 필요]")
    @DeleteMapping("/admin/guide")
    public ResponseEntity<APIResponse<Boolean>> deleteGuide(@RequestParam("guide-id") Long id) {
        boolean response = service.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body(APIResponse.createGetResponse(response));
    }
}
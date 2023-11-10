package com.server.oceankeeper.domain.notice.controller;

import com.server.oceankeeper.domain.notice.dto.request.NoticeModifyReqDto;
import com.server.oceankeeper.domain.notice.dto.request.NoticeReqDto;
import com.server.oceankeeper.domain.notice.dto.response.NoticeDetailResDto;
import com.server.oceankeeper.domain.notice.dto.response.NoticeListResDto;
import com.server.oceankeeper.domain.notice.dto.response.NoticeResDto;
import com.server.oceankeeper.domain.notice.service.NoticeService;
import com.server.oceankeeper.global.response.APIResponse;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class NoticeController {
    private final NoticeService service;

    @ApiOperation(value = "공지사항 리스트 조회[권한 필요]")
    @GetMapping("/notice")
    public ResponseEntity<APIResponse<NoticeListResDto>> get(@RequestParam(value = "notice-id", required = false) Long noticeId,
                                                             @RequestParam(value = "size", required = false) Integer size) {
        NoticeListResDto response = service.get(noticeId, size);
        return ResponseEntity.status(HttpStatus.OK).body(APIResponse.createGetResponse(response));
    }

    @ApiOperation(value = "공지사항 작성[권한 필요]")
    @PostMapping("/admin/notice")
    public ResponseEntity<APIResponse<NoticeResDto>> post(@RequestBody NoticeReqDto request, BindingResult bindingResult) {
        NoticeResDto response = service.post(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(APIResponse.createPostResponse(response));
    }

    @ApiOperation(value = "공지사항 수정[권한 필요]")
    @PutMapping("/admin/notice")
    public ResponseEntity<APIResponse<NoticeResDto>> put(@RequestBody NoticeModifyReqDto request, BindingResult bindingResult) {
        NoticeResDto response = service.put(request);
        return ResponseEntity.status(HttpStatus.OK).body(APIResponse.createPutResponse(response));
    }

    @ApiOperation(value = "공지사항 상세 조회[권한 필요]")
    @GetMapping("/notice/detail")
    public ResponseEntity<APIResponse<NoticeDetailResDto>> getDetail(@RequestParam("notice-id") Long noticeId) {
        NoticeDetailResDto response = service.getDetail(noticeId);
        return ResponseEntity.status(HttpStatus.OK).body(APIResponse.createGetResponse(response));
    }

    @ApiOperation(value = "공지사항 삭제[권한 필요]")
    @DeleteMapping("/admin/notice")
    public ResponseEntity<APIResponse<Boolean>> delete(@RequestParam("notice-id") Long noticeId) {
        boolean response = service.delete(noticeId);
        return ResponseEntity.status(HttpStatus.OK).body(APIResponse.createGetResponse(response));
    }
}
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
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class NoticeController {
    private final NoticeService service;

    @ApiOperation(value = "공지사항 리스트 조회[권한 필요]")
    @GetMapping("/admin/notice")
    public APIResponse<NoticeListResDto> getNotice(@RequestParam("notice-id") Long noticeId,
                                                   @RequestParam("size") Integer size) {
        NoticeListResDto response = service.getNotice(noticeId, size);
        return APIResponse.createGetResponse(response);
    }

    @ApiOperation(value = "공지사항 작성[권한 필요]")
    @PostMapping("/admin/notice")
    public APIResponse<NoticeResDto> postNotice(@RequestBody NoticeReqDto request) {
        NoticeResDto response = service.postNotice(request);
        return APIResponse.createPostResponse(response);
    }

    @ApiOperation(value = "공지사항 수정[권한 필요]")
    @PutMapping("/admin/notice")
    public APIResponse<NoticeResDto> putNotice(@RequestBody NoticeModifyReqDto request) {
        NoticeResDto response = service.putNotice(request);
        return APIResponse.createPostResponse(response);
    }

    @ApiOperation(value = "공지사항 상세 조회[권한 필요]")
    @GetMapping("/notice/detail")
    public APIResponse<NoticeDetailResDto> getNoticeDetail(@RequestParam("notice-id") Long noticeId) {
        NoticeDetailResDto response = service.getNoticeDetail(noticeId);
        return APIResponse.createGetResponse(response);
    }
}
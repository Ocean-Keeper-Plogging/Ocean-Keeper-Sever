package com.server.oceankeeper.domain.terms.controller;

import com.server.oceankeeper.domain.terms.dto.response.TermsDetailResDto;
import com.server.oceankeeper.domain.terms.dto.response.TermsResDto;
import com.server.oceankeeper.domain.terms.service.TermsService;
import com.server.oceankeeper.global.response.APIResponse;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class TermsController {
    private final TermsService service;

    @ApiOperation(value = "이용약관 작성[권한 필요]")
    @PostMapping("/admin/terms")
    public APIResponse<TermsResDto> postTerms(@RequestBody String contents, BindingResult bindingResult) {
        TermsResDto response = service.post(contents);
        return APIResponse.createPostResponse(response);
    }

    @ApiOperation(value = "이용약관 조회[권한 필요]")
    @GetMapping("/terms")
    public APIResponse<TermsDetailResDto> getTerms() {
        TermsDetailResDto response = service.get();
        return APIResponse.createGetResponse(response);
    }

    @ApiOperation(value = "이용약관 삭제[권한 필요]")
    @DeleteMapping("/admin/terms")
    public APIResponse<Boolean> deleteTerms(@RequestParam("id") Long id) {
        boolean response = service.delete(id);
        return APIResponse.createGetResponse(response);
    }
}
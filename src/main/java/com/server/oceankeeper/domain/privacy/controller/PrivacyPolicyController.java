package com.server.oceankeeper.domain.privacy.controller;

import com.server.oceankeeper.domain.privacy.dto.response.PrivacyPolicyDetailResDto;
import com.server.oceankeeper.domain.privacy.dto.response.PrivacyPolicyResDto;
import com.server.oceankeeper.domain.privacy.service.PrivacyPolicyService;
import com.server.oceankeeper.global.response.APIResponse;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class PrivacyPolicyController {
    private final PrivacyPolicyService service;

    @ApiOperation(value = "PrivacyPolicy 작성[권한 필요]")
    @PostMapping("/admin/privacy-policy")
    public APIResponse<PrivacyPolicyResDto> postPrivacyPolicy(@RequestBody String contents, BindingResult bindingResult) {
        PrivacyPolicyResDto response = service.post(contents);
        return APIResponse.createPostResponse(response);
    }

    @ApiOperation(value = "PrivacyPolicy 조회[권한 필요]")
    @GetMapping("/privacy-policy")
    public APIResponse<PrivacyPolicyDetailResDto> getPrivacyPolicy() {
        PrivacyPolicyDetailResDto response = service.get();
        return APIResponse.createGetResponse(response);
    }

    @ApiOperation(value = "PrivacyPolicy 삭제[권한 필요]")
    @DeleteMapping("/admin/privacy-policy")
    public APIResponse<Boolean> deletePrivacyPolicy(@RequestParam("id") Long id) {
        boolean response = service.delete(id);
        return APIResponse.createGetResponse(response);
    }
}
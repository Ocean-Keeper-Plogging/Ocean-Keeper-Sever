package com.server.oceankeeper.domain.privacy.controller;

import com.server.oceankeeper.domain.privacy.dto.request.PrivacyPolicyReqDto;
import com.server.oceankeeper.domain.privacy.dto.response.PrivacyPolicyDetailResDto;
import com.server.oceankeeper.domain.privacy.dto.response.PrivacyPolicyResDto;
import com.server.oceankeeper.domain.privacy.service.PrivacyPolicyService;
import com.server.oceankeeper.global.response.APIResponse;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class PrivacyPolicyController {
    private final PrivacyPolicyService service;

    @ApiOperation(value = "PrivacyPolicy 작성[권한 필요]")
    @PostMapping("/admin/privacy-policy")
    public ResponseEntity<APIResponse<PrivacyPolicyResDto>> postPrivacyPolicy(@RequestBody PrivacyPolicyReqDto contents, BindingResult bindingResult) {
        PrivacyPolicyResDto response = service.post(contents);
        return ResponseEntity.status(HttpStatus.CREATED).body(APIResponse.createPostResponse(response));
    }

    @ApiOperation(value = "PrivacyPolicy 조회[권한 필요]")
    @GetMapping("/privacy-policy")
    public ResponseEntity<APIResponse<PrivacyPolicyDetailResDto>> getPrivacyPolicy() {
        PrivacyPolicyDetailResDto response = service.get();
        return ResponseEntity.status(HttpStatus.OK).body(APIResponse.createGetResponse(response));
    }

    @ApiOperation(value = "PrivacyPolicy 삭제[권한 필요]")
    @DeleteMapping("/admin/privacy-policy")
    public ResponseEntity<APIResponse<Boolean>> deletePrivacyPolicy(@RequestParam("id") Long id) {
        boolean response = service.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body(APIResponse.createDeleteResponse(response));
    }
}
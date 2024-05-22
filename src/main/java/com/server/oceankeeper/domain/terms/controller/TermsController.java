package com.server.oceankeeper.domain.terms.controller;

import com.server.oceankeeper.domain.terms.dto.request.TermsReqDto;
import com.server.oceankeeper.domain.terms.dto.response.TermsDetailResDto;
import com.server.oceankeeper.domain.terms.dto.response.TermsResDto;
import com.server.oceankeeper.domain.terms.service.TermsService;
import com.server.oceankeeper.global.response.APIResponse;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class TermsController {
    private final TermsService service;

    @ApiOperation(value = "이용약관 작성[권한 필요]")
    @PostMapping(value = "/admin/terms",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<APIResponse<TermsResDto>> postTerms(@RequestBody TermsReqDto request, BindingResult bindingResult) {
        TermsResDto response = service.post(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(APIResponse.createPostResponse(response));
    }

    @ApiOperation(value = "이용약관 조회[권한 필요]")
    @GetMapping("/terms")
    public ResponseEntity<APIResponse<TermsDetailResDto>> getTerms() {
        TermsDetailResDto response = service.get();
        return  ResponseEntity.status(HttpStatus.OK).body(APIResponse.createGetResponse(response));
    }

    @ApiOperation(value = "이용약관 삭제[권한 필요]")
    @DeleteMapping("/admin/terms")
    public ResponseEntity<APIResponse<Boolean>> deleteTerms(@RequestParam("id") Long id) {
        boolean response = service.delete(id);
        return  ResponseEntity.status(HttpStatus.OK).body(APIResponse.createDeleteResponse(response));
    }
}
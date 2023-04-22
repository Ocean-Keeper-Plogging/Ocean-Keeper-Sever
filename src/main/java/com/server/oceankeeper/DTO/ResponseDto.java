package com.server.oceankeeper.DTO;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import com.server.oceankeeper.DTO.User.*;


@ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK", response = UserReqDto.class),
        @ApiResponse(code = 404, message = "Not Found", response = UserReqDto.class)
})
@RequiredArgsConstructor
@Getter
public class ResponseDto <T>{
    private final Integer code;
    private final String msg;

    private final T data;
}

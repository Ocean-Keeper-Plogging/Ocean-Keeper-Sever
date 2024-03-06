package com.server.oceankeeper.global.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.server.oceankeeper.global.response.APIResponse;
import com.server.oceankeeper.global.response.ErrorCode;
import com.server.oceankeeper.global.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Slf4j
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException)
            throws IOException {
        log.info("accessDeniedException {} error", accessDeniedException.getMessage());
        response.sendError(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("Utf-8");
        response.getWriter().write(new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .writeValueAsString(APIResponse.createErrResponse(HttpStatus.FORBIDDEN,
                        new ErrorResponse(HttpStatus.FORBIDDEN.getReasonPhrase(),"허용되지 않은 요청", ErrorCode.FORBIDDEN_ERROR))));
    }
}

package com.server.oceankeeper.global.jwt;

import com.server.oceankeeper.domain.user.service.TokenProvider;
import com.server.oceankeeper.global.exception.ExpiredTokenException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    private final TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException, ExpiredTokenException {
        String jwt = tokenProvider.resolveToken(request);

        try {
            tokenProvider.validateToken(jwt);
        }catch (ExpiredTokenException e){
            log.debug("토큰 만료 에러");
        }

        filterChain.doFilter(request, response);
    }

}

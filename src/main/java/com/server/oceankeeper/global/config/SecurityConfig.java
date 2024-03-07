package com.server.oceankeeper.global.config;

import com.server.oceankeeper.domain.user.entitiy.UserRole;
import com.server.oceankeeper.domain.user.service.TokenProvider;
import com.server.oceankeeper.global.jwt.JwtAuthorizationFilter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final TokenProvider tokenProvider;
    private final AuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final AccessDeniedHandler jwtAccessDeniedHandler;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    private final Logger log = LoggerFactory.getLogger(getClass());
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .antMatchers("/h2-console/**", "/favicon.ico");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        log.debug("디버그 : filterChain 빈 등록됨");

        http.headers().frameOptions().disable();
        http.csrf().disable();
        http.cors().configurationSource(configurationSource());

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.formLogin().disable();
        http.httpBasic().disable();

        http.apply(new JwtSecurityConfig(tokenProvider));
        http.exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler);

        http.authorizeRequests()
                //iamge
                //.antMatchers(HttpMethod.POST, "/image/edit/**").authenticated()
                .antMatchers(HttpMethod.POST, "/image/profile**").permitAll()
                .antMatchers(HttpMethod.POST, "/image/keeper**").permitAll()
                .antMatchers(HttpMethod.POST, "/image/thumbnail**").permitAll()
                .antMatchers(HttpMethod.POST, "/image/story**").permitAll()
                //auth
                .antMatchers(HttpMethod.POST,"/auth/signup").permitAll()
                .antMatchers(HttpMethod.POST,"/auth/login").permitAll()
                .antMatchers(HttpMethod.POST,"/admin/login").permitAll() //TODO: 다른유저는 이방식으로 로그인못하도록 수정필요
                .antMatchers(HttpMethod.POST,"/auth/reissue").permitAll()
                .antMatchers(HttpMethod.GET,"/auth/**").permitAll()
                .antMatchers(HttpMethod.GET,"/privacy-policy").permitAll()
                .antMatchers(HttpMethod.GET,"/terms").permitAll()
                .antMatchers("/admin/**").hasRole("" + UserRole.ADMIN)
                //swagger
                //TODO - prod단계에서 지워야함
                .antMatchers("/swagger-ui.html").permitAll()
                .antMatchers("/v2/api-docs/**", "/swagger-ui/**", "/swagger-resources/**", "/webjars/**", "/swagger/**").permitAll()
                .anyRequest().authenticated();

        return http.build();
    }
    @RequiredArgsConstructor
    public class JwtSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {
        private final TokenProvider tokenProvider;

        // TokenProvider 를 주입받아서 JwtFilter 를 통해 Security 로직에 필터를 등록
        @Override
        public void configure(HttpSecurity http) {
            JwtAuthorizationFilter customFilter = new JwtAuthorizationFilter(tokenProvider);
            http.addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class);
        }
    }

    public CorsConfigurationSource configurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*"); // GET, POST, PUT, DELETE (Javascript 요청 허용)
        configuration.addAllowedOriginPattern("*"); // 모든 IP 주소 허용
        configuration.setAllowCredentials(true); // 클라이언트에서 쿠키 요청 허용
        configuration.addExposedHeader("Authorization");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}

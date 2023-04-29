package com.server.oceankeeper.Global.Config;

import com.server.oceankeeper.Global.Jwt.JwtAuthenticationFilter;
import com.server.oceankeeper.Global.Jwt.JwtAuthorizationFilter;
import com.server.oceankeeper.Domain.User.UserEnum.UserRole;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


@Configuration
@AllArgsConstructor
public class SecurityConfig {

    @Bean // Ioc 컨테이너에 BCryptPasswordEncoder() 객체가 등록됨.
    public BCryptPasswordEncoder passwordEncoder() {
        log.debug("디버그 : BCryptPasswordEncoder 빈 등록됨");
        return new BCryptPasswordEncoder();
    }

    private final Logger log = LoggerFactory.getLogger(getClass()); //@slf4j 대신에 사용한다.
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        log.debug("디버그 : filterChain 빈 등록됨");
        // iframe 허용안함.
        http.headers().frameOptions().disable();
        http.csrf().disable();
        http.cors().configurationSource(configurationSource());

        //jwt 사용 => 세션 사용안함
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        // rest api 형태로 만들 것이기 때문에 form login은 필요 없다. => from login 비활성화
        http.formLogin().disable();
        // httpBasic은 브라우저가 팝업창을 이용해서 사용자 인증을 진행한다. => 비활성화
        http.httpBasic().disable();


        //jwt 필터 적용
        http.apply(new CustomSecurityFilterManager());

        http.authorizeRequests()
                //auth로 시작하는 경우 검증이 필요합니다.
                .antMatchers("/auth/**").authenticated()
                .antMatchers("/api/admin/**").hasRole("" + UserRole.ADMIN)
                //swagger 적용
                //todo - prod단계에서 지워야함
                .antMatchers("/swagger-ui.html").permitAll()
                .antMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-resources/**").permitAll()
                .anyRequest().permitAll();


        return http.build();
    }
    //jwt 필터 등록
    public class CustomSecurityFilterManager extends AbstractHttpConfigurer<CustomSecurityFilterManager, HttpSecurity>{
        @Override
        public void configure(HttpSecurity builder) throws Exception {
            AuthenticationManager authManager = builder.getSharedObject(AuthenticationManager.class);
            builder.addFilter(new JwtAuthenticationFilter(authManager));
            builder.addFilter(new JwtAuthorizationFilter(authManager));
            super.configure(builder);
        }
    }


    public CorsConfigurationSource configurationSource() {
        log.debug("디버그 : configurationSource cors 설정이 SecurityFilterChain에 등록됨");
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

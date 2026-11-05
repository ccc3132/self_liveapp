package egovframework.egovwebpractice.api.config;

import egovframework.egovwebpractice.api.jwt.JwtAuthenticationFilter;
import egovframework.egovwebpractice.api.jwt.JwtTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * 모바일 앱(React Native 등)용 REST API(/api/**) 전용 보안 체인.
 * 기존 웹(JSP) 화면은 egovframe-rte-fdl-security 가 만드는 세션/CSRF 기반 체인을
 * 그대로 쓰고({@link egovframework.egovwebpractice.config.SecurityConfig}), 이 체인은
 * /api/** 요청만 가로채 JWT 로 인증한다(세션 없음, CSRF 없음).
 *
 * <p>egov 체인은 {@code securityMatcher} 가 없는 catch-all 체인이라 기본 순서(가장 낮은 우선순위)로
 * 평가되므로, 이 체인에 명시적으로 {@code @Order(1)} 을 줘서 /api/** 요청을 먼저 가로채게 한다.</p>
 */
@Configuration
public class ApiSecurityConfig {

    @Bean
    @Order(1)
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http, JwtTokenProvider tokenProvider)
            throws Exception {
        http.securityMatcher("/api/**")
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/login").permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(new JwtAuthenticationFilter(tokenProvider), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}

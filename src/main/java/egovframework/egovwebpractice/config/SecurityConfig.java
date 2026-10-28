package egovframework.egovwebpractice.config;

import egovframework.com.uat.uia.filter.EgovSpringSecurityLoginFilter;
import egovframework.com.uat.uia.filter.EgovSpringSecurityLogoutFilter;
import org.egovframe.rte.fdl.security.config.EgovSecurityConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.session.HttpSessionEventPublisher;

/**
 * 표준프레임워크 보안(Spring Security) 설정.
 * 필터 체인(로그인·로그아웃·CSRF·권한)은 실행환경의 {@link EgovSecurityConfiguration} 이 만들고,
 * 설정값은 egovframework/egovProps/conf/egov-security-config.properties 에서 읽는다.
 * 원본(EgovWebApplicationInitializer)이 서블릿에 직접 등록하던 필터를 Spring Boot 방식으로 등록한다.
 */
@Configuration
@EnableWebSecurity
@Import(EgovSecurityConfiguration.class)
public class SecurityConfig {

    /**
     * 브리지 필터의 순서. springSecurityFilterChain(-100)보다 먼저, 그리고 요청 정보를 스레드에 묶는
     * RequestContextFilter(-105)보다는 나중에 실행되어야 한다(클라이언트 IP 확인에 RequestContextHolder 를 쓴다).
     */
    private static final int BEFORE_SECURITY_FILTER_CHAIN = -102;

    /** 로그인 폼(id/password/userSe)을 Spring Security 파라미터(username)로 바꿔 주는 브리지 */
    @Bean
    public FilterRegistrationBean<EgovSpringSecurityLoginFilter> egovLoginBridgeFilterRegistration(
            EgovSpringSecurityLoginFilter filter) {
        FilterRegistrationBean<EgovSpringSecurityLoginFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setOrder(BEFORE_SECURITY_FILTER_CHAIN);
        return registration;
    }

    /** /uat/uia/actionLogout.do 를 로그아웃 처리로 연결하는 브리지 */
    @Bean
    public FilterRegistrationBean<EgovSpringSecurityLogoutFilter> egovLogoutBridgeFilterRegistration(
            EgovSpringSecurityLogoutFilter filter) {
        FilterRegistrationBean<EgovSpringSecurityLogoutFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setOrder(BEFORE_SECURITY_FILTER_CHAIN + 1);
        return registration;
    }

    /** 동시 세션 제한(concurrentMaxSessons)에 필요한 세션 이벤트 전달 */
    @Bean
    public ServletListenerRegistrationBean<HttpSessionEventPublisher> httpSessionEventPublisher() {
        return new ServletListenerRegistrationBean<>(new HttpSessionEventPublisher());
    }
}

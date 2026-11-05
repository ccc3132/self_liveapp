package egovframework.egovwebpractice.config;

import egovframework.egovwebpractice.common.service.impl.ActiveUserTracker;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** 접속 중인 사용자 추적용 세션 리스너를 서블릿 컨테이너에 등록한다. */
@Configuration
public class SessionTrackingConfig {

    @Bean
    public ServletListenerRegistrationBean<ActiveSessionListener> activeSessionListener(ActiveUserTracker tracker) {
        return new ServletListenerRegistrationBean<>(new ActiveSessionListener(tracker));
    }
}

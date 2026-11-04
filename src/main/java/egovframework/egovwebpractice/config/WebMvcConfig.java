package egovframework.egovwebpractice.config;

import egovframework.com.cmm.resolver.EgovSecurityArgumentResolver;
import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 암호화된 파라미터를 {@code EgovSecurityMap} 으로 바로 받는 컨트롤러 핸들러를 위한 ArgumentResolver 등록.
 * 원본은 서블릿 설정(XML)의 argument-resolvers 로 등록하던 부분이다.
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new EgovSecurityArgumentResolver());
    }
}

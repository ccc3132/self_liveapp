package egovframework.egovwebpractice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

// egovframework.com: 표준프레임워크 공통컴포넌트(소스를 가져와 사용)
@SpringBootApplication(scanBasePackages = {"egovframework.egovwebpractice", "egovframework.com"})
public class EgovWebPracticeApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(EgovWebPracticeApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(EgovWebPracticeApplication.class, args);
    }
}

package egovframework.egovwebpractice.web;

import egovframework.com.cmm.web.EgovBindingInitializer;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

/**
 * 모든 컨트롤러의 요청 값 바인딩에 공통컴포넌트의 EgovBindingInitializer 를 적용한다
 * (문자열 앞뒤 공백 제거, 날짜 형식, 암호화된 ID 복호화). 원본은 서블릿 설정(XML)에서 지정하던 부분이다.
 */
@ControllerAdvice
public class BindingInitializerAdvice {

    private final EgovBindingInitializer initializer = new EgovBindingInitializer();

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        initializer.initBinder(binder);
    }
}

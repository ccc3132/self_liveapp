package egovframework.egovwebpractice.web;

import egovframework.com.cmm.exception.EgovAccessDeniedException;
import egovframework.com.cmm.exception.EgovLoginRequiredException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 업무 코드(@RequireAdmin, EgovAuthorizationHelper)가 던지는 권한 예외를 화면으로 연결한다.
 * 원본은 서블릿 설정(XML)의 예외 리졸버가 처리하던 부분이다.
 * - 로그인이 필요한 경우: 로그인 화면으로 이동
 * - 권한이 없는 경우: 403 + 권한 없음 화면
 */
@ControllerAdvice
public class SecurityExceptionAdvice {

    @ExceptionHandler(EgovLoginRequiredException.class)
    public String loginRequired() {
        return "redirect:/uat/uia/egovLoginUsr.do";
    }

    @ExceptionHandler(EgovAccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String accessDenied() {
        return "egovframework/com/cmm/error/accessDenied";
    }
}

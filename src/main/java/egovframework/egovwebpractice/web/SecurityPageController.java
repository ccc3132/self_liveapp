package egovframework.egovwebpractice.web;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Spring Security 가 권한 없음/CSRF 토큰 오류 때 이동시키는 안내 화면.
 * 공통컴포넌트(EgovAuthorManageController, EgovComIndexController)의 같은 이름 화면을 그대로 쓴다.
 */
@Controller
public class SecurityPageController {

    @GetMapping("/sec/ram/accessDenied.do")
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String accessDenied() {
        return "egovframework/com/cmm/error/accessDenied";
    }

    @RequestMapping("/egovCSRFAccessDenied.do")
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String csrfAccessDenied() {
        return "egovframework/com/cmm/error/csrfAccessDenied";
    }
}

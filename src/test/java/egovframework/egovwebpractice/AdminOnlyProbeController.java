package egovframework.egovwebpractice;

import egovframework.com.cmm.annotation.RequireAdmin;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/** 테스트 전용: @RequireAdmin 이 실제로 강제되는지 확인하기 위한 관리자 전용 엔드포인트 */
@Controller
public class AdminOnlyProbeController {

    @RequireAdmin
    @GetMapping("/test/admin-only.do")
    @ResponseBody
    public String adminOnly() {
        return "admin-ok";
    }
}

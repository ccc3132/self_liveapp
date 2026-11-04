package egovframework.egovwebpractice.web;

import egovframework.com.cmm.LoginVO;
import egovframework.com.cmm.util.EgovUserDetailsHelper;
import egovframework.egovwebpractice.common.service.DashboardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/** 표준프레임워크 MVC 규칙: DAO 를 직접 호출하지 않고 서비스 인터페이스를 통해서만 처리한다. */
@Controller
public class MainController {

    private final DashboardService dashboardService;

    public MainController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/")
    public String root() {
        return "redirect:/main.do";
    }

    @GetMapping("/main.do")
    public String main(Model model) throws Exception {
        LoginVO loginVO = (LoginVO) EgovUserDetailsHelper.getAuthenticatedUser();
        String uniqId = loginVO == null ? "" : loginVO.getUniqId();
        model.addAttribute("loginVO", loginVO);
        model.addAttribute("dashboard", dashboardService.selectDashboard(uniqId));
        return "main";
    }
}

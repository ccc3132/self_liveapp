package egovframework.egovwebpractice.web;

import egovframework.com.cmm.LoginVO;
import egovframework.com.cmm.util.EgovUserDetailsHelper;
import egovframework.egovwebpractice.common.service.ActiveUserVO;
import egovframework.egovwebpractice.common.service.DashboardService;
import egovframework.egovwebpractice.common.service.impl.ActiveUserTracker;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/** 표준프레임워크 MVC 규칙: DAO 를 직접 호출하지 않고 서비스 인터페이스를 통해서만 처리한다. */
@Controller
public class MainController {

    private final DashboardService dashboardService;
    private final ActiveUserTracker activeUserTracker;

    public MainController(DashboardService dashboardService, ActiveUserTracker activeUserTracker) {
        this.dashboardService = dashboardService;
        this.activeUserTracker = activeUserTracker;
    }

    /** 로그인 후 랜딩 화면: 사이드바+헤더(틀)와 그 안의 iframe 으로 /main.do 를 띄운다. */
    @GetMapping("/")
    public String root() {
        return "redirect:/shell.do";
    }

    @GetMapping("/shell.do")
    public String shell(Model model) {
        model.addAttribute("loginVO", EgovUserDetailsHelper.getAuthenticatedUser());
        return "shell";
    }

    @GetMapping("/main.do")
    public String main(Model model) throws Exception {
        LoginVO loginVO = (LoginVO) EgovUserDetailsHelper.getAuthenticatedUser();
        String uniqId = loginVO == null ? "" : loginVO.getUniqId();
        model.addAttribute("loginVO", loginVO);
        model.addAttribute("dashboard", dashboardService.selectDashboard(uniqId));
        return "main";
    }

    /** 대시보드의 "접속 중인 사용자"를 주기적으로 새로고침하기 위한 JSON. */
    @GetMapping("/main/activeUsers.do")
    @ResponseBody
    public Map<String, Object> activeUsers() {
        List<ActiveUserVO> users = activeUserTracker.selectActiveUsers();
        return Map.of("count", users.size(), "users", users);
    }
}

package egovframework.egovwebpractice.api.web;

import egovframework.com.cmm.LoginVO;
import egovframework.egovwebpractice.common.service.DashboardService;
import egovframework.egovwebpractice.common.service.DashboardVO;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/** 모바일 앱 대시보드 화면용 API. 웹 대시보드(/main.do)와 같은 {@link DashboardService} 를 그대로 재사용한다. */
@RestController
public class ApiDashboardController {

    private final DashboardService dashboardService;

    public ApiDashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/api/dashboard")
    public DashboardVO dashboard(@AuthenticationPrincipal LoginVO user) throws Exception {
        return dashboardService.selectDashboard(user.getUniqId());
    }
}

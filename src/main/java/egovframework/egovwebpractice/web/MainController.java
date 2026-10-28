package egovframework.egovwebpractice.web;

import egovframework.egovwebpractice.common.service.SystemCheckService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/** 표준프레임워크 MVC 규칙: DAO 를 직접 호출하지 않고 서비스 인터페이스를 통해서만 처리한다. */
@Controller
public class MainController {

    private final SystemCheckService systemCheckService;

    public MainController(SystemCheckService systemCheckService) {
        this.systemCheckService = systemCheckService;
    }

    @GetMapping("/")
    public String root() {
        return "redirect:/main.do";
    }

    @GetMapping("/main.do")
    public String main(Model model) throws Exception {
        model.addAttribute("check", systemCheckService.selectSystemCheck());
        return "main";
    }
}

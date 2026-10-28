package egovframework.egovwebpractice.web;

import egovframework.egovwebpractice.common.service.SystemCheckService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MainController {

    private final SystemCheckService systemCheckService;

    public MainController(SystemCheckService systemCheckService) {
        this.systemCheckService = systemCheckService;
    }

    @RequestMapping("/")
    public String root() {
        return "redirect:/main.do";
    }

    @RequestMapping("/main.do")
    public String main(Model model) {
        model.addAttribute("check", systemCheckService.getSystemCheck());
        return "main";
    }
}

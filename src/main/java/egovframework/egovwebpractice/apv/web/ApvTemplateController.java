package egovframework.egovwebpractice.apv.web;

import egovframework.com.cmm.LoginVO;
import egovframework.com.cmm.annotation.RequireAdmin;
import egovframework.com.cmm.util.EgovUserDetailsHelper;
import egovframework.egovwebpractice.apv.service.ApvTemplateService;
import egovframework.egovwebpractice.apv.service.ApvTemplateVO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/** 결재 양식 템플릿 관리(관리자 전용, 직접 개발 - 공통컴포넌트에 없는 모듈). */
@Controller
public class ApvTemplateController {

    private final ApvTemplateService apvTemplateService;

    public ApvTemplateController(ApvTemplateService apvTemplateService) {
        this.apvTemplateService = apvTemplateService;
    }

    private LoginVO currentUser() {
        return (LoginVO) EgovUserDetailsHelper.getAuthenticatedUser();
    }

    @GetMapping("/apv/admin/EgovApvTemplateList.do")
    @RequireAdmin
    public String list(Model model) throws Exception {
        model.addAttribute("templateList", apvTemplateService.selectTemplateList());
        return "egovframework/egovwebpractice/apv/EgovApvTemplateList";
    }

    @GetMapping("/apv/admin/EgovApvTemplateBuilder.do")
    @RequireAdmin
    public String builder(@RequestParam(value = "templateId", required = false) Long templateId, Model model)
            throws Exception {
        if (templateId != null) {
            model.addAttribute("template", apvTemplateService.selectTemplate(templateId));
        }
        return "egovframework/egovwebpractice/apv/EgovApvTemplateBuilder";
    }

    @PostMapping("/apv/admin/insertApvTemplate.do")
    @RequireAdmin
    public String insert(@ModelAttribute ApvTemplateVO vo) throws Exception {
        vo.setFrstRegisterId(currentUser().getUniqId());
        normalizeFileAtchPosblAt(vo);
        apvTemplateService.insertTemplate(vo);
        return "redirect:/apv/admin/EgovApvTemplateList.do";
    }

    @PostMapping("/apv/admin/updateApvTemplate.do")
    @RequireAdmin
    public String update(@ModelAttribute ApvTemplateVO vo) throws Exception {
        vo.setLastUpdusrId(currentUser().getUniqId());
        normalizeFileAtchPosblAt(vo);
        apvTemplateService.updateTemplate(vo);
        return "redirect:/apv/admin/EgovApvTemplateList.do";
    }

    /** 체크박스는 체크 해제 시 파라미터 자체가 전송되지 않으므로 null이면 'N'으로 보정한다. */
    private void normalizeFileAtchPosblAt(ApvTemplateVO vo) {
        if (vo.getFileAtchPosblAt() == null || vo.getFileAtchPosblAt().isBlank()) {
            vo.setFileAtchPosblAt("N");
        }
    }

    @PostMapping("/apv/admin/toggleApvTemplateUseAt.do")
    @RequireAdmin
    public String toggleUseAt(@RequestParam("templateId") Long templateId, @RequestParam("useAt") String useAt)
            throws Exception {
        apvTemplateService.updateTemplateUseAt(templateId, useAt);
        return "redirect:/apv/admin/EgovApvTemplateList.do";
    }

    @PostMapping("/apv/admin/deleteApvTemplate.do")
    @RequireAdmin
    public String delete(@RequestParam("templateId") Long templateId) throws Exception {
        apvTemplateService.deleteTemplate(templateId);
        return "redirect:/apv/admin/EgovApvTemplateList.do";
    }
}

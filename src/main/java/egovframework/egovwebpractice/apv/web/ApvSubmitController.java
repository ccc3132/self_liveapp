package egovframework.egovwebpractice.apv.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import egovframework.com.cmm.LoginVO;
import egovframework.com.cmm.util.EgovUserDetailsHelper;
import egovframework.egovwebpractice.apv.service.ApvSubmissionService;
import egovframework.egovwebpractice.apv.service.ApvTemplateService;
import egovframework.egovwebpractice.apv.service.ApvTemplateVO;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

/** 결재 신청(상신, 직접 개발 - 공통컴포넌트에 없는 모듈). */
@Controller
public class ApvSubmitController {

    private final ApvTemplateService apvTemplateService;
    private final ApvSubmissionService apvSubmissionService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ApvSubmitController(ApvTemplateService apvTemplateService, ApvSubmissionService apvSubmissionService) {
        this.apvTemplateService = apvTemplateService;
        this.apvSubmissionService = apvSubmissionService;
    }

    private LoginVO currentUser() {
        return (LoginVO) EgovUserDetailsHelper.getAuthenticatedUser();
    }

    @GetMapping("/apv/EgovApvSubmitSelect.do")
    public String select(Model model) throws Exception {
        model.addAttribute("templateList", apvTemplateService.selectActiveTemplateList());
        return "egovframework/egovwebpractice/apv/EgovApvSubmitSelect";
    }

    @SuppressWarnings("unchecked")
    @GetMapping("/apv/EgovApvSubmitForm.do")
    public String form(@RequestParam("templateId") Long templateId, Model model) throws Exception {
        ApvTemplateVO template = apvTemplateService.selectTemplate(templateId);
        List<Map<String, Object>> fields = objectMapper.readValue(template.getFieldSchema(), List.class);
        model.addAttribute("template", template);
        model.addAttribute("fields", fields);
        return "egovframework/egovwebpractice/apv/EgovApvSubmitForm";
    }

    @GetMapping("/apv/EgovApvApproverPopup.do")
    public String approverPopup(@RequestParam(value = "keyword", required = false) String keyword, Model model)
            throws Exception {
        model.addAttribute("employeeList", apvSubmissionService.selectEmployeeCandidates(keyword));
        model.addAttribute("keyword", keyword == null ? "" : keyword);
        return "egovframework/egovwebpractice/apv/EgovApvApproverPopup";
    }

    @PostMapping("/apv/insertApvSubmission.do")
    public String insert(MultipartHttpServletRequest multiRequest, @RequestParam("templateId") Long templateId,
            @RequestParam("fieldValuesJson") String fieldValuesJson,
            @RequestParam(value = "approverIds", required = false) String approverIdsJoined,
            @RequestParam(value = "approverNms", required = false) String approverNmsJoined) throws Exception {
        LoginVO user = currentUser();
        ApvTemplateVO template = apvTemplateService.selectTemplate(templateId);
        List<String> approverIds = splitJoined(approverIdsJoined);
        List<String> approverNms = splitJoined(approverNmsJoined);
        List<MultipartFile> files = multiRequest.getFiles("file_1");

        apvSubmissionService.submit(template, fieldValuesJson, user.getUniqId(), user.getName(), approverIds,
                approverNms, files);
        return "redirect:/apv/EgovApvSubmitSelect.do";
    }

    private List<String> splitJoined(String joined) {
        if (joined == null || joined.isBlank()) {
            return new ArrayList<>();
        }
        return Arrays.asList(joined.split(";"));
    }
}

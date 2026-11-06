package egovframework.egovwebpractice.apv.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import egovframework.com.cmm.LoginVO;
import egovframework.com.cmm.util.EgovUserDetailsHelper;
import egovframework.egovwebpractice.apv.service.ApvInboxService;
import egovframework.egovwebpractice.apv.service.ApvLineVO;
import egovframework.egovwebpractice.apv.service.ApvSubmissionService;
import egovframework.egovwebpractice.apv.service.ApvSubmissionVO;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/** 결재함: 내가 결재할 문서 / 내가 상신한 문서, 상세+승인/반려(직접 개발 - 공통컴포넌트에 없는 모듈). */
@Controller
public class ApvInboxController {

    private final ApvInboxService apvInboxService;
    private final ApvSubmissionService apvSubmissionService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ApvInboxController(ApvInboxService apvInboxService, ApvSubmissionService apvSubmissionService) {
        this.apvInboxService = apvInboxService;
        this.apvSubmissionService = apvSubmissionService;
    }

    private LoginVO currentUser() {
        return (LoginVO) EgovUserDetailsHelper.getAuthenticatedUser();
    }

    @GetMapping("/apv/EgovApvInbox.do")
    public String inbox(Model model) throws Exception {
        LoginVO user = currentUser();
        model.addAttribute("pendingList", apvInboxService.selectPendingList(user.getUniqId()));
        model.addAttribute("mySubmissionList", apvSubmissionService.selectMySubmissionList(user.getUniqId()));
        return "egovframework/egovwebpractice/apv/EgovApvInbox";
    }

    @SuppressWarnings("unchecked")
    @GetMapping("/apv/selectApvSubmissionDetailAjax.do")
    @ResponseBody
    public Map<String, Object> detail(@RequestParam("submissionId") Long submissionId) throws Exception {
        LoginVO user = currentUser();
        ApvSubmissionVO submission = apvSubmissionService.selectSubmissionDetail(submissionId);

        Long actionableLineId = null;
        if (submission.getLines() != null) {
            for (ApvLineVO line : submission.getLines()) {
                boolean isCurrentStep = line.getStepOrder().equals(submission.getCurrentStepOrder());
                boolean isWaiting = "대기".equals(line.getStatus());
                boolean isMe = user.getUniqId().equals(line.getApproverId());
                if (isCurrentStep && isWaiting && isMe) {
                    actionableLineId = line.getLineId();
                }
            }
        }

        List<Map<String, Object>> schema = objectMapper.readValue(submission.getSchemaSnapshot(), List.class);
        Map<String, Object> values = objectMapper.readValue(submission.getFieldValues(), Map.class);
        List<Map<String, Object>> renderFields = new java.util.ArrayList<>();
        for (Map<String, Object> field : schema) {
            Map<String, Object> rendered = new LinkedHashMap<>();
            rendered.put("label", field.get("label"));
            rendered.put("type", field.get("type"));
            rendered.put("value", values.get(field.get("id")));
            renderFields.add(rendered);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("submission", submission);
        result.put("renderFields", renderFields);
        result.put("actionableLineId", actionableLineId);
        return result;
    }

    @PostMapping("/apv/approveApvSubmission.do")
    public String approve(@RequestParam("submissionId") Long submissionId, @RequestParam("lineId") Long lineId,
            @RequestParam("stepOrder") Integer stepOrder) throws Exception {
        apvInboxService.approve(submissionId, lineId, stepOrder, currentUser().getUniqId());
        return "redirect:/apv/EgovApvInbox.do";
    }

    @PostMapping("/apv/rejectApvSubmission.do")
    public String reject(@RequestParam("submissionId") Long submissionId, @RequestParam("lineId") Long lineId,
            @RequestParam(value = "comment", required = false) String comment) throws Exception {
        apvInboxService.reject(submissionId, lineId, currentUser().getUniqId(), comment);
        return "redirect:/apv/EgovApvInbox.do";
    }
}

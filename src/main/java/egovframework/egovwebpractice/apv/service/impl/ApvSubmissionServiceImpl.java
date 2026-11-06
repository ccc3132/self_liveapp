package egovframework.egovwebpractice.apv.service.impl;

import egovframework.com.cmm.service.EgovFileMngService;
import egovframework.com.cmm.service.EgovFileMngUtil;
import egovframework.com.cmm.service.FileVO;
import egovframework.egovwebpractice.apv.service.ApvLineVO;
import egovframework.egovwebpractice.apv.service.ApvSubmissionService;
import egovframework.egovwebpractice.apv.service.ApvSubmissionVO;
import egovframework.egovwebpractice.apv.service.ApvTemplateVO;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/** 표준프레임워크 서비스 규칙: EgovAbstractServiceImpl 을 상속하고 서비스 인터페이스를 구현한다. */
@Service("apvSubmissionService")
@Transactional(readOnly = true)
public class ApvSubmissionServiceImpl extends EgovAbstractServiceImpl implements ApvSubmissionService {

    private static final DateTimeFormatter TITLE_DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final ApvSubmissionDAO apvSubmissionDAO;
    private final ApvLineDAO apvLineDAO;
    private final EgovFileMngUtil fileUtil;
    private final EgovFileMngService fileMngService;

    public ApvSubmissionServiceImpl(ApvSubmissionDAO apvSubmissionDAO, ApvLineDAO apvLineDAO,
            EgovFileMngUtil fileUtil, EgovFileMngService fileMngService) {
        this.apvSubmissionDAO = apvSubmissionDAO;
        this.apvLineDAO = apvLineDAO;
        this.fileUtil = fileUtil;
        this.fileMngService = fileMngService;
    }

    @Override
    @Transactional
    public void submit(ApvTemplateVO template, String fieldValuesJson, String requesterId, String requesterNm,
            List<String> approverIds, List<String> approverNms, List<MultipartFile> files) throws Exception {
        if (approverIds == null || approverIds.isEmpty()) {
            throw new IllegalArgumentException("결재자를 한 명 이상 지정해야 합니다.");
        }

        String atchFileId = "";
        if (files != null && !files.isEmpty()) {
            List<FileVO> parsed = fileUtil.parseFileInf(files, "APV_", 0, "", "");
            atchFileId = fileMngService.insertFileInfs(parsed);
        }

        ApvSubmissionVO submission = new ApvSubmissionVO();
        submission.setTemplateId(template.getTemplateId());
        submission.setTemplateNm(template.getTemplateNm());
        submission.setSchemaSnapshot(template.getFieldSchema());
        submission.setFieldValues(fieldValuesJson);
        submission.setTitle(template.getTemplateNm() + " (" + LocalDate.now().format(TITLE_DATE) + ")");
        submission.setRequesterId(requesterId);
        submission.setRequesterNm(requesterNm);
        submission.setAtchFileId(atchFileId);
        apvSubmissionDAO.insertSubmission(submission);

        for (int i = 0; i < approverIds.size(); i++) {
            ApvLineVO line = new ApvLineVO();
            line.setSubmissionId(submission.getSubmissionId());
            line.setStepOrder(i + 1);
            line.setApproverId(approverIds.get(i));
            line.setApproverNm(approverNms.get(i));
            apvLineDAO.insertLine(line);
        }
    }

    @Override
    public List<ApvSubmissionVO> selectMySubmissionList(String requesterId) {
        return apvSubmissionDAO.selectMySubmissionList(requesterId);
    }

    @Override
    public ApvSubmissionVO selectSubmissionDetail(Long submissionId) {
        ApvSubmissionVO submission = apvSubmissionDAO.selectSubmission(submissionId);
        if (submission != null) {
            submission.setLines(apvLineDAO.selectLinesBySubmission(submissionId));
        }
        return submission;
    }

    @Override
    public List<Map<String, Object>> selectEmployeeCandidates(String keyword) {
        return apvSubmissionDAO.selectEmployeeCandidates(keyword);
    }
}

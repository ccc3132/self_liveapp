package egovframework.egovwebpractice.apv.service.impl;

import egovframework.com.cmm.exception.EgovAccessDeniedException;
import egovframework.egovwebpractice.apv.service.ApvInboxService;
import java.util.List;
import java.util.Map;
import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 표준프레임워크 서비스 규칙: EgovAbstractServiceImpl 을 상속하고 서비스 인터페이스를 구현한다.
 *
 * <p>순차 결재의 핵심 상태 전이. 동시에 같은 라인을 두 번 처리하는 경쟁을 막기 위해
 * UPDATE 는 "대기 상태 + 본인" 조건을 걸고, 영향받은 행이 0이면(이미 처리됐거나 본인이
 * 아니면) 예외를 던진다 — 별도 SELECT FOR UPDATE 없이 단일 조건부 UPDATE 로 충분하다.</p>
 */
@Service("apvInboxService")
@Transactional(readOnly = true)
public class ApvInboxServiceImpl extends EgovAbstractServiceImpl implements ApvInboxService {

    private final ApvLineDAO apvLineDAO;
    private final ApvSubmissionDAO apvSubmissionDAO;

    public ApvInboxServiceImpl(ApvLineDAO apvLineDAO, ApvSubmissionDAO apvSubmissionDAO) {
        this.apvLineDAO = apvLineDAO;
        this.apvSubmissionDAO = apvSubmissionDAO;
    }

    @Override
    public List<Map<String, Object>> selectPendingList(String approverId) {
        return apvLineDAO.selectPendingLines(approverId);
    }

    @Override
    @Transactional
    public void approve(Long submissionId, Long lineId, Integer stepOrder, String approverId) {
        int updated = apvLineDAO.updateLineStatus(lineId, approverId, "승인", null);
        if (updated == 0) {
            throw new EgovAccessDeniedException("이미 처리되었거나 결재 차례가 아닙니다.");
        }
        int maxStep = apvLineDAO.selectMaxStepOrder(submissionId);
        if (stepOrder != null && stepOrder >= maxStep) {
            apvSubmissionDAO.completeSubmission(submissionId, "승인");
        } else {
            apvSubmissionDAO.advanceStep(submissionId);
        }
    }

    @Override
    @Transactional
    public void reject(Long submissionId, Long lineId, String approverId, String comment) {
        int updated = apvLineDAO.updateLineStatus(lineId, approverId, "반려", comment);
        if (updated == 0) {
            throw new EgovAccessDeniedException("이미 처리되었거나 결재 차례가 아닙니다.");
        }
        apvSubmissionDAO.completeSubmission(submissionId, "반려");
    }
}

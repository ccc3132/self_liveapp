package egovframework.egovwebpractice.apv.service;

import java.util.List;
import java.util.Map;
import org.springframework.web.multipart.MultipartFile;

/** 결재 신청(상신) 처리. */
public interface ApvSubmissionService {

    /**
     * 템플릿으로 신청서를 작성해 제출한다. 템플릿의 현재 필드 구성을 스냅샷으로 얼려서 저장하고,
     * 결재자 목록 순서대로 결재선(APV_LINE)을 만든다(1단계부터, 전부 '대기' 상태).
     */
    void submit(ApvTemplateVO template, String fieldValuesJson, String requesterId, String requesterNm,
            List<String> approverIds, List<String> approverNms, List<MultipartFile> files) throws Exception;

    List<ApvSubmissionVO> selectMySubmissionList(String requesterId);

    ApvSubmissionVO selectSubmissionDetail(Long submissionId);

    List<Map<String, Object>> selectEmployeeCandidates(String keyword);
}

package egovframework.egovwebpractice.apv.service;

import java.sql.Timestamp;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/** 사용자가 결재 양식 템플릿으로 올린 신청(상신) 문서. */
@Getter
@Setter
public class ApvSubmissionVO {

    private Long submissionId;
    private Long templateId;
    private String templateNm;
    private String schemaSnapshot;
    private String fieldValues;
    private String title;
    private String requesterId;
    private String requesterNm;
    private String atchFileId;
    private String status;
    private Integer currentStepOrder;
    private Timestamp submittedAt;
    private Timestamp completedAt;

    /** 결재선(단계별 결재자) - 상세 조회 시에만 채워지는 비영속 필드. */
    private List<ApvLineVO> lines;
}

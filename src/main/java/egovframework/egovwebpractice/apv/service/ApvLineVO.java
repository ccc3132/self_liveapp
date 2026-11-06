package egovframework.egovwebpractice.apv.service;

import java.sql.Timestamp;
import lombok.Getter;
import lombok.Setter;

/** 순차 결재선의 한 단계. */
@Getter
@Setter
public class ApvLineVO {

    private Long lineId;
    private Long submissionId;
    private Integer stepOrder;
    private String approverId;
    private String approverNm;
    private String status;
    private String commentCn;
    private Timestamp processedAt;
}

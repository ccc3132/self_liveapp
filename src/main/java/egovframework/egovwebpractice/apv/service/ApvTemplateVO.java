package egovframework.egovwebpractice.apv.service;

import java.sql.Timestamp;
import lombok.Getter;
import lombok.Setter;

/** 관리자가 만드는 결재 양식 템플릿. 필드 구성(FIELD_SCHEMA)은 순서가 있는 JSON 배열 텍스트로 저장한다. */
@Getter
@Setter
public class ApvTemplateVO {

    private Long templateId;
    private String templateNm;
    private String description;
    private String fieldSchema;
    private String fileAtchPosblAt;
    private String useAt;
    private String frstRegisterId;
    private Timestamp frstRegistPnttm;
    private String lastUpdusrId;
    private Timestamp lastUpdatePnttm;
}

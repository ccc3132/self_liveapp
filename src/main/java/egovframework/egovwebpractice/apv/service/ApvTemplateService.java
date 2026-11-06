package egovframework.egovwebpractice.apv.service;

import java.util.List;

/** 결재 양식 템플릿 관리(관리자 전용). */
public interface ApvTemplateService {

    List<ApvTemplateVO> selectTemplateList();

    List<ApvTemplateVO> selectActiveTemplateList();

    ApvTemplateVO selectTemplate(Long templateId);

    void insertTemplate(ApvTemplateVO vo);

    void updateTemplate(ApvTemplateVO vo);

    void updateTemplateUseAt(Long templateId, String useAt);

    void deleteTemplate(Long templateId);
}

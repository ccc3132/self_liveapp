package egovframework.egovwebpractice.apv.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import egovframework.egovwebpractice.apv.service.ApvTemplateService;
import egovframework.egovwebpractice.apv.service.ApvTemplateVO;
import java.util.List;
import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 표준프레임워크 서비스 규칙: EgovAbstractServiceImpl 을 상속하고 서비스 인터페이스를 구현한다. */
@Service("apvTemplateService")
@Transactional(readOnly = true)
public class ApvTemplateServiceImpl extends EgovAbstractServiceImpl implements ApvTemplateService {

    private final ApvTemplateDAO apvTemplateDAO;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ApvTemplateServiceImpl(ApvTemplateDAO apvTemplateDAO) {
        this.apvTemplateDAO = apvTemplateDAO;
    }

    @Override
    public List<ApvTemplateVO> selectTemplateList() {
        return apvTemplateDAO.selectTemplateList();
    }

    @Override
    public List<ApvTemplateVO> selectActiveTemplateList() {
        return apvTemplateDAO.selectActiveTemplateList();
    }

    @Override
    public ApvTemplateVO selectTemplate(Long templateId) {
        return apvTemplateDAO.selectTemplate(templateId);
    }

    @Override
    @Transactional
    public void insertTemplate(ApvTemplateVO vo) {
        validateFieldSchema(vo.getFieldSchema());
        apvTemplateDAO.insertTemplate(vo);
    }

    @Override
    @Transactional
    public void updateTemplate(ApvTemplateVO vo) {
        validateFieldSchema(vo.getFieldSchema());
        apvTemplateDAO.updateTemplate(vo);
    }

    @Override
    @Transactional
    public void updateTemplateUseAt(Long templateId, String useAt) {
        apvTemplateDAO.updateTemplateUseAt(templateId, useAt);
    }

    @Override
    @Transactional
    public void deleteTemplate(Long templateId) {
        apvTemplateDAO.deleteTemplate(templateId);
    }

    /** 빌더가 만든 필드 구성이 비어있지 않은 JSON 배열인지 검증한다(저장 전 1차 방어). */
    private void validateFieldSchema(String fieldSchema) {
        JsonNode node;
        try {
            node = objectMapper.readTree(fieldSchema);
        } catch (Exception e) {
            throw new IllegalArgumentException("필드 구성이 올바른 JSON 형식이 아닙니다.", e);
        }
        if (!node.isArray() || node.isEmpty()) {
            throw new IllegalArgumentException("필드를 하나 이상 구성해야 합니다.");
        }
    }
}

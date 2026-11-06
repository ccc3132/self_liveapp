package egovframework.egovwebpractice.apv.service.impl;

import egovframework.egovwebpractice.apv.service.ApvTemplateVO;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.session.SqlSessionFactory;
import org.egovframe.rte.psl.dataaccess.EgovAbstractMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/** 표준프레임워크 데이터 액세스 규칙: EgovAbstractMapper 를 상속하고 제공 메서드로만 SQL 을 호출한다. */
@Repository("apvTemplateDAO")
public class ApvTemplateDAO extends EgovAbstractMapper {

    @Autowired
    @Override
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        super.setSqlSessionFactory(sqlSessionFactory);
    }

    public List<ApvTemplateVO> selectTemplateList() {
        return selectList("apvTemplateDAO.selectTemplateList");
    }

    public List<ApvTemplateVO> selectActiveTemplateList() {
        return selectList("apvTemplateDAO.selectActiveTemplateList");
    }

    public ApvTemplateVO selectTemplate(Long templateId) {
        return selectOne("apvTemplateDAO.selectTemplate", templateId);
    }

    public void insertTemplate(ApvTemplateVO vo) {
        insert("apvTemplateDAO.insertTemplate", vo);
    }

    public void updateTemplate(ApvTemplateVO vo) {
        update("apvTemplateDAO.updateTemplate", vo);
    }

    public void updateTemplateUseAt(Long templateId, String useAt) {
        update("apvTemplateDAO.updateTemplateUseAt", Map.of("templateId", templateId, "useAt", useAt));
    }

    public void deleteTemplate(Long templateId) {
        delete("apvTemplateDAO.deleteTemplate", templateId);
    }
}

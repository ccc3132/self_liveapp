package egovframework.egovwebpractice.apv.service.impl;

import egovframework.egovwebpractice.apv.service.ApvLineVO;
import java.util.List;
import org.apache.ibatis.session.SqlSessionFactory;
import org.egovframe.rte.psl.dataaccess.EgovAbstractMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/** 표준프레임워크 데이터 액세스 규칙: EgovAbstractMapper 를 상속하고 제공 메서드로만 SQL 을 호출한다. */
@Repository("apvLineDAO")
public class ApvLineDAO extends EgovAbstractMapper {

    @Autowired
    @Override
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        super.setSqlSessionFactory(sqlSessionFactory);
    }

    public void insertLine(ApvLineVO vo) {
        insert("apvLineDAO.insertLine", vo);
    }

    public List<ApvLineVO> selectLinesBySubmission(Long submissionId) {
        return selectList("apvLineDAO.selectLinesBySubmission", submissionId);
    }
}

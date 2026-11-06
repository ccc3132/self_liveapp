package egovframework.egovwebpractice.apv.service.impl;

import egovframework.egovwebpractice.apv.service.ApvSubmissionVO;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.session.SqlSessionFactory;
import org.egovframe.rte.psl.dataaccess.EgovAbstractMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/** 표준프레임워크 데이터 액세스 규칙: EgovAbstractMapper 를 상속하고 제공 메서드로만 SQL 을 호출한다. */
@Repository("apvSubmissionDAO")
public class ApvSubmissionDAO extends EgovAbstractMapper {

    @Autowired
    @Override
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        super.setSqlSessionFactory(sqlSessionFactory);
    }

    public void insertSubmission(ApvSubmissionVO vo) {
        insert("apvSubmissionDAO.insertSubmission", vo);
    }

    public List<ApvSubmissionVO> selectMySubmissionList(String requesterId) {
        return selectList("apvSubmissionDAO.selectMySubmissionList", requesterId);
    }

    public ApvSubmissionVO selectSubmission(Long submissionId) {
        return selectOne("apvSubmissionDAO.selectSubmission", submissionId);
    }

    public List<Map<String, Object>> selectEmployeeCandidates(String keyword) {
        return selectList("apvSubmissionDAO.selectEmployeeCandidates",
                Map.of("keyword", keyword == null ? "" : keyword));
    }
}

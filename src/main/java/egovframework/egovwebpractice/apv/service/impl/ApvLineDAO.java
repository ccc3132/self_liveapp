package egovframework.egovwebpractice.apv.service.impl;

import egovframework.egovwebpractice.apv.service.ApvLineVO;
import java.util.List;
import java.util.Map;
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

    public List<Map<String, Object>> selectPendingLines(String approverId) {
        return selectList("apvLineDAO.selectPendingLines", approverId);
    }

    /** 조건부 UPDATE(대기 상태 + 본인)로 처리하며, 영향받은 행 수를 그대로 반환한다(0이면 이미 처리됨/본인 아님). */
    public int updateLineStatus(Long lineId, String approverId, String status, String commentCn) {
        return update("apvLineDAO.updateLineStatus",
                Map.of("lineId", lineId, "approverId", approverId, "status", status,
                        "commentCn", commentCn == null ? "" : commentCn));
    }

    public int selectMaxStepOrder(Long submissionId) {
        Integer max = selectOne("apvLineDAO.selectMaxStepOrder", submissionId);
        return max == null ? 0 : max;
    }
}

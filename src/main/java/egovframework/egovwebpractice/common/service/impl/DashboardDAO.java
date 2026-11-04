package egovframework.egovwebpractice.common.service.impl;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.session.SqlSessionFactory;
import org.egovframe.rte.psl.dataaccess.EgovAbstractMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/** 표준프레임워크 데이터 액세스 규칙: EgovAbstractMapper 를 상속하고 제공 메서드로만 SQL 을 호출한다. */
@Repository("dashboardDAO")
public class DashboardDAO extends EgovAbstractMapper {

    @Autowired
    @Override
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        super.setSqlSessionFactory(sqlSessionFactory);
    }

    public List<Map<String, Object>> selectTodaySchedules(String uniqId, String today) {
        return selectList("dashboardDAO.selectTodaySchedules", Map.of("uniqId", uniqId, "today", today));
    }

    public List<Map<String, Object>> selectRecentNotices() {
        return selectList("dashboardDAO.selectRecentNotices");
    }

    public int selectUnreadNoteCount(String uniqId) {
        Integer count = selectOne("dashboardDAO.selectUnreadNoteCount", uniqId);
        return count == null ? 0 : count;
    }
}

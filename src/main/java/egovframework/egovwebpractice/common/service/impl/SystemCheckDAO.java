package egovframework.egovwebpractice.common.service.impl;

import egovframework.egovwebpractice.common.service.SystemCheckVO;
import org.apache.ibatis.session.SqlSessionFactory;
import org.egovframe.rte.psl.dataaccess.EgovAbstractMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/** 표준프레임워크 데이터 액세스 규칙: EgovAbstractMapper 를 상속하고 제공 메서드로만 SQL 을 호출한다. */
@Repository("systemCheckDAO")
public class SystemCheckDAO extends EgovAbstractMapper {

    @Autowired
    @Override
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        super.setSqlSessionFactory(sqlSessionFactory);
    }

    public SystemCheckVO selectSystemCheck() {
        return selectOne("systemCheckDAO.selectSystemCheck");
    }

    /** 접속한 DB 종류(databaseId). 인식하지 못하면 null */
    public String selectDatabaseId() {
        return getSqlSession().getConfiguration().getDatabaseId();
    }
}

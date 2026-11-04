package egovframework.egovwebpractice.mail.service.impl;

import egovframework.egovwebpractice.mail.service.MailAccountVO;
import org.apache.ibatis.session.SqlSessionFactory;
import org.egovframe.rte.psl.dataaccess.EgovAbstractMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/** 표준프레임워크 데이터 액세스 규칙: EgovAbstractMapper 를 상속하고 제공 메서드로만 SQL 을 호출한다. */
@Repository("mailAccountDAO")
public class MailAccountDAO extends EgovAbstractMapper {

    @Autowired
    @Override
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        super.setSqlSessionFactory(sqlSessionFactory);
    }

    public MailAccountVO selectMailAccount(String userId) {
        return selectOne("mailAccountDAO.selectMailAccount", userId);
    }

    public void insertMailAccount(MailAccountVO vo) {
        insert("mailAccountDAO.insertMailAccount", vo);
    }

    public void updateMailAccount(MailAccountVO vo) {
        update("mailAccountDAO.updateMailAccount", vo);
    }
}

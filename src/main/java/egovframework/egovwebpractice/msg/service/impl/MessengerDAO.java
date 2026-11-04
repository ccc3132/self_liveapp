package egovframework.egovwebpractice.msg.service.impl;

import egovframework.egovwebpractice.msg.service.ConversationVO;
import egovframework.egovwebpractice.msg.service.MessageVO;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.session.SqlSessionFactory;
import org.egovframe.rte.psl.dataaccess.EgovAbstractMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/** 표준프레임워크 데이터 액세스 규칙: EgovAbstractMapper 를 상속하고 제공 메서드로만 SQL 을 호출한다. */
@Repository("messengerDAO")
public class MessengerDAO extends EgovAbstractMapper {

    @Autowired
    @Override
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        super.setSqlSessionFactory(sqlSessionFactory);
    }

    public List<ConversationVO> selectConversationList(String uniqId) {
        return selectList("messengerDAO.selectConversationList", uniqId);
    }

    public List<MessageVO> selectConversation(String uniqId, String partnerId) {
        return selectList("messengerDAO.selectConversation", Map.of("uniqId", uniqId, "partnerId", partnerId));
    }

    public void insertMessage(MessageVO vo) {
        insert("messengerDAO.insertMessage", vo);
    }

    public void markRead(String uniqId, String partnerId) {
        update("messengerDAO.markRead", Map.of("uniqId", uniqId, "partnerId", partnerId));
    }

    public int selectUnreadCount(String uniqId) {
        Integer count = selectOne("messengerDAO.selectUnreadCount", uniqId);
        return count == null ? 0 : count;
    }
}

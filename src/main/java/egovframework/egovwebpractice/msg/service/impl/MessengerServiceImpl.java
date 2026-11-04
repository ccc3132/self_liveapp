package egovframework.egovwebpractice.msg.service.impl;

import egovframework.egovwebpractice.msg.service.ConversationVO;
import egovframework.egovwebpractice.msg.service.MessageVO;
import egovframework.egovwebpractice.msg.service.MessengerService;
import java.time.LocalDateTime;
import java.util.List;
import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 표준프레임워크 서비스 규칙: EgovAbstractServiceImpl 을 상속하고 서비스 인터페이스를 구현한다. */
@Service("messengerService")
@Transactional(readOnly = true)
public class MessengerServiceImpl extends EgovAbstractServiceImpl implements MessengerService {

    private final MessengerDAO messengerDAO;

    public MessengerServiceImpl(MessengerDAO messengerDAO) {
        this.messengerDAO = messengerDAO;
    }

    @Override
    public List<ConversationVO> selectConversationList(String uniqId) {
        return messengerDAO.selectConversationList(uniqId);
    }

    @Override
    @Transactional
    public List<MessageVO> selectConversation(String uniqId, String partnerId) {
        List<MessageVO> result = messengerDAO.selectConversation(uniqId, partnerId);
        messengerDAO.markRead(uniqId, partnerId);
        return result;
    }

    @Override
    @Transactional
    public void sendMessage(MessageVO vo) {
        vo.setSentAt(LocalDateTime.now());
        messengerDAO.insertMessage(vo);
    }

    @Override
    public int selectUnreadCount(String uniqId) {
        return messengerDAO.selectUnreadCount(uniqId);
    }
}

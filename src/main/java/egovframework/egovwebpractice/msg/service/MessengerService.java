package egovframework.egovwebpractice.msg.service;

import java.util.List;

/** 표준프레임워크 서비스 규칙: 서비스 인터페이스. 1:1 메신저(직접 개발, 공통컴포넌트에 없는 모듈). */
public interface MessengerService {

    List<ConversationVO> selectConversationList(String uniqId) throws Exception;

    List<MessageVO> selectConversation(String uniqId, String partnerId) throws Exception;

    void sendMessage(MessageVO vo) throws Exception;

    int selectUnreadCount(String uniqId) throws Exception;
}

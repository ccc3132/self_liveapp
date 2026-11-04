package egovframework.egovwebpractice.mail.service;

import java.util.List;

/** 표준프레임워크 서비스 규칙: 서비스 인터페이스. IMAP 으로 받은편지함을 읽기 전용으로 조회한다. */
public interface MailReceiveService {

    /** @throws jakarta.mail.MessagingException 연결/인증 실패 시. 호출하는 쪽에서 화면에 메시지로 보여준다. */
    List<ReceivedMailVO> selectInbox(MailAccountVO account, int limit) throws Exception;
}

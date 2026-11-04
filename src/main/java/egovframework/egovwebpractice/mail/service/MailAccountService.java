package egovframework.egovwebpractice.mail.service;

/** 표준프레임워크 서비스 규칙: 서비스 인터페이스. 사용자별 IMAP 수신 계정 설정을 저장/조회한다. */
public interface MailAccountService {

    MailAccountVO selectMailAccount(String userId) throws Exception;

    void saveMailAccount(MailAccountVO vo) throws Exception;

    /** 저장된 계정의 암호화된 비밀번호를 복호화한다(수신 시에만 사용, 화면에는 노출하지 않는다). */
    MailAccountVO selectMailAccountWithPassword(String userId) throws Exception;
}

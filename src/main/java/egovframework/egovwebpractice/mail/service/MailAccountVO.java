package egovframework.egovwebpractice.mail.service;

import lombok.Getter;
import lombok.Setter;

/** 사용자별 IMAP 수신 계정 설정. encryptedPassword 는 DB/화면에 평문이 노출되지 않는다. */
@Getter
@Setter
public class MailAccountVO {

    private String userId;
    private String imapHost;
    private int imapPort = 993;
    private String imapUsername;
    private String password;
    private String encryptedPassword;
    private String useSsl = "Y";
}

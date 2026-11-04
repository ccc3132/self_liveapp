package egovframework.egovwebpractice.mail.service;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/** IMAP 으로 조회한 메일 한 건(읽기 전용, 저장하지 않는다). */
@Getter
@Setter
public class ReceivedMailVO {

    private String subject;
    private String from;
    private LocalDateTime sentDate;
}

package egovframework.egovwebpractice.msg.service;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/** 대화 상대방 한 명과의 대화 요약(목록 화면용). */
@Getter
@Setter
public class ConversationVO {

    private String partnerId;
    private String partnerNm;
    private String lastContent;
    private LocalDateTime lastSentAt;
    private int unreadCount;
}

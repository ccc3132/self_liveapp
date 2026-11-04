package egovframework.egovwebpractice.msg.service;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/** 메신저 쪽지 한 건. */
@Getter
@Setter
public class MessageVO {

    private long msgId;
    private String senderId;
    private String senderNm;
    private String receiverId;
    private String receiverNm;
    private String content;
    private LocalDateTime sentAt;
    private LocalDateTime readAt;
}

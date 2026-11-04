package egovframework.egovwebpractice.mail.service.impl;

import egovframework.egovwebpractice.mail.service.MailAccountVO;
import egovframework.egovwebpractice.mail.service.MailReceiveService;
import egovframework.egovwebpractice.mail.service.ReceivedMailVO;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.Store;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.egovframe.rte.fdl.cmmn.EgovAbstractServiceImpl;
import org.springframework.stereotype.Service;

/**
 * IMAP 받은편지함 조회(직접 개발, 공통컴포넌트에 없는 모듈). 조회만 하고 쓰지 않으며(읽음 처리 안 함),
 * 메일을 이 앱에 저장하지도 않는다 - 매 요청마다 IMAP 서버에 새로 연결해서 보여준다.
 */
@Service("mailReceiveService")
public class MailReceiveServiceImpl extends EgovAbstractServiceImpl implements MailReceiveService {

    @Override
    public List<ReceivedMailVO> selectInbox(MailAccountVO account, int limit) throws Exception {
        Properties props = new Properties();
        String protocol = "Y".equals(account.getUseSsl()) ? "imaps" : "imap";
        props.put("mail.store.protocol", protocol);
        props.put("mail." + protocol + ".host", account.getImapHost());
        props.put("mail." + protocol + ".port", String.valueOf(account.getImapPort()));

        Session session = Session.getInstance(props);
        List<ReceivedMailVO> result = new ArrayList<>();

        try (Store store = session.getStore(protocol)) {
            store.connect(account.getImapHost(), account.getImapPort(), account.getImapUsername(), account.getPassword());
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);
            try {
                int count = inbox.getMessageCount();
                int from = Math.max(1, count - limit + 1);
                if (count > 0) {
                    Message[] messages = inbox.getMessages(from, count);
                    for (int i = messages.length - 1; i >= 0; i--) {
                        Message m = messages[i];
                        ReceivedMailVO vo = new ReceivedMailVO();
                        vo.setSubject(m.getSubject());
                        vo.setFrom(m.getFrom() == null || m.getFrom().length == 0 ? "" : m.getFrom()[0].toString());
                        if (m.getSentDate() != null) {
                            vo.setSentDate(LocalDateTime.ofInstant(m.getSentDate().toInstant(), ZoneId.systemDefault()));
                        }
                        result.add(vo);
                    }
                }
            } finally {
                inbox.close(false);
            }
        }
        return result;
    }
}

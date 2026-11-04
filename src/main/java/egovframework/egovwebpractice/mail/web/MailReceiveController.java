package egovframework.egovwebpractice.mail.web;

import egovframework.com.cmm.LoginVO;
import egovframework.com.cmm.util.EgovUserDetailsHelper;
import egovframework.egovwebpractice.mail.service.MailAccountService;
import egovframework.egovwebpractice.mail.service.MailAccountVO;
import egovframework.egovwebpractice.mail.service.MailReceiveService;
import jakarta.mail.MessagingException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * 메일 수신(직접 개발, 공통컴포넌트에 없는 모듈). 사용자별 IMAP 계정을 등록하면 받은편지함을 조회할 수 있다.
 * 받은 메일은 이 앱에 저장하지 않고, 조회할 때마다 IMAP 서버에 새로 연결한다.
 */
@Controller
public class MailReceiveController {

    private static final int INBOX_LIMIT = 20;

    private final MailAccountService mailAccountService;
    private final MailReceiveService mailReceiveService;

    public MailReceiveController(MailAccountService mailAccountService, MailReceiveService mailReceiveService) {
        this.mailAccountService = mailAccountService;
        this.mailReceiveService = mailReceiveService;
    }

    private LoginVO currentUser() {
        return (LoginVO) EgovUserDetailsHelper.getAuthenticatedUser();
    }

    @GetMapping("/mail/EgovMailAccountSetting.do")
    public String settingView(Model model) throws Exception {
        LoginVO user = currentUser();
        MailAccountVO account = mailAccountService.selectMailAccount(user.getId());
        model.addAttribute("account", account == null ? new MailAccountVO() : account);
        return "egovframework/egovwebpractice/mail/EgovMailAccountSetting";
    }

    @PostMapping("/mail/saveMailAccount.do")
    public String saveAccount(@ModelAttribute MailAccountVO account) throws Exception {
        LoginVO user = currentUser();
        account.setUserId(user.getId());
        mailAccountService.saveMailAccount(account);
        return "redirect:/mail/EgovMailInbox.do";
    }

    @GetMapping("/mail/EgovMailInbox.do")
    public String inbox(Model model) throws Exception {
        LoginVO user = currentUser();
        MailAccountVO account = mailAccountService.selectMailAccountWithPassword(user.getId());
        if (account == null) {
            return "redirect:/mail/EgovMailAccountSetting.do";
        }
        try {
            model.addAttribute("mailList", mailReceiveService.selectInbox(account, INBOX_LIMIT));
        } catch (MessagingException e) {
            model.addAttribute("error", "메일 서버에 연결할 수 없습니다: " + e.getMessage());
        }
        return "egovframework/egovwebpractice/mail/EgovMailInbox";
    }
}

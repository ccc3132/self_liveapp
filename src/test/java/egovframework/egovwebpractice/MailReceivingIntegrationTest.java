package egovframework.egovwebpractice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import egovframework.egovwebpractice.mail.service.MailAccountService;
import egovframework.egovwebpractice.mail.service.MailAccountVO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

/**
 * 메일 수신(IMAP, 직접 개발 모듈). 받은 메일은 저장하지 않고 조회마다 IMAP 서버에 새로 연결하므로,
 * 실제 IMAP 연결 자체는 이 테스트로 확인하지 않는다(아직 확인하지 못한 것 - 실제 IMAP 서버/Mailpit
 * 은 IMAP 수신 테스트를 지원하지 않아 java -jar 로도 연결 자체는 검증하지 못했다).
 * 이 테스트는 계정 설정 저장/암호화와, 연결 실패 시 500 대신 에러 메시지로 처리되는지를 확인한다.
 */
@SpringBootTest(properties = "app.embedded-db.dir=target/test-pg-mailrecv")
@AutoConfigureMockMvc
class MailReceivingIntegrationTest {

    private static final String LOGIN_PAGE_PATTERN = "**/uat/uia/egovLoginUsr.do";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private MailAccountService mailAccountService;

    private MockHttpSession login(String id, String password, String userSe) throws Exception {
        MvcResult result = mockMvc.perform(post("/uat/uia/actionLogin.do").with(csrf())
                        .param("id", id).param("password", password).param("userSe", userSe))
                .andReturn();
        return (MockHttpSession) result.getRequest().getSession(false);
    }

    @Test
    void 비로그인은_메일_수신_화면에_접근할_수_없다() throws Exception {
        mockMvc.perform(get("/mail/EgovMailAccountSetting.do"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern(LOGIN_PAGE_PATTERN));
    }

    @Test
    void 계정_설정을_저장하면_비밀번호가_암호화되어_저장된다() throws Exception {
        MockHttpSession user = login("USER", "rhdxhd12", "GNR");
        mockMvc.perform(post("/mail/saveMailAccount.do").with(csrf()).session(user)
                        .param("imapHost", "imap.example.com").param("imapPort", "993")
                        .param("imapUsername", "user@example.com").param("password", "secret-password")
                        .param("useSsl", "Y"))
                .andExpect(status().is3xxRedirection());

        String encrypted = jdbcTemplate.queryForObject(
                "select encrypted_password from mail_account where user_id = 'USER'", String.class);
        assertNotEquals("secret-password", encrypted, "비밀번호는 평문으로 저장되면 안 된다");

        MailAccountVO withPassword = mailAccountService.selectMailAccountWithPassword("USER");
        assertEquals("secret-password", withPassword.getPassword(), "복호화하면 원래 비밀번호가 나와야 한다");
    }

    @Test
    void 메일서버_연결에_실패해도_500이_아니라_에러_메시지로_보여준다() throws Exception {
        MockHttpSession user = login("USER", "rhdxhd12", "GNR");
        mockMvc.perform(post("/mail/saveMailAccount.do").with(csrf()).session(user)
                        .param("imapHost", "127.0.0.1").param("imapPort", "19999")
                        .param("imapUsername", "nobody").param("password", "nopass").param("useSsl", "N"));

        mockMvc.perform(get("/mail/EgovMailInbox.do").session(user)).andExpect(status().isOk());
    }
}

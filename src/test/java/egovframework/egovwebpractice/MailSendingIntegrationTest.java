package egovframework.egovwebpractice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

/**
 * 메일 발송(cop.ems). 발송 화면·발송 이력(cop.ems.web)을 새로 들여왔다 — 발송 자체(EgovMultiPartEmail)와
 * DB 기록(COMTHEMAILDSPTCHMANAGE)은 "비밀번호 찾기" 때문에 이미 있었다. 발송결과 XML 요청 파일을
 * 남기는 보조 기능(trnsmitXmlData)은 Spring Boot 실행 가능 jar 의 클래스로더에서 XMLBeans 생성 클래스를
 * 못 찾아 깨지는 문제가 있어, 실패해도 발송 결과 자체에는 영향이 없도록 try-catch 로 방어했다
 * (java -jar + Mailpit(로컬 가짜 SMTP) 로 실제 발송 자체는 확인했고, 이 테스트는 외부 SMTP 없이
 * DB 기록까지만 확인한다 — 발송 실패(코드 'F')도 이 테스트 환경에서는 정상이다).
 */
@SpringBootTest(properties = "app.embedded-db.dir=target/test-pg-mail")
@AutoConfigureMockMvc
class MailSendingIntegrationTest {

    private static final String LOGIN_PAGE_PATTERN = "**/uat/uia/egovLoginUsr.do";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private MockHttpSession login(String id, String password, String userSe) throws Exception {
        MvcResult result = mockMvc.perform(post("/uat/uia/actionLogin.do").with(csrf())
                        .param("id", id).param("password", password).param("userSe", userSe))
                .andReturn();
        return (MockHttpSession) result.getRequest().getSession(false);
    }

    @Test
    void 비로그인은_메일발송_화면에_접근할_수_없다() throws Exception {
        mockMvc.perform(get("/cop/ems/insertSndngMailView.do"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern(LOGIN_PAGE_PATTERN));
    }

    @Test
    void 일반회원이_메일을_보내면_발송_기록이_DB에_남는다() throws Exception {
        MockHttpSession user = login("USER", "rhdxhd12", "GNR");
        mockMvc.perform(multipart("/cop/ems/insertSndngMail.do").with(csrf()).session(user)
                        .param("recptnPerson", "test@example.com")
                        .param("sj", "통합테스트 메일").param("emailCn", "본문"))
                .andReturn();

        Integer count = jdbcTemplate.queryForObject(
                "select count(1) from comthemaildsptchmanage where sj = '통합테스트 메일' and rcver = 'test@example.com'",
                Integer.class);
        assertEquals(1, count, "발송 시도 자체는 DB 에 기록되어야 한다(SMTP 미설정이면 발송결과만 실패)");
    }

    @Test
    void 관리자는_메일_발송_이력을_볼_수_있다() throws Exception {
        MockHttpSession admin = login("TEST1", "rhdxhd12", "USR");
        mockMvc.perform(get("/cop/ems/selectSndngMailList.do").session(admin)).andExpect(status().isOk());
    }
}

package egovframework.egovwebpractice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
 * 메신저(1:1 대화, 직접 개발 모듈). 실시간 push(WebSocket) 는 아직 없고, 화면 재방문으로 갱신된다
 * (아직 확인하지 못한 것).
 */
@SpringBootTest(properties = "app.embedded-db.dir=target/test-pg-msg")
@AutoConfigureMockMvc
class MessengerIntegrationTest {

    private static final String LOGIN_PAGE_PATTERN = "**/uat/uia/egovLoginUsr.do";
    private static final String TEST1_ESNTL_ID = "USRCNFRM_00000000000";
    private static final String USER_ESNTL_ID = "USRCNFRM_00000000001";

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
    void 비로그인은_메신저에_접근할_수_없다() throws Exception {
        mockMvc.perform(get("/msg/EgovMessengerMain.do"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern(LOGIN_PAGE_PATTERN));
    }

    @Test
    void 일반회원이_관리자에게_쪽지를_보내면_대화에_쌓이고_읽으면_안읽음이_줄어든다() throws Exception {
        MockHttpSession user = login("USER", "rhdxhd12", "GNR");
        mockMvc.perform(post("/msg/sendMessage.do").with(csrf()).session(user)
                        .param("receiverId", TEST1_ESNTL_ID).param("partnerNm", "테스트1")
                        .param("content", "통합테스트 메신저 메시지"))
                .andExpect(status().is3xxRedirection());

        Integer count = jdbcTemplate.queryForObject(
                "select count(1) from msgr_message where content = '통합테스트 메신저 메시지' and receiver_id = ?",
                Integer.class, TEST1_ESNTL_ID);
        assertEquals(1, count);

        Integer unreadBefore = jdbcTemplate.queryForObject(
                "select count(1) from msgr_message where receiver_id = ? and read_at is null",
                Integer.class, TEST1_ESNTL_ID);
        assertEquals(1, unreadBefore);

        MockHttpSession admin = login("TEST1", "rhdxhd12", "USR");
        mockMvc.perform(get("/msg/EgovMessengerConversation.do").session(admin)
                        .param("partnerId", USER_ESNTL_ID).param("partnerNm", "일반회원"))
                .andExpect(status().isOk());

        Integer unreadAfter = jdbcTemplate.queryForObject(
                "select count(1) from msgr_message where receiver_id = ? and read_at is null",
                Integer.class, TEST1_ESNTL_ID);
        assertEquals(0, unreadAfter, "대화를 읽으면 안읽음이 줄어야 한다");
    }
}

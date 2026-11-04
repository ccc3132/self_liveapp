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
 * 쪽지(uss.ion.ntm/ntr/nts)·주소록(cop.adb) 도입.
 *
 * <p>쪽지 보내기/상세조회 핵심 기능에 원본 소스 자체의 {@code @RequireAdmin} 이 걸려 있던 것을
 * 이 프로젝트에서 제거했다(일반회원이 전혀 쓸 수 없는 기능이 되는 건 쪽지의 목적과 맞지 않음).
 * 수신함/발신함 상세는 여전히 본인만 볼 수 있다(EgovAuthorizationHelper.assertOwner, 원본 그대로).
 */
@SpringBootTest(properties = "app.embedded-db.dir=target/test-pg-note")
@AutoConfigureMockMvc
class NoteAndAddressBookIntegrationTest {

    private static final String LOGIN_PAGE_PATTERN = "**/uat/uia/egovLoginUsr.do";
    private static final String TEST1_ESNTL_ID = "USRCNFRM_00000000000";

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
    void 비로그인은_쪽지함에_접근할_수_없다() throws Exception {
        mockMvc.perform(get("/uss/ion/ntr/listNoteRecptn.do"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern(LOGIN_PAGE_PATTERN));
    }

    @Test
    void 일반회원이_관리자에게_쪽지를_보내면_받은쪽지함에_쌓인다() throws Exception {
        MockHttpSession user = login("USER", "rhdxhd12", "GNR");
        mockMvc.perform(multipart("/uss/ion/ntm/registEgovNoteManageActor.do").with(csrf()).session(user)
                        .param("recptnEmpList", TEST1_ESNTL_ID).param("recptnSeList", "1")
                        .param("noteSj", "통합테스트 쪽지").param("noteCn", "안녕하세요"))
                .andExpect(status().is3xxRedirection());

        Integer count = jdbcTemplate.queryForObject(
                "select count(1) from comtnnote n join comtnnoterecptn r on n.note_id = r.note_id"
                        + " where n.note_sj = '통합테스트 쪽지' and trim(r.rcver_id) = ?",
                Integer.class, TEST1_ESNTL_ID);
        assertEquals(1, count, "관리자(TEST1)의 받은쪽지함에 쌓여야 한다");
    }

    @Test
    void 관리자는_받은쪽지함을_볼_수_있다() throws Exception {
        MockHttpSession admin = login("TEST1", "rhdxhd12", "USR");
        mockMvc.perform(get("/uss/ion/ntr/listNoteRecptn.do").session(admin)).andExpect(status().isOk());
    }

    @Test
    void 일반회원이_주소록을_만들면_DB에_저장된다() throws Exception {
        MockHttpSession user = login("USER", "rhdxhd12", "GNR");
        mockMvc.perform(post("/cop/adb/RegistAdbkInf.do").with(csrf()).session(user)
                        .param("adbkNm", "통합테스트 주소록").param("othbcScope", "1").param("useAt", "Y"))
                .andExpect(status().isOk());

        Integer count = jdbcTemplate.queryForObject(
                "select count(1) from comtnadbkmanage where adbk_nm = '통합테스트 주소록'", Integer.class);
        assertEquals(1, count);
    }

    @Test
    void 비로그인은_주소록에_접근할_수_없다() throws Exception {
        mockMvc.perform(get("/cop/adb/selectAdbkList.do"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern(LOGIN_PAGE_PATTERN));
    }
}

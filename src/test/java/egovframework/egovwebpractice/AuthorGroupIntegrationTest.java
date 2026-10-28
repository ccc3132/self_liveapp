package egovframework.egovwebpractice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

/**
 * 공통컴포넌트 권한그룹(sec.rgm): 관리자가 사용자에게 권한을 지정한다.
 * 이 화면은 권한 상승에 쓰일 수 있어 관리자만 접근해야 한다(@RequireAdmin + V8).
 */
@SpringBootTest(properties = "app.embedded-db.dir=target/test-pg-authority")
@AutoConfigureMockMvc
class AuthorGroupIntegrationTest {

    private static final String LIST_URL = "/sec/rgm/EgovAuthorGroupList.do";
    private static final String LIST_JSP = "/WEB-INF/jsp/egovframework/com/sec/rgm/EgovAuthorGroupManage.jsp";
    private static final String LOGIN_PAGE_PATTERN = "**/uat/uia/egovLoginUsr.do";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private MockHttpSession login(String id, String password, String userSe) throws Exception {
        MvcResult result = mockMvc.perform(post("/uat/uia/actionLogin.do").with(csrf())
                        .param("id", id).param("password", password).param("userSe", userSe))
                .andExpect(redirectedUrl("/main.do"))
                .andReturn();
        return (MockHttpSession) result.getRequest().getSession(false);
    }

    @Test
    void 비로그인은_권한그룹_화면에_접근할_수_없다() throws Exception {
        mockMvc.perform(get(LIST_URL))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern(LOGIN_PAGE_PATTERN));
    }

    @Test
    void 일반회원은_권한그룹_화면에_접근할_수_없다() throws Exception {
        MockHttpSession user = login("USER", "rhdxhd12", "GNR");
        mockMvc.perform(get(LIST_URL).session(user)).andExpect(forwardedUrl("/sec/ram/accessDenied.do"));
        // 일반회원이 직접 권한을 지정(권한 상승)하려는 요청도 거부된다
        mockMvc.perform(post("/sec/rgm/EgovAuthorGroupInsert.do").with(csrf()).session(user)
                        .param("userIds", "USRCNFRM_00000000001").param("authorCodes", "ROLE_ADMIN")
                        .param("regYns", "Y").param("mberTyCodes", "USR03"))
                .andExpect(forwardedUrl("/sec/ram/accessDenied.do"));
        String role = jdbcTemplate.queryForObject(
                "select author_code from comtnemplyrscrtyestbs where scrty_dtrmn_trget_id = 'USRCNFRM_00000000001'", String.class);
        assertEquals("ROLE_USER", role, "일반회원이 자신의 권한을 바꿀 수 있다");
    }

    @Test
    @SuppressWarnings("unchecked")
    void 관리자는_사용자_권한_목록을_볼_수_있다() throws Exception {
        MockHttpSession admin = login("TEST1", "rhdxhd12", "USR");
        MvcResult result = mockMvc.perform(get(LIST_URL).session(admin))
                .andExpect(status().isOk())
                .andExpect(forwardedUrl(LIST_JSP))
                .andReturn();
        List<Object> users = (List<Object>) result.getModelAndView().getModel().get("authorGroupList");
        List<Object> authorities = (List<Object>) result.getModelAndView().getModel().get("authorManageList");
        assertNotNull(users);
        assertFalse(users.isEmpty());
        assertFalse(authorities.isEmpty(), "지정할 수 있는 권한 목록(ROLE_USER, ROLE_ADMIN ...)이 있어야 한다");
    }

    @Test
    void 관리자가_권한을_지정하면_새_업무사용자가_로그인할_수_있다() throws Exception {
        MockHttpSession admin = login("TEST1", "rhdxhd12", "USR");
        String password = "Zx!9kP#4mQ7v";
        mockMvc.perform(post("/uss/umt/EgovEmplyrInsert.do").with(csrf()).session(admin)
                        .param("emplyrId", "AUTHEMP01").param("emplyrNm", "authority employee")
                        .param("password", password).param("password2", password)
                        .param("passwordHint", "P01").param("passwordCnsr", "answer")
                        .param("emplyrSttusCode", "P").param("orgnztId", "ORGNZT_0000000000000")
                        .param("groupId", "GROUP_00000000000000").param("insttCode", "00000001")
                        .param("sexdstnCode", "M").param("areaNo", "02").param("homemiddleTelno", "1234")
                        .param("homeendTelno", "5678").param("moblphonNo", "01012345678")
                        .param("emailAdres", "auth@example.com").param("zip", "100775")
                        .param("homeadres", "seoul").param("detailAdres", "detail").param("ofcpsNm", "staff"))
                .andExpect(forwardedUrl("/uss/umt/EgovEmplyrManage.do"));
        String esntlId = jdbcTemplate.queryForObject(
                "select esntl_id from comtnemplyrinfo where emplyr_id = 'AUTHEMP01'", String.class);

        // 권한 지정 전: 로그인 불가
        mockMvc.perform(post("/uat/uia/actionLogin.do").with(csrf())
                        .param("id", "AUTHEMP01").param("password", password).param("userSe", "USR"))
                .andExpect(redirectedUrl("/uat/uia/egovLoginUsr.do?login_error=1"));

        // 관리자가 권한그룹 화면의 요청으로 ROLE_USER 를 지정 (regYns=N: 아직 권한이 지정되지 않은 사용자)
        mockMvc.perform(post("/sec/rgm/EgovAuthorGroupInsert.do").with(csrf()).session(admin)
                        .param("userIds", esntlId).param("authorCodes", "ROLE_USER")
                        .param("regYns", "N").param("mberTyCodes", "USR01"))
                .andExpect(forwardedUrl("/sec/rgm/EgovAuthorGroupList.do"));
        String assigned = jdbcTemplate.queryForObject(
                "select author_code from comtnemplyrscrtyestbs where scrty_dtrmn_trget_id = ?", String.class, esntlId);
        assertEquals("ROLE_USER", assigned);

        // 권한 지정 후: 로그인 가능
        login("AUTHEMP01", password, "USR");
    }
}

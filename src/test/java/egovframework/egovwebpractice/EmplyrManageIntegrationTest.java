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
 * 공통컴포넌트 업무사용자 관리(uss.umt)를 확인한다.
 * 회원관리 URL(/uss/umt/**)은 관리자만 접근할 수 있어야 한다(V7).
 */
@SpringBootTest(properties = "app.embedded-db.dir=target/test-pg-emplyr")
@AutoConfigureMockMvc
class EmplyrManageIntegrationTest {

    private static final String LIST_URL = "/uss/umt/EgovEmplyrManage.do";
    private static final String LIST_JSP = "/WEB-INF/jsp/egovframework/com/uss/umt/EgovEmplyrManage.jsp";
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
    void 비로그인은_업무사용자_목록에_접근할_수_없다() throws Exception {
        mockMvc.perform(get(LIST_URL))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern(LOGIN_PAGE_PATTERN));
    }

    @Test
    void 일반회원은_업무사용자_목록에_접근할_수_없다() throws Exception {
        MockHttpSession user = login("USER", "rhdxhd12", "GNR");
        // 보안 모듈이 접근 거부 화면으로 forward 한다 (MockMvc 는 forward 를 실행하지 않으므로 대상만 확인.
        // 실제 응답 상태 403 은 java -jar 로 확인)
        mockMvc.perform(get(LIST_URL).session(user))
                .andExpect(forwardedUrl("/sec/ram/accessDenied.do"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void 관리자는_업무사용자_목록을_볼_수_있다() throws Exception {
        MockHttpSession admin = login("TEST1", "rhdxhd12", "USR");
        MvcResult result = mockMvc.perform(get(LIST_URL).session(admin))
                .andExpect(status().isOk())
                .andExpect(forwardedUrl(LIST_JSP))
                .andReturn();
        List<Object> rows = (List<Object>) result.getModelAndView().getModel().get("resultList");
        assertNotNull(rows);
        assertFalse(rows.isEmpty(), "초기 데이터의 업무사용자(TEST1, webmaster)가 조회되어야 한다");
        assertNotNull(result.getModelAndView().getModel().get("paginationInfo"));
    }

    @Test
    void 관리자는_업무사용자_등록_화면을_볼_수_있다() throws Exception {
        MockHttpSession admin = login("TEST1", "rhdxhd12", "USR");
        mockMvc.perform(post("/uss/umt/EgovEmplyrInsertView.do").with(csrf()).session(admin))
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("/WEB-INF/jsp/egovframework/com/uss/umt/EgovEmplyrInsert.jsp"));
    }

    @Test
    void 관리자가_등록한_업무사용자는_권한이_지정되어야_로그인할_수_있다() throws Exception {
        MockHttpSession admin = login("TEST1", "rhdxhd12", "USR");
        String password = "Zx!9kP#4mQ7v"; // 연속된 숫자·문자가 없는 비밀번호여야 한다
        mockMvc.perform(post("/uss/umt/EgovEmplyrInsert.do").with(csrf()).session(admin)
                        .param("emplyrId", "NEWEMP01").param("emplyrNm", "new employee")
                        .param("password", password).param("password2", password)
                        .param("passwordHint", "P01").param("passwordCnsr", "answer")
                        .param("emplyrSttusCode", "P").param("orgnztId", "ORGNZT_0000000000000")
                        .param("groupId", "GROUP_00000000000000").param("insttCode", "00000001")
                        .param("sexdstnCode", "M").param("areaNo", "02").param("homemiddleTelno", "1234")
                        .param("homeendTelno", "5678").param("moblphonNo", "01012345678")
                        .param("emailAdres", "new@example.com").param("zip", "100775")
                        .param("homeadres", "seoul").param("detailAdres", "detail").param("ofcpsNm", "staff"))
                .andExpect(forwardedUrl("/uss/umt/EgovEmplyrManage.do")); // 등록 성공 시 목록으로 이동

        Integer registered = jdbcTemplate.queryForObject(
                "select count(*) from comtnemplyrinfo where emplyr_id = 'NEWEMP01'", Integer.class);
        assertEquals(1, registered);
        String esntlId = jdbcTemplate.queryForObject(
                "select esntl_id from comtnemplyrinfo where emplyr_id = 'NEWEMP01'", String.class);
        assertNotNull(esntlId); // ID 생성기(USRCNFRM_...)가 만든 고유 ID

        // 공통컴포넌트는 등록할 때 권한을 만들지 않는다. 권한 지정 전에는 로그인되지 않는다.
        mockMvc.perform(post("/uat/uia/actionLogin.do").with(csrf())
                        .param("id", "NEWEMP01").param("password", password).param("userSe", "USR"))
                .andExpect(redirectedUrl("/uat/uia/egovLoginUsr.do?login_error=1"));

        jdbcTemplate.update("insert into comtnemplyrscrtyestbs (scrty_dtrmn_trget_id, mber_ty_code, author_code) values (?, 'USR01', 'ROLE_USER')", esntlId);
        MockHttpSession employee = login("NEWEMP01", password, "USR");
        // 새 직원은 ROLE_USER 이므로 업무사용자 관리에는 접근할 수 없다
        mockMvc.perform(get(LIST_URL).session(employee)).andExpect(forwardedUrl("/sec/ram/accessDenied.do"));
    }
}

package egovframework.egovwebpractice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

/**
 * 일정관리(cop.smt) 9개 하위 모듈(개인/부서/전사 일정, 상급자 일정, 업무일지, 부서업무, 메모보고,
 * 메모할일, 주간/월간 보고) 전부 도입. 부서 일정(sdm)·부서업무(djm)는 원본 소스 자체에 이미
 * {@code @RequireAdmin} 이 붙어 있어 관리자 전용이고, 나머지는 로그인한 누구나 접근한다(원본 설계 그대로,
 * 이 프로젝트에서 새로 URL 규칙을 추가하지 않았다).
 *
 * <p>9개 전부를 끝까지 쓰기 테스트하진 않고, 대표로 개인일정(sim 전체 읽기/쓰기)과 부서업무(djm, 관리자
 * 전용 확인)는 깊게, 나머지는 로그인 후 목록 화면이 500 없이 뜨는지만 확인한다.
 */
@SpringBootTest(properties = "app.embedded-db.dir=target/test-pg-smt")
@AutoConfigureMockMvc
class ScheduleManagementIntegrationTest {

    private static final String LOGIN_PAGE_PATTERN = "**/uat/uia/egovLoginUsr.do";
    private static final String ACCESS_DENIED_URL = "/WEB-INF/jsp/egovframework/com/cmm/error/accessDenied.jsp";

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
    void 비로그인은_개인일정_목록에_접근할_수_없다() throws Exception {
        mockMvc.perform(get("/cop/smt/sim/EgovIndvdlSchdulManageMainList.do"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern(LOGIN_PAGE_PATTERN));
    }

    @Test
    void 일반회원이_개인일정을_쓰면_DB에_저장된다() throws Exception {
        MockHttpSession user = login("USER", "rhdxhd12", "GNR");
        mockMvc.perform(multipart("/cop/smt/sim/EgovIndvdlSchdulManageRegistActor.do?cmd=save").with(csrf()).session(user)
                        .param("schdulNm", "통합테스트 개인일정").param("schdulCn", "내용")
                        .param("schdulBgnde", "20261105").param("schdulBgndeHH", "09").param("schdulBgndeMM", "00")
                        .param("schdulEndde", "20261105").param("schdulEnddeHH", "10").param("schdulEnddeMM", "00")
                        .param("schdulIpcrCode", "B").param("schdulSe", "1").param("reptitSeCode", "1")
                        .param("schdulPlace", "회의실")
                        .param("schdulBgndeYYYMMDD", "20261105").param("schdulEnddeYYYMMDD", "20261105"))
                .andExpect(status().is3xxRedirection());

        Integer count = jdbcTemplate.queryForObject(
                "select count(1) from comtnschdulinfo where schdul_nm = '통합테스트 개인일정' and schdul_knd_code = '2'",
                Integer.class);
        assertEquals(1, count, "개인일정(SCHDUL_KND_CODE=2)으로 저장되어야 한다");
    }

    @Test
    void 일반회원은_부서일정_관리_화면에_접근할_수_없다() throws Exception {
        MockHttpSession user = login("USER", "rhdxhd12", "GNR");
        mockMvc.perform(get("/cop/smt/sdm/EgovDeptSchdulManageDailyList.do").session(user))
                .andExpect(forwardedUrl(ACCESS_DENIED_URL));
    }

    @Test
    void 일반회원은_부서업무함을_만들_수_없다() throws Exception {
        MockHttpSession user = login("USER", "rhdxhd12", "GNR");
        mockMvc.perform(post("/cop/smt/djm/insertDeptJobBx.do").with(csrf()).session(user)
                        .param("deptJobBxNm", "해킹업무함").param("deptId", "ORGNZT_0000000000000"))
                .andExpect(forwardedUrl(ACCESS_DENIED_URL));
    }

    @Test
    void 관리자는_자기_부서에_부서업무함을_만들_수_있다() throws Exception {
        MockHttpSession admin = login("TEST1", "rhdxhd12", "USR");
        mockMvc.perform(post("/cop/smt/djm/insertDeptJobBx.do").with(csrf()).session(admin)
                        .param("deptJobBxNm", "통합테스트 업무함").param("deptId", "ORGNZT_0000000000000")
                        .param("deptNm", "테스트부서").param("indictOrdr", "1"))
                .andExpect(status().isOk());

        Integer count = jdbcTemplate.queryForObject(
                "select count(1) from comtndeptjobbx where dept_jobbx_nm = '통합테스트 업무함'", Integer.class);
        assertEquals(1, count);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "/cop/smt/sam/EgovAllSchdulManageList.do",
            "/cop/smt/lsm/usr/selectLeaderSchdulList.do",
            "/cop/smt/mtm/selectMemoTodoList.do",
            "/cop/smt/mrm/selectMemoReprtList.do",
            "/cop/smt/wmr/selectWikMnthngReprtList.do",
            "/cop/smt/dsm/EgovDiaryManageList.do",
            "/cop/smt/djm/selectDeptJobBxList.do",
            "/cop/smt/sdm/EgovDeptSchdulManageMainList.do"
    })
    void 로그인하면_나머지_일정관리_화면도_500_없이_뜬다(String url) throws Exception {
        MockHttpSession user = login("TEST1", "rhdxhd12", "USR");
        mockMvc.perform(get(url).session(user)).andExpect(status().isOk());
    }
}

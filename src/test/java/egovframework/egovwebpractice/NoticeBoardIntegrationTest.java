package egovframework.egovwebpractice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
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
 * 공지 게시판(cop.bbs, 스코프 B: 게시판+게시물만). 초기 데이터로 "공지사항" 게시판(BBSMSTR_000000000001)이 하나 있다(V9).
 * 게시판 설정(등록/수정/삭제/목록/상세)은 관리자만, 게시물 조회/작성은 로그인한 누구나 가능하다(URL 규칙 + 보안 모듈의 접근 거부 처리).
 * MockMvc 는 forward 를 실행하지 않고 JSP 를 그리지 않으므로(README 9장), 접근 거부는 forwardedUrl 대상으로,
 * 성공한 쓰기는 DB 상태로 확인한다. 실제 403 응답과 화면은 java -jar 로 확인했다.
 */
@SpringBootTest(properties = "app.embedded-db.dir=target/test-pg-bbs")
@AutoConfigureMockMvc
class NoticeBoardIntegrationTest {

    private static final String BOARD_LIST_URL = "/cop/bbs/selectBBSMasterInfs.do";
    private static final String ARTICLE_LIST_URL = "/cop/bbs/selectArticleList.do";
    private static final String ARTICLE_INSERT_URL = "/cop/bbs/insertArticle.do";
    private static final String BBS_ID = "BBSMSTR_000000000001";
    private static final String LOGIN_PAGE_PATTERN = "**/uat/uia/egovLoginUsr.do";
    private static final String ACCESS_DENIED_URL = "/sec/ram/accessDenied.do";

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
    void 비로그인은_게시판_목록도_게시글_목록도_볼_수_없다() throws Exception {
        mockMvc.perform(get(BOARD_LIST_URL))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern(LOGIN_PAGE_PATTERN));
        mockMvc.perform(get(ARTICLE_LIST_URL).param("bbsId", BBS_ID))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern(LOGIN_PAGE_PATTERN));
    }

    @Test
    void 일반회원은_게시판_설정은_못보지만_게시글은_볼_수_있다() throws Exception {
        MockHttpSession user = login("USER", "rhdxhd12", "GNR");
        mockMvc.perform(get(BOARD_LIST_URL).session(user)).andExpect(forwardedUrl(ACCESS_DENIED_URL));
        mockMvc.perform(get(ARTICLE_LIST_URL).param("bbsId", BBS_ID).session(user))
                .andExpect(status().isOk());
    }

    @Test
    @SuppressWarnings("unchecked")
    void 관리자는_게시판_목록을_볼_수_있다() throws Exception {
        MockHttpSession admin = login("TEST1", "rhdxhd12", "USR");
        MvcResult result = mockMvc.perform(get(BOARD_LIST_URL).session(admin))
                .andExpect(status().isOk())
                .andReturn();
        List<Object> boards = (List<Object>) result.getModelAndView().getModel().get("resultList");
        assertNotNull(boards);
        assertFalse(boards.isEmpty(), "초기 데이터(V9)의 공지사항 게시판이 목록에 있어야 한다");
    }

    @Test
    void 일반회원이_게시판_설정_URL을_직접_호출해도_막힌다() throws Exception {
        MockHttpSession user = login("USER", "rhdxhd12", "GNR");
        mockMvc.perform(post("/cop/bbs/insertBBSMaster.do").with(csrf()).session(user)
                        .param("bbsNm", "해킹게시판").param("bbsTyCode", "BBST01")
                        .param("fileAtchPosblAt", "N").param("atchPosblFileNumber", "0").param("useAt", "Y"))
                .andExpect(forwardedUrl(ACCESS_DENIED_URL));
        Integer count = jdbcTemplate.queryForObject(
                "select count(1) from comtnbbsmaster where bbs_nm = '해킹게시판'", Integer.class);
        assertEquals(0, count, "일반회원이 게시판을 만들 수 있으면 안 된다");
    }

    @Test
    void 일반회원이_공지사항에_글을_쓰면_DB에_저장된다() throws Exception {
        MockHttpSession user = login("USER", "rhdxhd12", "GNR");
        mockMvc.perform(multipart(ARTICLE_INSERT_URL).with(csrf()).session(user)
                        .param("bbsId", BBS_ID).param("nttSj", "통합테스트 공지").param("nttCn", "본문")
                        .param("noticeAt", "N").param("useAt", "Y"))
                .andExpect(status().is3xxRedirection());

        Integer count = jdbcTemplate.queryForObject(
                "select count(1) from comtnbbs where bbs_id = ? and ntt_sj = '통합테스트 공지'", Integer.class, BBS_ID);
        assertEquals(1, count, "일반회원이 작성한 게시물이 저장되어야 한다");
    }
}

package egovframework.egovwebpractice;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

/**
 * 표준프레임워크 공통컴포넌트(로그인) + Spring Security(인증·권한·CSRF)를 내장 PostgreSQL 에 적용한 상태에서 확인한다.
 * 계정은 공통컴포넌트 초기 데이터(V3, V4): TEST1(업무사용자, ROLE_ADMIN), USER(일반회원, ROLE_USER).
 */
@SpringBootTest(properties = "app.embedded-db.dir=target/test-pg-security")
@AutoConfigureMockMvc
class SecurityIntegrationTest {

    private static final String LOGIN_PAGE = "/uat/uia/egovLoginUsr.do";
    private static final String LOGIN_PAGE_JSP = "/WEB-INF/jsp/egovframework/com/uat/uia/EgovLoginUsr.jsp";
    private static final String LOGIN_ACTION = "/uat/uia/actionLogin.do";
    private static final String LOGIN_PAGE_PATTERN = "**" + LOGIN_PAGE;

    @Autowired
    private MockMvc mockMvc;

    private MockHttpSession login(String id, String password, String userSe) throws Exception {
        MvcResult result = mockMvc.perform(post(LOGIN_ACTION).with(csrf())
                        .param("id", id).param("password", password).param("userSe", userSe))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/main.do"))
                .andReturn();
        return (MockHttpSession) result.getRequest().getSession(false);
    }

    @Test
    void 로그인_화면은_누구나_볼_수_있다() throws Exception {
        // MockMvc 는 JSP 를 렌더링하지 않으므로 이동할 뷰만 확인한다 (실제 렌더링은 java -jar 로 확인)
        mockMvc.perform(get(LOGIN_PAGE))
                .andExpect(status().isOk())
                .andExpect(forwardedUrl(LOGIN_PAGE_JSP));
    }

    @Test
    void 로그인하지_않으면_메인에_접근할_수_없다() throws Exception {
        mockMvc.perform(get("/main.do"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern(LOGIN_PAGE_PATTERN));
    }

    @Test
    void 관리자는_로그인하면_메인에_접근할_수_있다() throws Exception {
        MockHttpSession session = login("TEST1", "rhdxhd12", "USR");
        mockMvc.perform(get("/main.do").session(session))
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("/WEB-INF/jsp/main.jsp"));
    }

    @Test
    void 일반회원도_로그인할_수_있다() throws Exception {
        MockHttpSession session = login("USER", "rhdxhd12", "GNR");
        mockMvc.perform(get("/main.do").session(session)).andExpect(status().isOk());
    }

    @Test
    void 틀린_비밀번호는_로그인되지_않는다() throws Exception {
        MvcResult result = mockMvc.perform(post(LOGIN_ACTION).with(csrf())
                        .param("id", "TEST1").param("password", "wrong-password").param("userSe", "USR"))
                .andReturn();
        MockHttpSession session = (MockHttpSession) result.getRequest().getSession(false);
        mockMvc.perform(get("/main.do").session(session == null ? new MockHttpSession() : session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern(LOGIN_PAGE_PATTERN));
    }

    @Test
    void CSRF_토큰이_없으면_로그인되지_않는다() throws Exception {
        MvcResult result = mockMvc.perform(post(LOGIN_ACTION)
                        .param("id", "TEST1").param("password", "rhdxhd12").param("userSe", "USR"))
                .andReturn();
        MockHttpSession session = (MockHttpSession) result.getRequest().getSession(false);
        mockMvc.perform(get("/main.do").session(session == null ? new MockHttpSession() : session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern(LOGIN_PAGE_PATTERN));
    }

    @Test
    void 관리자_전용_메서드는_관리자만_호출할_수_있다() throws Exception {
        MockHttpSession admin = login("TEST1", "rhdxhd12", "USR");
        mockMvc.perform(get("/test/admin-only.do").session(admin))
                .andExpect(status().isOk())
                .andExpect(content().string("admin-ok"));
    }

    @Test
    void 일반회원은_관리자_전용_메서드에_접근할_수_없다() throws Exception {
        MockHttpSession user = login("USER", "rhdxhd12", "GNR");
        mockMvc.perform(get("/test/admin-only.do").session(user))
                .andExpect(status().isForbidden());
    }

    @Test
    void 비로그인은_관리자_전용_메서드에_접근할_수_없다() throws Exception {
        mockMvc.perform(get("/test/admin-only.do"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern(LOGIN_PAGE_PATTERN));
    }

    @Test
    void 로그아웃하면_다시_접근할_수_없다() throws Exception {
        MockHttpSession session = login("TEST1", "rhdxhd12", "USR");
        mockMvc.perform(post("/uat/uia/actionLogout.do").with(csrf()).session(session))
                .andExpect(status().is3xxRedirection());
        mockMvc.perform(get("/main.do").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern(LOGIN_PAGE_PATTERN));
    }
}

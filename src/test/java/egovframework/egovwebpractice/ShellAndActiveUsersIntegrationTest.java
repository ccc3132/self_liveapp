package egovframework.egovwebpractice;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
 * 사이드바+헤더(틀) 화면(/shell.do)과 접속 중인 사용자 JSON(/main/activeUsers.do).
 * HttpSessionAttributeListener 는 MockMvc(실제 서블릿 컨테이너를 띄우지 않음)에서는 발동하지 않으므로,
 * 접속자 수가 실제로 올라가는지는 이 테스트로 확인하지 못한다 — java -jar 로 브라우저에서 확인했다.
 */
@SpringBootTest(properties = "app.embedded-db.dir=target/test-pg-shell")
@AutoConfigureMockMvc
class ShellAndActiveUsersIntegrationTest {

    private static final String LOGIN_PAGE_PATTERN = "**/uat/uia/egovLoginUsr.do";

    @Autowired
    private MockMvc mockMvc;

    private MockHttpSession login(String id, String password, String userSe) throws Exception {
        MvcResult result = mockMvc.perform(post("/uat/uia/actionLogin.do").with(csrf())
                        .param("id", id).param("password", password).param("userSe", userSe))
                .andReturn();
        return (MockHttpSession) result.getRequest().getSession(false);
    }

    @Test
    void 비로그인은_shell_화면에_접근할_수_없다() throws Exception {
        mockMvc.perform(get("/shell.do"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern(LOGIN_PAGE_PATTERN));
    }

    @Test
    void 로그인하면_shell_화면이_뜬다() throws Exception {
        MockHttpSession user = login("USER", "rhdxhd12", "GNR");
        mockMvc.perform(get("/shell.do").session(user)).andExpect(status().isOk());
    }

    @Test
    void 접속자_목록_json이_응답한다() throws Exception {
        MockHttpSession user = login("USER", "rhdxhd12", "GNR");
        MvcResult result = mockMvc.perform(get("/main/activeUsers.do").session(user))
                .andExpect(status().isOk())
                .andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("\"count\""));
    }
}

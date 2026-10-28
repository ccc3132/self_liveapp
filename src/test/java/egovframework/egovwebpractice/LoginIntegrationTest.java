package egovframework.egovwebpractice;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

/**
 * 표준프레임워크 공통컴포넌트(로그인, uat.uia)를 내장 PostgreSQL 에 적용한 상태에서 로그인 흐름을 확인한다.
 * 계정은 공통컴포넌트 초기 데이터(V3)의 업무사용자 TEST1 이다.
 */
@SpringBootTest(properties = "app.embedded-db.dir=target/test-pg-login")
@AutoConfigureMockMvc
class LoginIntegrationTest {

    private static final String LOGIN_URL = "/uat/uia/actionLogin.do";
    private static final String LOGIN_PAGE = "/uat/uia/egovLoginUsr.do";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void 로그인_화면_JSP로_이동한다() throws Exception {
        // MockMvc 는 JSP 를 렌더링하지 않으므로 이동할 뷰만 확인한다 (실제 렌더링은 java -jar 로 확인)
        mockMvc.perform(get(LOGIN_PAGE))
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("/WEB-INF/jsp/egovframework/com/uat/uia/EgovLoginUsr.jsp"));
    }

    @Test
    void 올바른_계정은_메인으로_이동한다() throws Exception {
        mockMvc.perform(post(LOGIN_URL).param("id", "TEST1").param("password", "rhdxhd12").param("userSe", "USR"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/uat/uia/actionMain.do"));
    }

    @Test
    void 틀린_비밀번호는_로그인_화면으로_돌아온다() throws Exception {
        mockMvc.perform(post(LOGIN_URL).param("id", "TEST1").param("password", "wrong-password").param("userSe", "USR"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(LOGIN_PAGE));
    }

    @Test
    void 로그인하지_않으면_메인에_접근할_수_없다() throws Exception {
        mockMvc.perform(get("/uat/uia/actionMain.do"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(LOGIN_PAGE));
    }
}

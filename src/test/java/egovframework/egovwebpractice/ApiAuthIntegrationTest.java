package egovframework.egovwebpractice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

/**
 * 모바일 앱(React Native 등)용 REST API(/api/**) 의 JWT 로그인·인증을 확인한다.
 * 기존 웹(JSP)의 세션 기반 로그인과는 별개의 보안 체인({@code ApiSecurityConfig})이다.
 */
@SpringBootTest(properties = "app.embedded-db.dir=target/test-pg-api-auth")
@AutoConfigureMockMvc
class ApiAuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private String login(String id, String password, String userSe) throws Exception {
        String body = objectMapper.writeValueAsString(new java.util.HashMap<>() {
            {
                put("id", id);
                put("password", password);
                put("userSe", userSe);
            }
        });
        MvcResult result = mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        return json.get("token").asText();
    }

    @Test
    void 올바른_정보로_로그인하면_토큰을_받는다() throws Exception {
        String token = login("TEST1", "rhdxhd12", "USR");
        assertNotNull(token);
        assertTrue(token.length() > 10);
    }

    @Test
    void 틀린_비밀번호는_401을_반환한다() throws Exception {
        String body = objectMapper.writeValueAsString(new java.util.HashMap<>() {
            {
                put("id", "TEST1");
                put("password", "wrong-password");
                put("userSe", "USR");
            }
        });
        mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void 토큰없이_보호된_API에_접근할_수_없다() throws Exception {
        mockMvc.perform(get("/api/dashboard")).andExpect(status().isForbidden());
    }

    @Test
    void 토큰으로_내정보_조회와_대시보드_조회가_된다() throws Exception {
        String token = login("TEST1", "rhdxhd12", "USR");

        MvcResult me = mockMvc.perform(get("/api/auth/me").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode meJson = objectMapper.readTree(me.getResponse().getContentAsString());
        assertEquals("TEST1", meJson.get("id").asText());

        mockMvc.perform(get("/api/dashboard").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }
}

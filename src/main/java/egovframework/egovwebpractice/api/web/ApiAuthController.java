package egovframework.egovwebpractice.api.web;

import egovframework.com.cmm.LoginVO;
import egovframework.egovwebpractice.api.jwt.JwtTokenProvider;
import egovframework.com.uat.uia.service.EgovLoginService;
import org.egovframe.rte.fdl.security.config.EgovIdSaltSha256PasswordEncoder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 모바일 앱(React Native 등) 전용 로그인 API. 기존 웹 로그인(/uat/uia/actionLogin.do)과
 * 같은 사용자 DB·같은 비밀번호 해시(EgovIdSaltSha256PasswordEncoder)를 쓰지만, 세션 쿠키 대신
 * JWT 를 발급한다.
 */
@RestController
public class ApiAuthController {

    private final EgovLoginService loginService;
    private final UserDetailsService userDetailsService;
    private final EgovIdSaltSha256PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    // PasswordEncoder 빈이 인터페이스 반환형으로 등록돼 구체 타입으로 직접 주입받을 수 없어 캐스팅한다.
    public ApiAuthController(EgovLoginService loginService, UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder, JwtTokenProvider tokenProvider) {
        this.loginService = loginService;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = (EgovIdSaltSha256PasswordEncoder) passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    public record LoginRequest(String id, String password, String userSe) {
    }

    public record LoginResponse(String token, String id, String name, String orgnztNm, String userSe) {
    }

    @PostMapping("/api/auth/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        if (request.id() == null || request.password() == null) {
            return ResponseEntity.badRequest().body("id/password가 필요합니다.");
        }
        String userSe = request.userSe() == null || request.userSe().isBlank() ? "GNR" : request.userSe();
        String username = userSe + request.id();

        UserDetails userDetails;
        try {
            userDetails = userDetailsService.loadUserByUsername(username);
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(401).body("아이디 또는 비밀번호가 올바르지 않습니다.");
        }
        if (!passwordEncoder.matches(request.password(), userDetails.getPassword(), userDetails.getUsername())) {
            return ResponseEntity.status(401).body("아이디 또는 비밀번호가 올바르지 않습니다.");
        }

        try {
            LoginVO query = new LoginVO();
            query.setId(request.id());
            query.setPassword(request.password());
            query.setUserSe(userSe);
            LoginVO user = loginService.actionLogin(query);
            if (user == null || user.getId() == null || user.getId().isEmpty()) {
                return ResponseEntity.status(401).body("아이디 또는 비밀번호가 올바르지 않습니다.");
            }
            String token = tokenProvider.createToken(username, user, userDetails.getAuthorities());
            return ResponseEntity.ok(new LoginResponse(token, user.getId(), user.getName(), user.getOrgnztNm(), user.getUserSe()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("로그인 처리 중 오류가 발생했습니다.");
        }
    }

    @GetMapping("/api/auth/me")
    public LoginVO me(@AuthenticationPrincipal LoginVO authenticatedUser) {
        return authenticatedUser;
    }
}

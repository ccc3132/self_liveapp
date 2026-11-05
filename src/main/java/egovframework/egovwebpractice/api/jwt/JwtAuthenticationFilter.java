package egovframework.egovwebpractice.api.jwt;

import egovframework.com.cmm.LoginVO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.lang.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * /api/** 요청의 Authorization: Bearer 헤더에서 JWT 를 읽어 인증한다.
 * 토큰의 claim 으로 LoginVO 를 바로 구성해 SecurityContext 의 principal 로 쓴다
 * (세션이 없으므로 기존 웹 로그인의 세션 기반 egov 헬퍼는 쓰지 않는다).
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring("Bearer ".length());
            try {
                Claims claims = tokenProvider.parseClaims(token);
                LoginVO user = new LoginVO();
                user.setId(claims.get("id", String.class));
                user.setUserSe(claims.get("userSe", String.class));
                user.setName(claims.get("name", String.class));
                user.setOrgnztNm(claims.get("orgnztNm", String.class));
                user.setUniqId(claims.get("uniqId", String.class));

                List<?> rawRoles = claims.get("roles", List.class);
                List<GrantedAuthority> authorities = rawRoles == null ? List.of()
                        : rawRoles.stream().map(r -> (GrantedAuthority) new SimpleGrantedAuthority(String.valueOf(r))).toList();
                var authentication = new UsernamePasswordAuthenticationToken(user, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (JwtException | IllegalArgumentException e) {
                // 토큰이 유효하지 않으면 인증 없이 통과시킨다 - 이후 권한 검사에서 401/403 으로 처리된다.
                SecurityContextHolder.clearContext();
            }
        }
        filterChain.doFilter(request, response);
    }
}

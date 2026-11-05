package egovframework.egovwebpractice.api.jwt;

import egovframework.com.cmm.LoginVO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

/**
 * 모바일 앱(React Native 등)용 REST API(/api/**) 인증에 쓰는 JWT 를 발급·검증한다.
 * 기존 웹(JSP)의 세션 기반 로그인과는 완전히 별개이며, API 요청은 상태 없이(stateless)
 * 이 토큰의 Authorization: Bearer 헤더만으로 인증한다.
 */
@Component
public class JwtTokenProvider {

    private final SecretKey key;
    private final Duration expiration;

    public JwtTokenProvider(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration-minutes}") long expirationMinutes) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expiration = Duration.ofMinutes(expirationMinutes);
    }

    /**
     * userSe+id(예: USRTEST1)를 subject 로, 화면에 필요한 사용자 정보와 DB 에 지정된 권한(roles)을
     * claim 으로 담은 토큰을 만든다. 권한은 로그인 시점에 {@code UserDetailsService} 로 조회한 실제
     * 권한을 그대로 넣는다(웹 로그인과 동일한 COMTNEMPLYRSCRTYESTBS 기반 권한).
     */
    public String createToken(String username, LoginVO user, Collection<? extends GrantedAuthority> authorities) {
        Instant now = Instant.now();
        List<String> roles = authorities.stream().map(GrantedAuthority::getAuthority).toList();
        return Jwts.builder()
                .subject(username)
                .claim("id", user.getId())
                .claim("userSe", user.getUserSe())
                .claim("name", user.getName())
                .claim("orgnztNm", user.getOrgnztNm())
                .claim("uniqId", user.getUniqId())
                .claim("roles", roles)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(expiration)))
                .signWith(key)
                .compact();
    }

    /** 토큰을 검증하고 claim 을 반환한다. 서명이 틀렸거나 만료된 경우 JwtException 을 던진다. */
    public Claims parseClaims(String token) throws JwtException {
        return Jwts.parser().verifyWith(key).build()
                .parseSignedClaims(token).getPayload();
    }
}

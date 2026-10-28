package egovframework.com.sec.security.filter;

import java.io.IOException;

import org.egovframe.rte.fdl.security.config.EgovSecurityConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Servlet ApplicationContext용 Spring Security 필터 체인 및 CSRF 토큰 노출 필터 구성.
 * <p>
 * Root Context의 {@link org.egovframe.rte.fdl.security.config.EgovSecurityConfiguration}에서
 * 생성한 {@link SecurityFilterChain}(CSRF 포함)을 {@link FilterChainProxy}로 노출하고,
 * {@code csrf=true}일 때 CSRF 토큰을 JSP/AJAX에서 사용할 수 있도록 {@code _csrf} 요청 속성으로 노출한다.
 * </p>
 */
@Configuration
public class EgovCsrfSecurityConfig {

	// [egov-web-practice] 원본의 springSecurityFilterChain(FilterChainProxy) 빈은 제거했다.
	// Spring Boot 에서는 @EnableWebSecurity 가 SecurityFilterChain 으로 같은 이름의 빈을 만들기 때문이다.

	@Bean(name = "egovCsrfTokenAttributeFilter")
	public Filter egovCsrfTokenAttributeFilter(EgovSecurityConfig egovSecurityConfig) {
		return new CsrfTokenAttributeFilter(egovSecurityConfig);
	}

	private static final class CsrfTokenAttributeFilter extends OncePerRequestFilter {

		private final EgovSecurityConfig egovSecurityConfig;
		private final HttpSessionCsrfTokenRepository csrfTokenRepository = new HttpSessionCsrfTokenRepository();

		private CsrfTokenAttributeFilter(EgovSecurityConfig egovSecurityConfig) {
			this.egovSecurityConfig = egovSecurityConfig;
		}

		@Override
		protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
				throws ServletException, IOException {

			if (egovSecurityConfig.isCsrf()) {
				exposeCsrfToken(request, csrfTokenRepository);
			}

			filterChain.doFilter(request, response);
		}

		private static void exposeCsrfToken(HttpServletRequest request, HttpSessionCsrfTokenRepository repository) {
			CsrfToken csrfToken = resolveCsrfToken(request, repository);
			if (csrfToken != null) {
				request.setAttribute("_csrf", csrfToken);
				request.setAttribute(csrfToken.getParameterName(), csrfToken);
				request.setAttribute(CsrfToken.class.getName(), csrfToken);
			}
		}

		private static CsrfToken resolveCsrfToken(HttpServletRequest request, HttpSessionCsrfTokenRepository repository) {
			Object csrfAttribute = request.getAttribute("_csrf");
			if (csrfAttribute instanceof CsrfToken token) {
				return token;
			}

			Object tokenAttribute = request.getAttribute(CsrfToken.class.getName());
			if (tokenAttribute instanceof CsrfToken token) {
				return token;
			}

			return repository.loadToken(request);
		}
	}

}

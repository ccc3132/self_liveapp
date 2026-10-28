package egovframework.egovwebpractice.config;

import egovframework.com.cmm.EgovComExcepHndlr;
import egovframework.com.cmm.EgovComOthersExcepHndlr;
import egovframework.com.cmm.util.EgovAdminAuthorizationAspect;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.egovframe.rte.fdl.cmmn.aspect.ExceptionTransfer;
import org.egovframe.rte.fdl.cmmn.exception.handler.ExceptionHandler;
import org.egovframe.rte.fdl.cmmn.exception.manager.DefaultExceptionHandleManager;
import org.egovframe.rte.fdl.cmmn.exception.manager.ExceptionHandlerService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.AntPathMatcher;

/**
 * 공통컴포넌트의 AOP 설정(context-aspect.xml)을 Spring Boot 용 Java Config 로 옮긴 것.
 * - {@code @RequireAdmin} 이 붙은 메서드는 관리자(ROLE_ADMIN)만 호출할 수 있다.
 * - 서비스 구현 클래스(egovframework.com..impl.*Impl)에서 예외가 발생하면 실행환경의 ExceptionTransfer 가 처리한다.
 */
@Aspect
@Configuration
public class CommonComponentsAspect {

    private final ExceptionTransfer exceptionTransfer;
    private final EgovAdminAuthorizationAspect adminAuthorization = new EgovAdminAuthorizationAspect();

    public CommonComponentsAspect(ExceptionTransfer exceptionTransfer) {
        this.exceptionTransfer = exceptionTransfer;
    }

    @Before("@annotation(egovframework.com.cmm.annotation.RequireAdmin)")
    public void assertAdmin() {
        adminAuthorization.assertAdmin();
    }

    @AfterThrowing(pointcut = "execution(* egovframework.com..impl.*Impl.*(..))", throwing = "exception")
    public void transfer(JoinPoint joinPoint, Exception exception) throws Exception {
        exceptionTransfer.transfer(joinPoint, exception);
    }

    @Bean(name = "egov.exceptionTransfer")
    public static ExceptionTransfer exceptionTransfer(AntPathMatcher antPathMater) {
        ExceptionTransfer transfer = new ExceptionTransfer();
        transfer.setExceptionHandlerService(new ExceptionHandlerService[] {
                handleManager(antPathMater, new EgovComExcepHndlr()),
                handleManager(antPathMater, new EgovComOthersExcepHndlr())});
        return transfer;
    }

    private static DefaultExceptionHandleManager handleManager(AntPathMatcher matcher, ExceptionHandler handler) {
        DefaultExceptionHandleManager manager = new DefaultExceptionHandleManager();
        manager.setReqExpMatcher(matcher);
        manager.setPatterns(new String[] {"**service.impl.*"});
        manager.setHandlers(new ExceptionHandler[] {handler});
        return manager;
    }
}

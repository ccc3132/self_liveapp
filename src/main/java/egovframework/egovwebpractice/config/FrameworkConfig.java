package egovframework.egovwebpractice.config;

import org.egovframe.rte.fdl.cmmn.trace.LeaveaTrace;
import org.egovframe.rte.fdl.cmmn.trace.handler.DefaultTraceHandler;
import org.egovframe.rte.fdl.cmmn.trace.handler.TraceHandler;
import org.egovframe.rte.fdl.cmmn.trace.manager.DefaultTraceHandleManager;
import org.egovframe.rte.fdl.cmmn.trace.manager.TraceHandlerService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.AntPathMatcher;

/**
 * 표준프레임워크 실행환경 설정 (Java Config 방식).
 * - EgovAbstractServiceImpl 이 사용하는 leaveaTrace 빈
 * - 어노테이션 기반 트랜잭션(@Transactional)
 * 커넥션 풀은 application.yml 의 spring.datasource.hikari 에서 설정한다.
 */
@Configuration
@EnableTransactionManagement
public class FrameworkConfig {

    @Bean
    public AntPathMatcher antPathMater() {
        return new AntPathMatcher();
    }

    @Bean
    public DefaultTraceHandler defaultTraceHandler() {
        return new DefaultTraceHandler();
    }

    @Bean
    public TraceHandlerService traceHandlerService(AntPathMatcher antPathMater, DefaultTraceHandler defaultTraceHandler) {
        DefaultTraceHandleManager manager = new DefaultTraceHandleManager();
        manager.setReqExpMatcher(antPathMater);
        manager.setPatterns(new String[] {"*"});
        manager.setHandlers(new TraceHandler[] {defaultTraceHandler});
        return manager;
    }

    @Bean
    public LeaveaTrace leaveaTrace(TraceHandlerService traceHandlerService) {
        LeaveaTrace trace = new LeaveaTrace();
        trace.setTraceHandlerServices(new TraceHandlerService[] {traceHandlerService});
        return trace;
    }
}

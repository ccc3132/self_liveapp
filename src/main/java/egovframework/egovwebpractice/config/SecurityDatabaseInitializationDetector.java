package egovframework.egovwebpractice.config;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.sql.init.dependency.DependsOnDatabaseInitializationDetector;

/**
 * 표준프레임워크 보안 모듈의 일부 빈(권한 계층, URL 권한 등)은 생성될 때 DB 테이블을 바로 조회한다.
 * Flyway 마이그레이션보다 먼저 만들어지면 새 DB 에서는 테이블이 없어 앱이 뜨지 않으므로,
 * 이 빈들이 DB 초기화(Flyway) 이후에 만들어지도록 Spring Boot 에 알려준다.
 * (META-INF/spring.factories 에 등록)
 */
public class SecurityDatabaseInitializationDetector implements DependsOnDatabaseInitializationDetector {

    private static final List<String> BEANS_THAT_QUERY_DB_AT_STARTUP = List.of(
            "hierarchyStrings",
            "roleHierarchy",
            "securedObjectDAO",
            "securedObjectService",
            "requestMap",
            "databaseSecurityMetadataSource",
            "jdbcUserService",
            "securityFilterChain");

    @Override
    public Set<String> detect(ConfigurableListableBeanFactory beanFactory) {
        Set<String> detected = new HashSet<>();
        for (String name : BEANS_THAT_QUERY_DB_AT_STARTUP) {
            if (beanFactory.containsBeanDefinition(name)) {
                detected.add(name);
            }
        }
        return detected;
    }
}

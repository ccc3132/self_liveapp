package egovframework.egovwebpractice.config;

import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

/**
 * 운영(prod 등 local 이 아닌 프로필): spring.datasource.* 로 외부 PostgreSQL 에 연결한다.
 * 보안 모듈이 DataSource 타입 빈을 하나 더 만들기 때문에, Boot 의 자동 DataSource 대신 기본 DataSource 를 명시한다.
 */
@Configuration
@Profile("!local")
public class ExternalDataSourceConfig {

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource.hikari")
    public DataSource dataSource(DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }
}

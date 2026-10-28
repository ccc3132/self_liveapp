package egovframework.egovwebpractice.config;

import java.util.Properties;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.mapping.VendorDatabaseIdProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 접속한 DB 종류를 databaseId 로 인식한다. 매퍼 XML 에서 databaseId="postgresql" 처럼
 * DB마다 달라지는 SQL 만 따로 지정할 수 있다.
 */
@Configuration
public class MyBatisConfig {

    @Bean
    public DatabaseIdProvider databaseIdProvider() {
        Properties vendors = new Properties();
        vendors.setProperty("PostgreSQL", "postgresql");
        vendors.setProperty("MariaDB", "mariadb");
        VendorDatabaseIdProvider provider = new VendorDatabaseIdProvider();
        provider.setProperties(vendors);
        return provider;
    }
}

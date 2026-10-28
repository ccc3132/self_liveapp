package egovframework.egovwebpractice.config;

import com.zaxxer.hikari.HikariDataSource;
import io.zonky.test.db.postgres.embedded.EmbeddedPostgres;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * local 프로필 전용: 별도 설치 없이 실제 PostgreSQL 을 내장해서 띄운다.
 * 운영(prod) 프로필은 spring.datasource.* 로 외부 PostgreSQL 에 연결한다.
 */
@Configuration
@Profile("local")
public class EmbeddedPostgresConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmbeddedPostgresConfig.class);
    private static final long EXIT_WAIT_SECONDS = 10L;

    @Bean(destroyMethod = "close")
    public EmbeddedPostgres embeddedPostgres(
            @Value("${app.embedded-db.dir:./data/pg}") String dir,
            @Value("${app.embedded-db.port:0}") int port) throws IOException {
        Path dataDir = Paths.get(dir).toAbsolutePath();
        stopLeftoverPostgres(dataDir);
        return EmbeddedPostgres.builder()
                .setDataDirectory(dataDir)
                .setCleanDataDirectory(false)
                .setPort(port)
                .start();
    }

    @Bean
    @ConfigurationProperties("spring.datasource.hikari")
    public DataSource dataSource(EmbeddedPostgres embeddedPostgres) {
        return DataSourceBuilder.create()
                .type(HikariDataSource.class)
                .url(embeddedPostgres.getJdbcUrl("postgres", "postgres"))
                .username("postgres")
                .build();
    }

    /**
     * 앱이 강제 종료되면 이전 PostgreSQL 이 같은 데이터 폴더를 잡은 채 남아서 다음 시작이 실패한다.
     * 데이터 폴더의 postmaster.pid 가 가리키는 프로세스가 postgres 일 때만, 그 프로세스와 자식을 종료한다.
     * (강제 종료여도 PostgreSQL 은 다음 시작 때 WAL 로 복구하므로 데이터는 유지된다.)
     */
    private static void stopLeftoverPostgres(Path dataDir) {
        Path pidFile = dataDir.resolve("postmaster.pid");
        if (!Files.isRegularFile(pidFile)) {
            return;
        }
        try {
            long pid = Long.parseLong(Files.readAllLines(pidFile).get(0).trim());
            Optional<ProcessHandle> leftover = ProcessHandle.of(pid)
                    .filter(h -> h.info().command().map(c -> c.toLowerCase().contains("postgres")).orElse(false));
            if (leftover.isEmpty()) {
                return;
            }
            LOGGER.warn("남아 있는 PostgreSQL 프로세스(PID {})와 자식 프로세스를 종료합니다.", pid);
            List<ProcessHandle> tree = new ArrayList<>();
            leftover.get().descendants().forEach(tree::add);
            tree.add(leftover.get());
            tree.forEach(ProcessHandle::destroyForcibly);
            for (ProcessHandle p : tree) {
                p.onExit().get(EXIT_WAIT_SECONDS, TimeUnit.SECONDS);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            LOGGER.warn("남아 있는 PostgreSQL 프로세스를 정리하지 못했습니다: {}", e.getMessage());
        }
    }
}

package egovframework.egovwebpractice.config;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.util.StringUtils;

/**
 * 표준프레임워크 암호화 서비스(egovEnvCryptoService)는 기본 키("egovframe")가 공개되어 있어, 그대로 쓰면
 * 실행환경이 "기본 키를 반드시 바꾸라"는 치명 경고를 낸다. 저장소에 비밀 키를 넣을 수 없으므로,
 * 첫 실행 때 무작위 키를 만들어 설정 파일(기본 ./data/egov-crypto-config.properties)로 저장하고 그것을 사용한다.
 *
 * - 운영에서 직접 관리하려면 환경변수 APP_CRYPTO_CONFIG_FILE 로 파일 위치를 지정한다(없으면 생성).
 * - 여러 서버가 같은 암호화 ID 를 주고받아야 하면 같은 파일을 공유한다.
 * - algorithmKeyHash 는 base64(SHA-256(algorithmKey)) 이다.
 */
public class CryptoConfigEnvironmentPostProcessor implements EnvironmentPostProcessor {

    private static final String CONFIG_PATH_PROPERTY = "Globals.CryptoConfigPath";
    private static final String FILE_PROPERTY = "app.crypto.config-file";
    private static final String DEFAULT_FILE = "./data/egov-crypto-config.properties";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        if (StringUtils.hasText(environment.getProperty(CONFIG_PATH_PROPERTY))) {
            return; // 이미 직접 지정됨
        }
        Path file = Paths.get(environment.getProperty(FILE_PROPERTY, DEFAULT_FILE)).toAbsolutePath().normalize();
        if (!Files.exists(file)) {
            create(file);
        }
        String location = "file:" + file.toString().replace(String.valueOf((char) 92), "/"); // 윈도우 경로 구분자(역슬래시) 변환
        environment.getPropertySources().addFirst(
                new MapPropertySource("egovCryptoConfigLocation", Map.of(CONFIG_PATH_PROPERTY, location)));
    }

    private static void create(Path file) {
        byte[] random = new byte[32];
        new SecureRandom().nextBytes(random);
        String key = Base64.getUrlEncoder().withoutPadding().encodeToString(random);
        String content = String.join("\n",
                "# 자동 생성된 암호화 설정입니다. 이 파일은 비밀 키를 포함하므로 git 에 올리지 마세요.",
                "id = egovCryptoConfig",
                "initial = true",
                "crypto = true",
                "algorithm = SHA-256",
                "algorithmKey = " + key,
                "algorithmKeyHash = " + sha256Base64(key),
                "cryptoBlockSize = 1024",
                "cryptoPropertyLocation = classpath:/egovframework/egovProps/globals.properties",
                "plainDigest = false",
                "");
        try {
            Files.createDirectories(file.getParent());
            Files.writeString(file, content, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("암호화 설정 파일을 만들 수 없습니다: " + file, e);
        }
    }

    private static String sha256Base64(String value) {
        try {
            return Base64.getEncoder().encodeToString(
                    MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}

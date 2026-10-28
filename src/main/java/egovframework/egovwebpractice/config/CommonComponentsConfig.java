package egovframework.egovwebpractice.config;

import egovframework.com.cmm.EgovMessageSource;
import egovframework.com.cmm.config.EgovLoginConfig;
import egovframework.com.cmm.service.impl.EgovUserDetailsSessionServiceImpl;
import egovframework.com.cmm.util.EgovUserDetailsHelper;
import egovframework.com.cop.ems.service.EgovMultiPartEmail;
import java.nio.charset.StandardCharsets;
import javax.sql.DataSource;
import org.egovframe.rte.fdl.idgnr.impl.EgovTableIdGnrServiceImpl;
import org.egovframe.rte.fdl.idgnr.impl.strategy.EgovIdGnrStrategyImpl;
import org.egovframe.rte.psl.dataaccess.util.EgovMap;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

/**
 * 공통컴포넌트(egovframework.com)가 XML(context-*.xml)로 정의하던 빈을 Spring Boot 에서 쓰도록 Java Config 로 옮긴 것.
 * 원본: context-common.xml, context-config.xml, context-egovuserdetailshelper.xml, context-mapper.xml,
 *       context-mail.xml, idgn/context-idgn-MailMsg.xml
 */
@Configuration
@PropertySource("classpath:egovframework/egovProps/globals.properties")
@ImportResource("classpath:egovframework/spring/context-com-alias.xml")
public class CommonComponentsConfig {

    /** 메시지: 공통컴포넌트 메시지 + 이 프로젝트 메시지(messages/message). 빈 이름은 messageSource 여야 한다. */
    @Bean(name = "messageSource")
    public ReloadableResourceBundleMessageSource messageSource() {
        ReloadableResourceBundleMessageSource source = new ReloadableResourceBundleMessageSource();
        source.setBasenames(
                "classpath:messages/message",
                "classpath:egovframework/message/com/message-common",
                "classpath:egovframework/message/com/message-validation",
                "classpath:egovframework/message/com/cmm/message-common",
                "classpath:egovframework/message/com/uat/uia/message",
                "classpath:egovframework/message/com/uat/uap/message",
                "classpath:org/egovframe/rte/fdl/idgnr/messages/idgnr",
                "classpath:org/egovframe/rte/fdl/property/messages/properties");
        source.setDefaultEncoding(StandardCharsets.UTF_8.name());
        source.setFallbackToSystemLocale(false);
        source.setCacheSeconds(60);
        return source;
    }

    @Bean(name = "egovMessageSource")
    public EgovMessageSource egovMessageSource(ReloadableResourceBundleMessageSource messageSource) {
        EgovMessageSource egovMessageSource = new EgovMessageSource();
        egovMessageSource.setReloadableResourceBundleMessageSource(messageSource);
        return egovMessageSource;
    }

    /** 로그인 인증 제한(연속 실패 횟수 초과 시 잠금) */
    @Bean(name = "egovLoginConfig")
    public EgovLoginConfig egovLoginConfig(
            @Value("${Globals.login.Lock:false}") boolean lock,
            @Value("${Globals.login.LockCount:0}") int lockCount) {
        EgovLoginConfig config = new EgovLoginConfig();
        config.setLock(lock);
        config.setLockCount(lockCount);
        return config;
    }

    /** 인증 사용자 조회 헬퍼 (Globals.Auth=session 방식) */
    @Bean(name = "egovUserDetailsHelper")
    public EgovUserDetailsHelper egovUserDetailsHelper() {
        EgovUserDetailsHelper helper = new EgovUserDetailsHelper();
        helper.setEgovUserDetailsService(new EgovUserDetailsSessionServiceImpl());
        return helper;
    }

    /** 발송메일 메시지 ID 생성기 (COMTECOPSEQ 테이블 사용) */
    @Bean(name = "mailMsgtrategy")
    public EgovIdGnrStrategyImpl mailMsgStrategy() {
        EgovIdGnrStrategyImpl strategy = new EgovIdGnrStrategyImpl();
        strategy.setPrefix("MAILMSG_");
        strategy.setCipers(12);
        strategy.setFillChar('0');
        return strategy;
    }

    @Bean(name = "egovMailMsgIdGnrService", destroyMethod = "destroy")
    public EgovTableIdGnrServiceImpl egovMailMsgIdGnrService(DataSource dataSource, EgovIdGnrStrategyImpl mailMsgtrategy) {
        EgovTableIdGnrServiceImpl service = new EgovTableIdGnrServiceImpl();
        service.setDataSource(dataSource);
        service.setStrategy(mailMsgtrategy);
        service.setBlockSize(10);
        service.setTable("COMTECOPSEQ");
        service.setTableName("MAILMSG_ID");
        return service;
    }

    /** 외부 SMTP 로 메일 발송. 값은 환경변수(MAIL_*)로 주입한다. */
    @Bean(name = "egovMultiPartEmail")
    public EgovMultiPartEmail egovMultiPartEmail(
            @Value("${app.mail.host:}") String host,
            @Value("${app.mail.port:587}") int port,
            @Value("${app.mail.username:}") String username,
            @Value("${app.mail.password:}") String password,
            @Value("${app.mail.sender-name:System}") String senderName,
            @Value("${app.mail.sender-address:}") String senderAddress) {
        EgovMultiPartEmail email = new EgovMultiPartEmail();
        email.setHost(host);
        email.setPort(port);
        email.setId(username);
        email.setPassword(password);
        email.setSenderName(senderName);
        email.setEmailAddress(senderAddress);
        return email;
    }

    /** 공통컴포넌트 mapper-config.xml 의 설정을 Boot 의 MyBatis 설정에 반영한다. */
    @Bean
    public ConfigurationCustomizer commonComponentsMyBatisCustomizer() {
        return configuration -> {
            configuration.setJdbcTypeForNull(org.apache.ibatis.type.JdbcType.VARCHAR);
            configuration.getTypeAliasRegistry().registerAlias("egovMap", EgovMap.class);
            configuration.getTypeAliasRegistry().registerAlias("FileVO", egovframework.com.cmm.service.FileVO.class);
            configuration.getTypeAliasRegistry().registerAlias("ComDefaultCodeVO", egovframework.com.cmm.ComDefaultCodeVO.class);
            configuration.getTypeAliasRegistry().registerAlias("comDefaultVO", egovframework.com.cmm.ComDefaultVO.class);
        };
    }
}

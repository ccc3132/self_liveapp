package egovframework.egovwebpractice.config;

import egovframework.com.cmm.EgovMessageSource;
import egovframework.com.cmm.ImagePaginationRenderer;
import egovframework.com.cmm.config.EgovLoginConfig;
import egovframework.com.cmm.util.EgovUserDetailsHelper;
import egovframework.com.cop.ems.service.EgovMultiPartEmail;
import egovframework.com.sec.ram.service.impl.EgovUserDetailsSecurityServiceImpl;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.egovframe.rte.fdl.crypto.config.EgovCryptoConfiguration;
import org.egovframe.rte.fdl.idgnr.impl.EgovTableIdGnrServiceImpl;
import org.egovframe.rte.fdl.idgnr.impl.strategy.EgovIdGnrStrategyImpl;
import org.egovframe.rte.fdl.property.impl.EgovPropertyServiceImpl;
import org.egovframe.rte.psl.dataaccess.util.EgovMap;
import org.egovframe.rte.ptl.mvc.tags.ui.pagination.DefaultPaginationManager;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
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
@Import(EgovCryptoConfiguration.class) // egovEnvCryptoService 등 암호화 빈 (egov-crypto-config.properties)
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
                "classpath:egovframework/message/com/cmm/err/message-common",
                "classpath:egovframework/message/com/uat/uia/message",
                "classpath:egovframework/message/com/uat/uap/message",
                "classpath:egovframework/message/com/uss/umt/message",
                "classpath:messages/message-validation", // 실행환경 ptl-reactive 검증 메시지(validation.pwdCheck 등)
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

    /** 인증 사용자·권한 조회 서비스 (Globals.Auth=security, Spring Security 의 인증 정보를 사용) */
    @Bean(name = "egovUserDetailsSecurityService")
    public EgovUserDetailsSecurityServiceImpl egovUserDetailsSecurityService() {
        return new EgovUserDetailsSecurityServiceImpl();
    }

    /** 인증 사용자 조회 헬퍼 */
    @Bean(name = "egovUserDetailsHelper")
    public EgovUserDetailsHelper egovUserDetailsHelper(EgovUserDetailsSecurityServiceImpl egovUserDetailsSecurityService) {
        EgovUserDetailsHelper helper = new EgovUserDetailsHelper();
        helper.setEgovUserDetailsService(egovUserDetailsSecurityService);
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

    /** 속성 서비스: 페이지당 건수 등 (원본 context-properties.xml) */
    @Bean(name = "propertiesService", destroyMethod = "destroy")
    public EgovPropertyServiceImpl propertiesService(
            @Value("${Globals.fileUpload.maxRequestSize:104857600}") String maxRequestSize,
            @Value("${Globals.fileStorePath:./data/upload/}") String fileStorePath) {
        EgovPropertyServiceImpl service = new EgovPropertyServiceImpl();
        service.setProperties(Map.of(
                "pageUnit", "10",
                "pageSize", "10",
                "posblAtchFileSize", maxRequestSize,
                "Globals.fileStorePath", fileStorePath,
                "Globals.addedOptions", "false"));
        return service;
    }

    /** 업무사용자 ID(USRCNFRM_...) 생성기 (원본 idgn/context-idgn-UsrCnfrm.xml) */
    @Bean(name = "usrCnfrmStrategy")
    public EgovIdGnrStrategyImpl usrCnfrmStrategy() {
        EgovIdGnrStrategyImpl strategy = new EgovIdGnrStrategyImpl();
        strategy.setPrefix("USRCNFRM_");
        strategy.setCipers(11);
        strategy.setFillChar('0');
        return strategy;
    }

    @Bean(name = "egovUsrCnfrmIdGnrService", destroyMethod = "destroy")
    public EgovTableIdGnrServiceImpl egovUsrCnfrmIdGnrService(DataSource dataSource, EgovIdGnrStrategyImpl usrCnfrmStrategy) {
        EgovTableIdGnrServiceImpl service = new EgovTableIdGnrServiceImpl();
        service.setDataSource(dataSource);
        service.setStrategy(usrCnfrmStrategy);
        service.setBlockSize(10);
        service.setTable("COMTECOPSEQ");
        service.setTableName("USRCNFRM_ID");
        return service;
    }

    /** 화면 이동(EgovPageLink.do) 허용 목록과, 로그인 후 이동 허용 URL 목록 (원본 context-whitelist.xml) */
    @Bean(name = "egovPageLinkWhitelist")
    public List<String> egovPageLinkWhitelist() {
        return List.of(
                "/egovframework/com/sym/mnu/stm/EgovSiteMap",
                "/cmm/sym/mpm/EgovSiteMap",
                "/egovframework/com/main_bottom",
                "utl/sys/wsi/EgovWebStandardInspectionUri");
    }

    @Bean(name = "egovNextUrlWhitelist")
    public List<String> egovNextUrlWhitelist() {
        return List.of(
                "/uss/umt/EgovMberSbscrbView.do",
                "/uss/umt/EgovEntrprsSbscrbView.do",
                "/uss/olp/cns/CnsltDtlsRegistView.do");
    }

    /** 목록 화면의 페이지 번호 표시(<ui:pagination type="image">) (원본 egov-com-servlet.xml) */
    @Bean(name = "imageRenderer")
    public ImagePaginationRenderer imageRenderer() {
        return new ImagePaginationRenderer();
    }

    @Bean(name = "paginationManager")
    public DefaultPaginationManager paginationManager(ImagePaginationRenderer imageRenderer) {
        DefaultPaginationManager manager = new DefaultPaginationManager();
        manager.setRendererType(Map.of("image", imageRenderer));
        return manager;
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

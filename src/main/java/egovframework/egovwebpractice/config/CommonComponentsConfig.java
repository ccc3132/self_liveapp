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
                "classpath:egovframework/message/com/sec/rgm/message",
                "classpath:egovframework/message/com/cop/bbs/message",
                "classpath:egovframework/message/com/cop/smt/djm/message",
                "classpath:egovframework/message/com/cop/smt/dsm/message",
                "classpath:egovframework/message/com/cop/smt/lsm/message",
                "classpath:egovframework/message/com/cop/smt/mrm/message",
                "classpath:egovframework/message/com/cop/smt/mtm/message",
                "classpath:egovframework/message/com/cop/smt/sam/message",
                "classpath:egovframework/message/com/cop/smt/sdm/message",
                "classpath:egovframework/message/com/cop/smt/sim/message",
                "classpath:egovframework/message/com/cop/smt/wmr/message",
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

    /** {@code idgn/context-idgn-*.xml} 패턴의 ID 생성기 빈을 코드로 만드는 공통 도우미. */
    private EgovTableIdGnrServiceImpl idGnrService(DataSource dataSource, String prefix, int cipers, String tableName) {
        EgovIdGnrStrategyImpl strategy = new EgovIdGnrStrategyImpl();
        strategy.setPrefix(prefix);
        strategy.setCipers(cipers);
        strategy.setFillChar('0');
        EgovTableIdGnrServiceImpl service = new EgovTableIdGnrServiceImpl();
        service.setDataSource(dataSource);
        service.setStrategy(strategy);
        service.setBlockSize(10);
        service.setTable("COMTECOPSEQ");
        service.setTableName(tableName);
        return service;
    }

    /** 부서/개인 일정관리 ID 생성기. sim(개인)과 sdm(부서)이 공유한다 (원본 idgn/context-idgn-deptSchdulManage.xml) */
    @Bean(name = "deptSchdulManageIdGnrService", destroyMethod = "destroy")
    public EgovTableIdGnrServiceImpl deptSchdulManageIdGnrService(DataSource dataSource) {
        return idGnrService(dataSource, "SCHDUL_", 13, "SCHDUL_ID");
    }

    /** 업무일지 ID 생성기 (원본 idgn/context-idgn-diaryManage.xml) */
    @Bean(name = "diaryManageIdGnrService", destroyMethod = "destroy")
    public EgovTableIdGnrServiceImpl diaryManageIdGnrService(DataSource dataSource) {
        return idGnrService(dataSource, "DIARY_", 14, "DIARY_ID");
    }

    /** 부서업무 ID 생성기 (원본 idgn/context-idgn-DeptJob.xml) */
    @Bean(name = "egovDeptJobIdGnrService", destroyMethod = "destroy")
    public EgovTableIdGnrServiceImpl egovDeptJobIdGnrService(DataSource dataSource) {
        return idGnrService(dataSource, "DEPTJOB_", 12, "DEPT_JOB_ID");
    }

    /** 부서업무함 ID 생성기 (원본 idgn/context-idgn-DeptJob.xml) */
    @Bean(name = "egovDeptJobBxIdGnrService", destroyMethod = "destroy")
    public EgovTableIdGnrServiceImpl egovDeptJobBxIdGnrService(DataSource dataSource) {
        return idGnrService(dataSource, "DX_", 3, "DEPT_JOB_BX_ID");
    }

    /** 메모보고 ID 생성기 (원본 idgn/context-idgn-MemoReprt.xml) */
    @Bean(name = "egovMemoReprtIdGnrService", destroyMethod = "destroy")
    public EgovTableIdGnrServiceImpl egovMemoReprtIdGnrService(DataSource dataSource) {
        return idGnrService(dataSource, "MR", 4, "MEMO_REPRT");
    }

    /** 메모할일 ID 생성기 (원본 idgn/context-idgn-MemoTodo.xml) */
    @Bean(name = "egovMemoTodoIdGnrService", destroyMethod = "destroy")
    public EgovTableIdGnrServiceImpl egovMemoTodoIdGnrService(DataSource dataSource) {
        return idGnrService(dataSource, "MEMOTODO_", 11, "MEMO_TODO_ID");
    }

    /** 주간/월간 보고 ID 생성기 (원본 idgn/context-idgn-WikMnthngReprt.xml) */
    @Bean(name = "egovWikMnthngReprtIdGnrService", destroyMethod = "destroy")
    public EgovTableIdGnrServiceImpl egovWikMnthngReprtIdGnrService(DataSource dataSource) {
        return idGnrService(dataSource, "WR", 4, "WIKMNTHNG_REPRT");
    }

    /** 간부(상급자) 일정 ID 생성기 (원본 idgn/context-idgn-LeaderSchdu.xml) */
    @Bean(name = "egovLeaderSchdulIdGnrService", destroyMethod = "destroy")
    public EgovTableIdGnrServiceImpl egovLeaderSchdulIdGnrService(DataSource dataSource) {
        return idGnrService(dataSource, "LDSCHDUL_", 11, "LEADER_SCHDUL_ID");
    }

    /** 첨부파일 ID(FILE_...) 생성기. EgovFileMngUtil 이 요구한다 (원본 idgn/context-idgn-File.xml) */
    @Bean(name = "fileStrategy")
    public EgovIdGnrStrategyImpl fileStrategy() {
        EgovIdGnrStrategyImpl strategy = new EgovIdGnrStrategyImpl();
        strategy.setPrefix("FILE_");
        strategy.setCipers(15);
        strategy.setFillChar('0');
        return strategy;
    }

    @Bean(name = "egovFileIdGnrService", destroyMethod = "destroy")
    public EgovTableIdGnrServiceImpl egovFileIdGnrService(DataSource dataSource, EgovIdGnrStrategyImpl fileStrategy) {
        EgovTableIdGnrServiceImpl service = new EgovTableIdGnrServiceImpl();
        service.setDataSource(dataSource);
        service.setStrategy(fileStrategy);
        service.setBlockSize(10);
        service.setTable("COMTECOPSEQ");
        service.setTableName("FILE_ID");
        return service;
    }

    /** 게시판 마스터 ID(BBSMSTR_...) 생성기 (원본 idgn/context-idgn-bbs.xml) */
    @Bean(name = "bbsMstrStrategy")
    public EgovIdGnrStrategyImpl bbsMstrStrategy() {
        EgovIdGnrStrategyImpl strategy = new EgovIdGnrStrategyImpl();
        strategy.setPrefix("BBSMSTR_");
        strategy.setCipers(12);
        strategy.setFillChar('0');
        return strategy;
    }

    @Bean(name = "egovBBSMstrIdGnrService", destroyMethod = "destroy")
    public EgovTableIdGnrServiceImpl egovBBSMstrIdGnrService(DataSource dataSource, EgovIdGnrStrategyImpl bbsMstrStrategy) {
        EgovTableIdGnrServiceImpl service = new EgovTableIdGnrServiceImpl();
        service.setDataSource(dataSource);
        service.setStrategy(bbsMstrStrategy);
        service.setBlockSize(10);
        service.setTable("COMTECOPSEQ");
        service.setTableName("BBS_ID");
        return service;
    }

    /** 게시물 ID(NTT_ID) 생성기 (원본 idgn/context-idgn-bbs.xml) */
    @Bean(name = "nttIdStrategy")
    public EgovIdGnrStrategyImpl nttIdStrategy() {
        EgovIdGnrStrategyImpl strategy = new EgovIdGnrStrategyImpl();
        strategy.setCipers(20);
        return strategy;
    }

    @Bean(name = "egovNttIdGnrService", destroyMethod = "destroy")
    public EgovTableIdGnrServiceImpl egovNttIdGnrService(DataSource dataSource, EgovIdGnrStrategyImpl nttIdStrategy) {
        EgovTableIdGnrServiceImpl service = new EgovTableIdGnrServiceImpl();
        service.setDataSource(dataSource);
        service.setStrategy(nttIdStrategy);
        service.setBlockSize(10);
        service.setTable("COMTECOPSEQ");
        service.setTableName("NTT_ID");
        return service;
    }

    /** 블로그 ID(BLOG_...) 생성기. 이 프로젝트는 블로그 화면을 열지 않지만
     *  EgovArticleController/EgovBBSMasterController 가 무조건 주입받는 빈이라 등록은 필요하다. (원본 idgn/context-idgn-bbs.xml) */
    @Bean(name = "blogIdStrategy")
    public EgovIdGnrStrategyImpl blogIdStrategy() {
        EgovIdGnrStrategyImpl strategy = new EgovIdGnrStrategyImpl();
        strategy.setPrefix("BLOG_");
        strategy.setCipers(15);
        strategy.setFillChar('0');
        return strategy;
    }

    @Bean(name = "egovBlogIdGnrService", destroyMethod = "destroy")
    public EgovTableIdGnrServiceImpl egovBlogIdGnrService(DataSource dataSource, EgovIdGnrStrategyImpl blogIdStrategy) {
        EgovTableIdGnrServiceImpl service = new EgovTableIdGnrServiceImpl();
        service.setDataSource(dataSource);
        service.setStrategy(blogIdStrategy);
        service.setBlockSize(10);
        service.setTable("COMTECOPSEQ");
        service.setTableName("BLOG_ID");
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

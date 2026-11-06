# 3. 기술 스택과 프로젝트 구조

> [메인 README](../../README.md) · 이전: [2. 새 PC 에서 시작하기](02_setup_readme.md) · 다음: [4. 작업 방식](04_workflow_readme.md)

| 항목 | 기술 |
|------|------|
| 언어 | Java 17 |
| 프레임워크 | Spring Boot 3.5.6 (Spring Framework 6.2.11) + 전자정부프레임워크 실행환경 5.0.2 (`ptl-mvc`, `fdl-cmmn`, `fdl-logging`, `psl-dataaccess`, `fdl-security` 등) |
| 빌드 | Maven (실행 가능한 war, 내장 Tomcat) |
| 뷰 | JSP + JSTL (Jakarta) |
| 데이터 접근 | MyBatis 3.5 (`mybatis-spring-boot-starter` 3.0.5) |
| DB 마이그레이션 | Flyway |
| DB | PostgreSQL 14 내장(로컬, zonky `embedded-postgres` 2.2.2) / PostgreSQL (외부) |
| 보안 | Spring Security 6.5 (실행환경 `fdl-security` 의 Java Config): 로그인, CSRF, DB 기반 URL 권한. `/api/**` 는 별도 JWT 체인([6. 공통컴포넌트 도입 현황과 설계](06_components_readme.md) 참고) |
| 공통컴포넌트 | 표준프레임워크 공통컴포넌트 5.0.7 (소스를 가져와 사용) |
| 로깅 | logback (eGovFrame 이 끌고 오는 log4j2 계열은 충돌하므로 `pom.xml` 에서 제외) |
| 테스트 | JUnit 5, MockMvc, ArchUnit(규칙 자동 검증), spring-security-test |
| 모바일 앱 | Expo(React Native) + TypeScript, `/api/**` JWT API 사용(`mobile/` 폴더, 별도 npm 프로젝트) |

```text
tools/                                    # 개발 보조 도구 (closure.py, copy_closure.py, DbQuery.java)
mobile/                                   # 모바일 앱(Expo/React Native, 별도 npm 프로젝트 - mobile/README.md 참고)
src/main/
├─ java/egovframework/egovwebpractice/    # 이 프로젝트가 직접 만든 코드
│  ├─ EgovWebPracticeApplication.java     # Boot 진입점 (egovframework.com 도 스캔)
│  ├─ config/                             # 설정 (Java Config)
│  │   ├─ FrameworkConfig                 #   leaveaTrace, 트랜잭션
│  │   ├─ CommonComponentsConfig          #   공통컴포넌트가 XML 로 정의하던 빈, 메시지 소스
│  │   ├─ CommonComponentsAspect          #   @RequireAdmin AOP, 서비스 예외 처리
│  │   ├─ SecurityConfig                  #   Spring Security, 로그인/로그아웃 브리지 필터
│  │   ├─ SecurityDatabaseInitializationDetector   # DB 조회 빈을 Flyway 이후에 만들기
│  │   ├─ CryptoConfigEnvironmentPostProcessor     # 첫 실행 때 암호화 키 생성
│  │   ├─ EmbeddedPostgresConfig / ExternalDataSourceConfig   # DataSource (@Primary)
│  │   └─ MyBatisConfig                   #   DB 종류(databaseId)
│  ├─ api/                                # 모바일 앱용 REST API(JWT 인증) - config/jwt/web
│  ├─ web/                                # XxxController, SecurityExceptionAdvice, BindingInitializerAdvice
│  └─ common/service/ (+ impl/)           # 샘플(시스템 점검) Service/VO/DAO
├─ java/egovframework/com/                # 공통컴포넌트 소스 (원본과 같은 패키지 구조)
├─ resources/
│  ├─ application.yml / application-local.yml / application-prod.yml
│  ├─ egovframework/                      # 공통컴포넌트 설정(globals.properties, conf/), 메시지, 매퍼 XML
│  ├─ messages/message.properties         # 이 프로젝트의 예외 메시지
│  ├─ mapper/*.xml                        # 이 프로젝트의 MyBatis SQL (namespace = DAO 빈 이름)
│  ├─ META-INF/spring.factories           # DB 초기화 대기 / 환경 후처리 등록
│  └─ db/migration/common/V1..V14         # Flyway (아래 [6. 공통컴포넌트 도입 현황과 설계](06_components_readme.md) 참고)
└─ webapp/                                # JSP(WEB-INF/jsp), css, js, images, tlds
src/test/java/egovframework/egovwebpractice/
   ├─ EgovComplianceRulesTest             # 표준프레임워크 규칙 자동 검증
   ├─ ApplicationContextTest, SecurityIntegrationTest, EmplyrManageIntegrationTest, AuthorGroupIntegrationTest
   └─ AdminOnlyProbeController            # 테스트 전용 @RequireAdmin 엔드포인트
```

---
[메인 README](../../README.md) · 이전: [2. 새 PC 에서 시작하기](02_setup_readme.md) · 다음: [4. 작업 방식](04_workflow_readme.md)

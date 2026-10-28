# egov-web-practice

전자정부 표준프레임워크(eGovFrame) 기반 **업무 시스템(그룹웨어) 연습 프로젝트**입니다. 목표는 로그인·직원·권한 같은 기본 기능 위에 게시판, 일정, 쪽지, 메일 발송, 메신저를 쌓는 것입니다. 로컬에서는 설치 없이 바로 실행되고, 외부 PostgreSQL 에 연결하면 다른 사람도 접속해서 쓸 수 있는 구조입니다.

> 이 문서만 읽고 새 PC 에서 시작해 [로드맵](#7-로드맵-다음-할-일)을 이어갈 수 있도록 썼습니다. 처음이면 1장부터 순서대로 읽으세요.
> GitHub 저장소 이름은 `self_liveapp` 입니다(코드 안의 프로젝트 이름은 `egov-web-practice`).

## 목차

1. [현재 상태 한눈에](#1-현재-상태-한눈에)
2. [새 PC 에서 시작하기](#2-새-pc-에서-시작하기)
3. [기술 스택과 프로젝트 구조](#3-기술-스택과-프로젝트-구조)
4. [작업 방식 (브랜치·PR·새 모듈 도입 절차)](#4-작업-방식)
5. [표준프레임워크 호환성 규칙](#5-표준프레임워크-호환성-규칙)
6. [공통컴포넌트 도입 현황과 설계](#6-공통컴포넌트-도입-현황과-설계)
7. [로드맵 (다음 할 일)](#7-로드맵-다음-할-일)
8. [아직 확인하지 못한 것](#8-아직-확인하지-못한-것)
9. [겪었던 문제 모음](#9-겪었던-문제-모음)

---

## 1. 현재 상태 한눈에

| 영역 | 상태 | 비고 |
|------|------|------|
| 실행 구조 | 완료 | Spring Boot 실행형 war, 내장 PostgreSQL(로컬) / 외부 PostgreSQL(운영) |
| 로그인·로그아웃 | 완료 | Spring Security, CSRF, DB 기반 URL 권한 |
| 업무사용자(직원) 관리 | 완료 | 관리자 전용 |
| 권한 지정 | 완료 | 관리자 전용, `@RequireAdmin` + URL 규칙 이중 방어 |
| 표준프레임워크 규칙 자동 검증 | 완료 | `mvn package` 때 테스트로 확인 |
| 공지 게시판, 일정, 쪽지, 메일 발송 | **다음 할 일** | [로드맵](#7-로드맵-다음-할-일) |
| 메신저(실시간), 메일 수신 | 예정 | 공통컴포넌트에 없어 직접 개발 |
| 메인 화면(대시보드) | 예정 | 지금 `/main.do` 는 DB 연결 확인용 임시 화면 |

- 자동 테스트 30개가 모두 통과합니다(`mvn clean package`).
- **브라우저에서 화면 모양을 눈으로 확인한 적은 없습니다.** 지금까지는 HTML 응답, 상태 코드, 리다이렉트만 확인했습니다. 새 PC 에서 가장 먼저 [화면 확인](#화면-확인-체크리스트)을 해 주세요.
- 이 프로젝트는 AI 어시스턴트(Claude)와 함께 만들었고, 그 과정의 판단과 함정을 이 문서에 정리했습니다.

---

## 2. 새 PC 에서 시작하기

### 준비물

| 항목 | 버전 | 비고 |
|------|------|------|
| JDK | 17 이상 | 21 에서 빌드·테스트함 |
| Maven | 3.x | 3.8.5 에서 확인 |
| Git | 최신 | |
| OS | Windows(amd64) 또는 Linux(glibc, amd64) | 내장 PostgreSQL 바이너리가 이 둘만 포함됨. macOS/Alpine 은 아래 참고 |

인터넷이 필요합니다(첫 빌드 때 라이브러리와 내장 PostgreSQL 바이너리를 받음). 표준프레임워크 Maven 저장소(`maven.egovframe.go.kr`)에서 일부 라이브러리를 받습니다.

### 1) 받기 전에: Git 작성자 설정 (중요)

이 저장소에 커밋을 만들기 전에, **본인 개인 정보로 작성자를 설정**하세요. 회사 PC 의 전역 설정이 그대로 쓰이면 커밋에 회사 이메일이 남습니다.

```shell
git config --global core.longpaths true      # Windows: 긴 경로 오류 방지
git clone https://github.com/ccc3132/self_liveapp.git egov-web-practice
cd egov-web-practice
git config user.name  "본인 이름"            # 이 저장소에만 적용
git config user.email "본인 개인 이메일"
git log -1 --format='%an <%ae>'              # 확인
```

토큰·비밀번호는 코드나 문서에 적지 않습니다. GitHub 에는 만료 기간이 짧은 개인용 토큰(필요한 저장소 권한만)을 쓰고, 공용 PC 에는 로그인 정보를 남기지 마세요.

### 2) 빌드와 실행

```shell
mvn clean package            # 테스트 포함 1~2분. 급하면 -DskipTests
java -jar target/egov-web-practice.war
```

- `run.bat`(Windows) / `run.sh`(Linux) 를 실행해도 됩니다. 빌드 파일이 없으면 먼저 빌드합니다.
- 접속: `http://localhost:8080/` → 로그인 화면으로 이동합니다. 포트 변경: 환경변수 `SERVER_PORT=9090`
- **실행은 프로젝트 폴더에서 하세요.** `./data/` 가 현재 위치 기준으로 만들어집니다.
- DB: 설치 없이 **PostgreSQL 14 가 앱 안에서 함께 실행**되고, 데이터는 `./data/pg/` 에 저장됩니다. 테이블은 첫 실행 때 자동으로 만들어집니다.
- 첫 실행은 DB 초기화로 10~15초, 이후는 5~9초 걸립니다.
- 앱을 강제 종료해도 다음 실행 때 남은 PostgreSQL 프로세스를 정리하고 다시 시작합니다(데이터 유지).
- 데이터를 처음부터 다시 시작하려면 앱을 끄고 `./data/pg/` 를 삭제합니다.
- `./data/egov-crypto-config.properties` 는 첫 실행 때 만들어지는 **비밀 키 파일**입니다(git 에는 올라가지 않음). 지우면 새 키가 만들어지고, 이미 발급된 암호화 ID(URL 의 `selectedId` 등)는 무효가 됩니다.

### 3) 로그인 정보

공통컴포넌트의 초기 데이터입니다. 비밀번호는 모두 `rhdxhd12`.

| 아이디 | 구분(`userSe`) | 권한 |
|--------|----------------|------|
| `TEST1`, `webmaster` | 업무사용자(`USR`) | `ROLE_ADMIN` |
| `USER` | 일반회원(`GNR`) | `ROLE_USER` |

> **주의:** 이 계정과 비밀번호는 공통컴포넌트 문서에 공개된 값입니다. **다른 사람이 접속할 수 있는 환경에 배포하기 전에 반드시 비밀번호를 바꾸거나 계정을 삭제하세요.** 연속 5회 로그인에 실패하면 계정이 잠깁니다.

### 화면 확인 체크리스트

새 PC 에서 처음 한 번, 아래를 브라우저로 열어 모양이 이상한 곳을 찾으세요(CSS·이미지 누락 여부).

1. `http://localhost:8080/` 로그인 화면 (구분을 "업무사용자"로 선택하고 `TEST1` 로 로그인)
2. `/uss/umt/EgovEmplyrManage.do` 직원 목록, 등록 화면, 페이지 번호
3. `/sec/rgm/EgovAuthorGroupList.do` 권한 지정 화면
4. 직원 등록 → 권한 지정 → 그 직원으로 로그인하는 흐름
5. 수정·삭제·비밀번호 변경 버튼(자동 테스트로 확인하지 못한 부분)

### 테스트와 DB 조회

- `mvn clean package` 가 모든 테스트를 실행합니다. 테스트는 각자 별도의 내장 PostgreSQL(`target/test-pg-*`)을 띄웁니다.
- 실행 중인 로컬 DB 를 들여다보려면 `tools/DbQuery.java` 를 쓰세요(포트는 앱 로그의 `postmaster started ... on port N`).
- macOS/Alpine 에서 실행하려면 `pom.xml` 의 `embedded-postgres` 제외 항목(`darwin-amd64`, `linux-amd64-alpine`)을 지우세요. 제외한 이유는 war 용량(약 120MB → 약 90MB)입니다. Apple Silicon 용 바이너리는 포함된 적이 없어 별도 확인이 필요합니다.

### 외부 DB 로 실행 (운영·다른 사람 접속용)

환경변수로 `prod` 프로필과 접속 정보를 주고 실행합니다. 비밀번호는 파일에 적지 않습니다.

```shell
SPRING_PROFILES_ACTIVE=prod \
DB_URL=jdbc:postgresql://localhost:5432/egovweb \
DB_USER=egov \
DB_PASSWORD=비밀번호 \
java -jar target/egov-web-practice.war
```

- 앱이 시작할 때 Flyway 가 DB 버전을 확인하고, 비어 있으면 테이블을 전부 만들고 오래됐으면 부족한 부분만 추가합니다.
- **`prod` 프로필로 실제 실행해 본 적은 없습니다**([아직 확인하지 못한 것](#8-아직-확인하지-못한-것)).
- 외부 SMTP(메일 발송)는 환경변수 `APP_MAIL_HOST`, `APP_MAIL_PORT`, `APP_MAIL_USERNAME`, `APP_MAIL_PASSWORD`, `APP_MAIL_SENDER_NAME`, `APP_MAIL_SENDER_ADDRESS` 로 줍니다(`CommonComponentsConfig`). 환경변수 이름은 Spring 의 이름 변환 규칙을 따른 것이며, 실제 메일 발송으로 확인하지는 않았습니다.
- 외장 Tomcat(10.1 이상, Jakarta)에 war 를 올리는 방식도 가능하지만 확인하지 않았습니다.

---

## 3. 기술 스택과 프로젝트 구조

| 항목 | 기술 |
|------|------|
| 언어 | Java 17 |
| 프레임워크 | Spring Boot 3.5.6 (Spring Framework 6.2.11) + 전자정부프레임워크 실행환경 5.0.2 (`ptl-mvc`, `fdl-cmmn`, `fdl-logging`, `psl-dataaccess`, `fdl-security` 등) |
| 빌드 | Maven (실행 가능한 war, 내장 Tomcat) |
| 뷰 | JSP + JSTL (Jakarta) |
| 데이터 접근 | MyBatis 3.5 (`mybatis-spring-boot-starter` 3.0.5) |
| DB 마이그레이션 | Flyway |
| DB | PostgreSQL 14 내장(로컬, zonky `embedded-postgres` 2.2.2) / PostgreSQL (외부) |
| 보안 | Spring Security 6.5 (실행환경 `fdl-security` 의 Java Config): 로그인, CSRF, DB 기반 URL 권한 |
| 공통컴포넌트 | 표준프레임워크 공통컴포넌트 5.0.7 (소스를 가져와 사용) |
| 로깅 | logback (eGovFrame 이 끌고 오는 log4j2 계열은 충돌하므로 `pom.xml` 에서 제외) |
| 테스트 | JUnit 5, MockMvc, ArchUnit(규칙 자동 검증), spring-security-test |

```text
tools/                                    # 개발 보조 도구 (closure.py, copy_closure.py, DbQuery.java)
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
│  ├─ web/                                # XxxController, SecurityExceptionAdvice, BindingInitializerAdvice
│  └─ common/service/ (+ impl/)           # 샘플(시스템 점검) Service/VO/DAO
├─ java/egovframework/com/                # 공통컴포넌트 소스 (원본과 같은 패키지 구조)
├─ resources/
│  ├─ application.yml / application-local.yml / application-prod.yml
│  ├─ egovframework/                      # 공통컴포넌트 설정(globals.properties, conf/), 메시지, 매퍼 XML
│  ├─ messages/message.properties         # 이 프로젝트의 예외 메시지
│  ├─ mapper/*.xml                        # 이 프로젝트의 MyBatis SQL (namespace = DAO 빈 이름)
│  ├─ META-INF/spring.factories           # DB 초기화 대기 / 환경 후처리 등록
│  └─ db/migration/common/V1..V8          # Flyway (아래 6장 참고)
└─ webapp/                                # JSP(WEB-INF/jsp), css, js, images, tlds
src/test/java/egovframework/egovwebpractice/
   ├─ EgovComplianceRulesTest             # 표준프레임워크 규칙 자동 검증
   ├─ ApplicationContextTest, SecurityIntegrationTest, EmplyrManageIntegrationTest, AuthorGroupIntegrationTest
   └─ AdminOnlyProbeController            # 테스트 전용 @RequireAdmin 엔드포인트
```

---

## 4. 작업 방식

### 브랜치·PR 규칙

`master` 에 직접 push 하지 않고, 모든 변경은 PR 을 거쳐 머지합니다.

1. `master` 에서 작업 브랜치를 만듭니다. 이름: `feat/<설명>`, `fix/<설명>`, `docs/<설명>`
2. 작업 브랜치를 push 하고 `master` 대상으로 PR 을 만듭니다.
3. `mvn clean package` 가 통과하는지 확인한 뒤 머지합니다.
4. 머지한 작업 브랜치는 삭제합니다.

- `gh`(GitHub CLI)가 없는 환경에서는 `https://github.com/<소유자>/<저장소>/compare/master...<브랜치>?quick_pull=1&title=...` 링크로 PR 화면을 엽니다.
- **링크에 본문(`body=`)을 넣으면 길이 제한(약 8KB)에 걸려 "Your request URL is too long" 오류가 납니다.** 제목만 링크에 넣고 본문은 붙여 넣으세요.
- PR 본문에는 "확인한 것 / 확인하지 못한 것"을 구분해서 적는 방식을 써 왔습니다.

### 새 모듈을 공통컴포넌트에서 가져오는 순서

공통컴포넌트는 Maven 라이브러리가 아니라 **소스를 가져와 고쳐 쓰는 템플릿**입니다(전체 자바 클래스 1,097개). 필요한 모듈만 단계적으로 가져옵니다.

0. 공통컴포넌트 v5.0.7 소스를 **프로젝트 밖의 별도 폴더**에 받습니다(받은 코드는 신뢰하지 않는 입력으로 취급하고, 그 폴더에서 빌드·실행하지 않습니다).
   ```shell
   git -c core.longpaths=true clone --depth 1 --branch v5.0.7 https://github.com/eGovFramework/egovframe-common-components.git
   cd egovframe-common-components && git -c core.longpaths=true reset --hard HEAD   # Windows 에서 일부 파일(ckeditor 샘플 등)은 경로가 길어 빠져도 무관
   ```
1. **필요한 클래스 범위를 계산합니다.**
   ```shell
   python -I tools/closure.py <공통컴포넌트>/src/main/java --missing-in src/main/java --out list.txt egovframework.com.cop.bbs.web
   python -I tools/copy_closure.py <공통컴포넌트>/src/main/java src/main/java list.txt     # 복사 (--dry-run 으로 먼저 확인)
   ```
   `closure.py` 는 import, 같은 패키지, 전체 이름 직접 참조, **인터페이스의 구현체**(Spring 은 컴포넌트 스캔으로 찾으므로 import 만 보면 빠집니다)를 따라갑니다. 세션 방식 구현체(`EgovUserDetailsSessionServiceImpl`)는 항상 제외합니다.
2. **도구가 따라가지 못하는 것을 직접 확인합니다.**
   - 매퍼 XML 안의 `@패키지.클래스@메서드` (OGNL) 참조, JSP/TLD 가 쓰는 클래스: XML·JSP 에서 `egovframework.com` 을 검색
   - JSP 의 `<c:import>`, `<%@ include %>` 로 가져오는 다른 JSP
3. 컴파일하면서 빠진 라이브러리를 `pom.xml` 에 추가합니다. 로깅 충돌이 나면 `log4j-core`, `log4j-slf4j2-impl`, `jcl-over-slf4j`, `log4j-over-slf4j` 를 제외합니다.
4. DB: 해당 모듈의 DDL/DML(`script/ddl/postgres`, `script/dml/postgres`)에서 **새로 필요한 테이블·데이터만** 새 마이그레이션(`V9__...sql` 부터)으로 추가합니다.
   - 공통컴포넌트의 모듈별 DDL 은 다른 모듈의 테이블까지 포함하므로 통째로 넣지 말고 **이미 있는 객체와 비교해 차이만** 넣으세요.
   - MySQL/Oracle 방식의 이스케이프(`'\\A...'` 처럼 역슬래시 두 개)가 있는지 확인합니다(PostgreSQL 은 그대로 저장함).
5. `@Resource(name=...)` 로 요구하는 빈을 `CommonComponentsConfig` 에 Java Config 로 추가합니다(원본은 `context-*.xml`).
6. JSP·CSS·JS·이미지·메시지·매퍼 XML 을 복사합니다. **메시지 파일은 `CommonComponentsConfig.messageSource()` 의 basename 에도 등록**해야 합니다.
7. **URL 권한을 반드시 점검합니다.** 공통컴포넌트 기본 규칙은 너무 넓습니다(회원관리 URL 은 익명 허용, 나머지 `*.do` 는 `ROLE_USER` 에도 허용). 새 관리 화면은 `COMTNROLEINFO` 에 URL 규칙을 추가해 `ROLE_ADMIN` 전용으로 만들고(`V7`, `V8` 참고), 핸들러마다 `@RequireAdmin` 또는 코드 안의 권한 검사가 있는지 확인합니다.
8. 통합 테스트를 추가합니다. `@RequireAdmin` 이 필요한 기능은 **관리자 / 일반회원 / 비로그인 세 경우**를 모두 검증하고, 권한 검사를 임시로 꺼서 테스트가 실제로 실패하는지도 확인하세요(테스트가 형식적이지 않은지).
9. **MockMvc 는 JSP 를 그리지 않으므로, 반드시 `java -jar` 로 실제 실행해서 화면을 확인합니다.** 새 DB(`./data/pg` 삭제)로 첫 실행도 확인합니다. 메시지 누락, 태그 오류, `c:import` 실패는 이때만 드러납니다.
10. 원본 코드를 고쳤다면 아래 "원본에서 수정한 부분"에 기록합니다.

---

## 5. 표준프레임워크 호환성 규칙

[전자정부 표준프레임워크 호환성 가이드(2024.11.14)](https://maven.egovframe.go.kr/publist/HDD1/public/documents/%ED%98%B8%ED%99%98%EC%84%B1%ED%99%95%EC%9D%B8_%EA%B0%80%EC%9D%B4%EB%93%9C%EB%9D%BC%EC%9D%B8_20241114.pdf)의 7개 규칙을 따릅니다. 표의 "자동 검증"은 `mvn package` 때 `EgovComplianceRulesTest` 가 확인하므로, 규칙을 어기면 빌드가 실패합니다.

| # | 규칙 | 이 프로젝트의 적용 | 자동 검증 |
|---|------|--------------------|-----------|
| 1-1 | 실행환경 라이브러리 변경 금지 | `org.egovframe.rte` jar 를 Maven 에서 받은 그대로 사용 (수정·재패키징 안 함). 제3자 log4j2 계열만 제외 | - (수동) |
| 1-2 | 라이브러리 적용 범위·버전 | 필수 4종을 포함한 실행환경 라이브러리를 모두 5.0.2 로 통일(공통컴포넌트 5.0.7 이 요구하는 버전). Spring Framework 는 실행환경 적용 버전과 같은 6.2.11 (Boot 3.5.6 이 관리하는 버전) | - (수동) |
| 2 | 실행환경 설정 | 트랜잭션: `@EnableTransactionManagement` + `@Transactional` (Java Config). 커넥션 풀: HikariCP. 메시지·`leaveaTrace`: `FrameworkConfig` | - (수동) |
| 3 | MVC 아키텍처 | Controller 는 `@Controller` + `@GetMapping` 등 매핑, DAO·MyBatis·DataSource 직접 호출 금지, 서비스 인터페이스 주입으로만 처리 | O |
| 4 | 서비스 아키텍처 | `@Service` 클래스는 `EgovAbstractServiceImpl` 상속 + `..service` 패키지의 인터페이스 구현. 예외는 `processException(키)` | O |
| 5 | 데이터 액세스 아키텍처 | `@Repository` DAO 는 `EgovAbstractMapper` 상속, 제공 메서드(`selectOne` 등)로 SQL 호출 | O |
| 6 | 실행환경 확장 | 실행환경 클래스를 상속한 클래스는 `org.egovframe.rte` 패키지에 두지 않고 이름을 `Egov` 로 시작하지 않음 | O |
| 7 | 표준프레임워크 적용·명명 규칙 통일 | 프로젝트에 Controller·Service·DAO 가 모두 있고, 같은 명명 규칙 사용 | O |

**명명 규칙** (`egovframework.egovwebpractice` 아래 새로 만드는 코드는 모두 동일)

| 계층 | 클래스 | 패키지 | 빈 이름 |
|------|--------|--------|---------|
| Controller | `XxxController` | `..web` | (기본) |
| Service | `XxxService` (인터페이스) | `..service` | - |
| ServiceImpl | `XxxServiceImpl` | `..service.impl` | `xxxService` |
| DAO | `XxxDAO` | `..service.impl` | `xxxDAO` |
| VO | `XxxVO` | `..service` | - |
| 매퍼 XML | `mapper/XxxMapper.xml` (namespace = DAO 빈 이름) | - | - |

**코딩 규칙**
- Controller → Service(인터페이스) → ServiceImpl → DAO 순으로만 호출합니다. 서비스 메서드는 `throws Exception` 을 선언합니다.
- 테이블 변경은 항상 **새 마이그레이션 파일**로 추가합니다. **이미 적용된 마이그레이션(`V1`~`V8`)은 주석 한 줄도 고치지 않습니다**(Flyway 체크섬 검증이 실패해 기존 DB 에서 앱이 뜨지 않음).
- DB 마다 문법이 달라지는 SQL 만 `db/migration/<vendor>/` 에 둡니다. 페이징은 `LIMIT/OFFSET` 처럼 공통 문법을 우선 사용합니다.
- `egovframework.com` 아래(공통컴포넌트 소스)는 위 명명 규칙과 자동 검증의 대상이 아닙니다("공통컴포넌트 재사용" 예외). 우리가 만든 코드와 섞지 마세요.

> 이 규칙은 가이드에 적힌 내용을 코드로 옮긴 것이며, 공식 호환성 확인 도구로 검사한 결과는 아닙니다. 규칙 1-1, 1-2, 2 는 사람이 확인해야 합니다. 가이드 외에 표준프레임워크 위키의 개발 가이드(코딩 규약 등)는 접속이 막혀 읽지 못했습니다.

---

## 6. 공통컴포넌트 도입 현황과 설계

| 모듈 | 상태 | 비고 |
|------|------|------|
| 로그인·로그아웃 (`uat.uia`) | 도입 | Spring Security 방식 인증(`Globals.Auth=security`) |
| 사용자 권한 지정, `@RequireAdmin` | 도입 | |
| 업무사용자 관리 (`uss.umt`) | 도입 | **관리자 전용**. 일반회원·기업회원 관리, 회원가입 화면은 도입하지 않음 |
| 권한그룹 관리 (`sec.rgm`) | 도입 | **관리자 전용** |
| 권한·롤·그룹 관리 화면 (`sec.ram`, `sec.rmt`, `sec.gmt`) | 예정 | |
| 게시판·일정·쪽지·메일 등 (`cop.*`) | 예정 | [로드맵](#7-로드맵-다음-할-일) |

**화면 주소:** 로그인 `/uat/uia/egovLoginUsr.do`, 직원 관리 `/uss/umt/EgovEmplyrManage.do`, 권한 지정 `/sec/rgm/EgovAuthorGroupList.do`

### Flyway 마이그레이션

| 번호 | 내용 |
|------|------|
| `V1` | 샘플 테이블(`system_check`), `/main.do` 임시 화면용 |
| `V2`, `V3` | 로그인 모듈의 공통컴포넌트 DDL(테이블 17개)과 초기 데이터(샘플 계정 포함) |
| `V4` | 사용자별 권한 테이블(`COMTNEMPLYRSCRTYESTBS`)과 초기 권한 |
| `V5` | **URL 권한 패턴 보정.** 원본 DML 은 정규식을 `'\\A/uat/uia/.*\\.do.*\\Z'` 처럼 역슬래시 두 개로 적었는데, MySQL/Oracle 과 달리 PostgreSQL 은 두 개를 그대로 저장해 모든 URL 이 거부됩니다(로그인 화면 무한 리다이렉트) |
| `V6` | 업무사용자 정보 변경 이력 테이블 |
| `V7` | `/uss/umt/**` 를 관리자 전용으로 제한 |
| `V8` | `/sec/rgm/**` 를 관리자 전용으로 제한 |

### 인증·권한 구조

- **Spring Security**: 필터 체인은 실행환경 `EgovSecurityConfiguration` 이 만들고(`SecurityConfig` 에서 `@Import`), 설정값은 `egovframework/egovProps/conf/egov-security-config.properties` 에 있습니다.
- **URL 권한은 DB 에서 읽습니다**: `COMTNROLEINFO`(URL 패턴), `COMTNAUTHORROLERELATE`(권한-롤), `COMTNEMPLYRSCRTYESTBS`(사용자-권한). 앱 시작 때 읽습니다(실행 중에 갱신되는지는 확인하지 않았으니, 바꾼 뒤에는 앱을 재시작해서 확인하세요). 패턴은 `ROLE_SORT` 순서로 처음 맞는 규칙이 적용됩니다.
- **CSRF 보호가 켜져 있습니다.** POST 폼에는 `_csrf` 값이 필요합니다. 공통컴포넌트의 `<form:form>` 은 자동으로 포함합니다. 직접 만드는 폼은 `<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>` 를 넣으세요.
- **`@RequireAdmin`**: 관리자(`ROLE_ADMIN`)만 호출할 수 있는 메서드에 붙입니다. 원본에서는 XML(`context-aspect.xml`)의 AOP 가 강제하던 것을 `CommonComponentsAspect` 로 옮겼습니다. 이 AOP 가 없으면 어노테이션만 있고 검사는 일어나지 않습니다. 권한이 없으면 403, 로그인이 필요하면 로그인 화면으로 이동합니다(`SecurityExceptionAdvice`).
- 공통컴포넌트의 **세션 방식 인증(`Globals.Auth=session`)은 쓰지 않습니다.** 5.0.7 에서 권한 조회가 서로를 끝없이 호출하는 구조(정적 헬퍼 → 서비스 → 정적 헬퍼)라 관리자 판정이 불가능합니다(코드를 읽어 확인했고, 실행으로 재현하지는 않았습니다).
- 권한을 바꾸면 **해당 사용자가 다시 로그인해야** 반영된다고 보지만 직접 확인하지는 않았습니다.

### 업무사용자 관리 (`uss.umt`)

- **관리자만 접근합니다.** 공통컴포넌트 초기 데이터는 `/uss/umt/**` 를 익명에게 열어 두었고(회원가입 화면용), 직원 목록 화면은 "로그인했는지"만 확인할 뿐 관리자인지는 확인하지 않습니다. 그대로 두면 **로그인한 일반회원이 직원 목록(개인정보)을 볼 수 있어서** `V7` 로 제한했습니다. 회원가입 화면을 도입하면 해당 URL 만 따로 익명 허용하세요.
- **새로 등록한 업무사용자는 권한을 지정하기 전까지 로그인할 수 없습니다.** 등록할 때 권한 행을 만들지 않는 것이 공통컴포넌트 설계입니다. 권한 지정 화면에서 지정하세요.
- **비밀번호 규칙**: 연속된 숫자·문자(`1234`, `abcd`)가 있으면 거부됩니다(`validation.pwdCheck`).
- **암호화 키**: 실행환경의 기본 키(`egovframe`)는 공개된 값이라 그대로 쓰면 "치명 경고"가 납니다. 첫 실행 때 무작위 키를 만들어 `./data/egov-crypto-config.properties` 에 저장합니다(`CryptoConfigEnvironmentPostProcessor`). 위치는 환경변수 `APP_CRYPTO_CONFIG_FILE` 로 바꿀 수 있고, 여러 서버가 같은 암호화 ID 를 주고받아야 하면 같은 파일을 공유하세요.
- 우편번호 검색 팝업(`sym/ccm/zip`)은 도입하지 않아 동작하지 않습니다. 주소는 직접 입력합니다.

### 권한 지정 (`sec.rgm`)

- 관리자가 사용자(업무사용자·일반회원·기업회원)에게 `ROLE_USER`, `ROLE_ADMIN` 등을 지정합니다. 요청 값: `userIds`(고유 ID, `;` 로 연결), `authorCodes`, `mberTyCodes`, `regYns`(`N`=아직 권한 없음, `Y`=이미 있음).
- **권한 상승을 이중으로 막습니다.** 모든 핸들러의 `@RequireAdmin` + URL 규칙 `V8`. 둘 중 하나를 빼도 일반회원이 막힌다는 것을 확인했습니다.
- 마지막 관리자의 권한을 `ROLE_USER` 로 바꾸면 관리자가 없어집니다(복구는 DB 에서 직접).

### Spring Boot 에 맞춘 방식

- 원본은 서블릿(`WebApplicationInitializer`) + XML(`context-*.xml`) 구조라서, 필요한 빈만 Java Config(`CommonComponentsConfig`, `SecurityConfig`, `CommonComponentsAspect`)로 옮겼습니다. 새 모듈이 `@Resource(name=...)` 로 요구하는 빈은 `CommonComponentsConfig` 에 같은 방식으로 추가합니다.
- 원본이 서블릿에 직접 등록하던 필터는 `FilterRegistrationBean` 으로 등록합니다. 로그인·로그아웃 브리지 필터의 순서는 `RequestContextFilter`(-105) 다음, 보안 필터 체인(-100) 앞(-102)이어야 합니다.
- 보안 모듈의 일부 빈은 생성할 때 DB 를 조회합니다. 새 DB 에서는 Flyway 보다 먼저 만들어지면 앱이 뜨지 않으므로 `SecurityDatabaseInitializationDetector`(+ `META-INF/spring.factories`)로 순서를 지정합니다. 새 보안 관련 빈을 추가할 때 같은 문제가 생기면 이 목록에 넣으세요.
- 보안 모듈이 `DataSource` 타입 빈을 하나 더 만들기 때문에 기본 DataSource 를 `@Primary` 로 명시합니다. 그렇지 않으면 MyBatis 자동 설정이 꺼집니다.
- `egov-security-config.properties` 의 접근 허용 목록에 `/WEB-INF/**`(컨트롤러가 JSP 로 이동하는 내부 요청도 보안 필터를 거침)와 `/error` 를 추가했습니다.

### 원본에서 수정한 부분 (공통컴포넌트를 업데이트할 때 다시 확인)

- `EgovProperties`: `java -jar` 로 실행하면 `WEB-INF/classes` 가 실제 파일이 아니므로, 파일이 없으면 클래스패스에서 `globals.properties` 를 읽도록 보완
- `EgovLoginController`: 로그아웃 후 이동 주소를 포털 메인(`/EgovContent.do`)에서 로그인 화면으로 변경
- `EgovCsrfSecurityConfig`: `springSecurityFilterChain` 빈 제거(Boot 의 `@EnableWebSecurity` 가 같은 이름의 빈을 만듦). CSRF 토큰 노출 필터는 유지
- `EgovFileMngController`: 오래된 `commons-lang`(2.x) 대신 `commons-lang3` 의 `StringUtils` 를 import
- `globals.properties`: 사용하는 키만 남기고 `Globals.DbType=postgres`, `Globals.Auth=security`
- `egov-security-config.properties`: `dataSource` 빈 이름, 로그인 후 이동 주소(`/main.do`), 접근 허용 목록
- 제외한 구현: 세션 방식 사용자 서비스(`EgovUserDetailsSessionServiceImpl`), 기본 암호화 설정 파일(`egov-crypto-config.properties`, 공개된 기본 키 포함)

---

## 7. 로드맵 (다음 할 일)

추천 순서: **① 화면 확인 → ② 공지 게시판 → ③ 일정 → ④ 쪽지·주소록 → ⑤ 메일 발송 → ⑥ 메인 화면 → ⑦ 메신저 → ⑧ 메일 수신 → ⑨ 운영 준비.** 각 항목은 PR 하나(또는 둘)로 나눕니다. 시작하기 전에 4장의 도구로 범위를 먼저 측정하세요.

### ① 화면 확인 (제일 먼저)

[화면 확인 체크리스트](#화면-확인-체크리스트)를 브라우저로 진행합니다. 이상이 있는 곳(CSS, 이미지, 버튼 동작)은 `fix/` 브랜치로 먼저 고칩니다.

### ② 공지 게시판 (`cop.bbs`)

**측정 결과** (`closure.py` 로 `egovframework.com.cop.bbs.web` 를 시작점으로 계산, 이 프로젝트에 없는 것만):

| 구분 | 내용 |
|------|------|
| 자바 클래스 | 34개: `bbs.service`(게시판·게시물·블로그·만족도), `bbs.web`(2개 컨트롤러), `cmt.service`(댓글, 5), `tpl.service`(게시판 템플릿, 5), `stf.service`(만족도, 2), `EgovHtmlSanitizer`, `EgovFileMngUtil` |
| 매퍼 | `EgovArticle`, `EgovBBSAddedOptions`, `EgovBBSMaster` (`mapper/com/cop/bbs/`) |
| JSP | `WEB-INF/jsp/egovframework/com/cop/bbs/` 19개 |
| 새 테이블 | `COMTNBBS`, `COMTNBBSMASTER`, `COMTNBBSMASTEROPTN`, `COMTNBBSUSE`, `COMTNCOMMENT` (`COMTNFILE`, `COMTNFILEDETAIL` 은 `V2` 에 이미 있음) |

**범위 선택** (먼저 정하세요)
- **A. 전체**: 게시판 설정, 게시물, 댓글, 첨부파일, 템플릿
- **B. 게시판 + 게시물만 (추천)**: 공지 용도에는 충분하고 PR 이 작습니다. 댓글·만족도·템플릿은 제외

**반드시 점검할 것**
- 읽기·쓰기·수정·삭제를 누가 할 수 있는가: 게시판 설정은 관리자 전용, 게시물 수정·삭제는 작성자 본인 또는 관리자. 컨트롤러 코드에서 `@RequireAdmin`/`EgovAuthorizationHelper` 사용 여부를 핸들러마다 확인하고, `/cop/bbs/**` URL 규칙을 DB 에 추가합니다(기본 규칙은 `ROLE_USER` 에 모든 `*.do` 를 엶).
- **파일 업로드**: 저장 경로(`Globals.fileStorePath`), 확장자 제한(`Globals.fileUpload.Extensions`), 크기 제한(`maxSize`), 업로드 크기는 Boot 의 `spring.servlet.multipart.*` 도 설정해야 함. 경로 조작·실행 파일 업로드가 막히는지 테스트로 확인.
- **HTML 정화**: `EgovHtmlSanitizer` 가 jsoup 을 쓰는지 확인하고(공통컴포넌트 pom 은 jsoup 1.18.3), 게시물 본문의 XSS 가 막히는지 테스트.
- JSP 가 쓰는 에디터(CKEditor 등) 의존 여부. 도입하지 않으면 해당 화면 기능이 동작하지 않을 수 있음.

### ③ 일정관리 (`cop.smt`)

달력 중심의 일정·업무입니다. `cop.smt` 아래에 하위 모듈이 여러 개(`sdm`, `djm`, `lsm`, `mrm`, `wmr`, `mtm` …)라 **범위 측정이 먼저**입니다. 약어의 정확한 의미와 어떤 하위 모듈이 필요한지는 코드와 JSP 를 열어 확인하세요(아직 측정하지 않았습니다). 부서 일정은 "같은 부서 확인"(`EgovAuthorizationHelper.isSameDept`) 같은 권한 로직이 있으니 `EgovAuthorizationHelper` 를 읽고 시작하세요.

### ④ 쪽지(`cop.ncm`)·주소록(`cop.adb`)

아직 측정하지 않았습니다. 쪽지는 암호화 ID(`noteId` 등, `BindingInitializerAdvice` 가 복호화)를 쓰므로 암호화 키 파일 관리가 중요합니다. 메신저(⑦)와는 별개의 기능입니다.

### ⑤ 메일 발송 (`cop.ems`)

- 이미 도입된 것: 발송 서비스 일부(`EgovSndngMailRegistService`, `EgovMultiPartEmail`)와 외부 SMTP 설정(`app.mail.*`). 로그인 화면의 "비밀번호 찾기"가 이 서비스를 요구해서 들어왔습니다.
- 아직 없는 것: 메일 발송 화면(`cop.ems.web`), 발송 이력 조회.
- **미확인**: 공통컴포넌트의 발송은 "XML 요청 파일을 만드는 방식"(`sndng-mail` 라이브러리)과 직접 발송(`EgovSndngMailServiceImpl`) 두 갈래로 보입니다. 어느 쪽이 실제로 메일을 보내는지, 별도의 스케줄러/배치가 필요한지 코드를 읽어 확인하세요. 처음에는 Mailpit 같은 가짜 SMTP 로 테스트해서 실제 메일이 나가지 않게 하세요.
- SMTP 비밀번호는 환경변수로만 주고, 로그에 찍히지 않는지 확인하세요.

### ⑥ 메인 화면(대시보드)

지금 `/main.do` 는 DB 연결을 확인하는 임시 화면(`SystemCheck`)입니다. 로그인 후 이동하는 주소가 `Globals.MainPage`(`globals.properties`)와 `egov-security-config.properties`(`defaultTargetUrl`)에 있습니다. 오늘 일정, 최근 공지, 안 읽은 쪽지 등을 모아 보여 주는 화면으로 바꾸고, `V1` 의 `system_check` 와 샘플 코드(`common/service`)는 정리하세요(`V1` 파일 자체는 수정 금지, 테이블을 지우려면 새 마이그레이션).

### ⑦ 메신저 (직접 개발)

공통컴포넌트에 없습니다. 설계 제안:
- Spring WebSocket + STOMP, 인증은 기존 HTTP 세션(Spring Security)을 재사용. **WebSocket 접속·구독·전송 모두 권한을 확인**(다른 사람의 대화방 구독 방지), CSRF·Origin 검사 확인
- 메시지는 DB 에 저장(새 마이그레이션 `V*__messenger.sql`), 읽음 표시와 대화방(1:1 → 그룹) 순서로 확장
- 표준프레임워크 규칙(Controller → Service → DAO, 명명 규칙)을 그대로 따라 `egovframework.egovwebpractice.msg...` 같은 패키지에 작성하고, 자동 검증 테스트에 걸리는지 확인
- 이 모듈은 "표준프레임워크 적용 규칙 7"의 예외("실행환경이 적용되지 않는 모듈")를 주장할 수도 있지만, 되도록 규칙을 따르세요

### ⑧ 메일 수신 (직접 개발)

공통컴포넌트에 없습니다. IMAP 연동(Jakarta Mail)이 필요하고, 사용자별 메일 계정 비밀번호를 저장하면 **암호화 저장과 접근 통제**가 필수입니다. 난이도가 높아 마지막에 하세요.

### ⑨ 운영 준비

- `prod` 프로필(외부 PostgreSQL)로 실제 실행 확인, 초기 계정(`rhdxhd12`) 비밀번호 변경 또는 삭제
- 암호화 키 파일(`APP_CRYPTO_CONFIG_FILE`) 관리·백업, HTTPS, 세션 쿠키 `Secure`/`SameSite`, 로그 파일 설정
- CI(빌드·테스트 자동 실행), macOS/Apple Silicon 대응, DB 백업
- 일반회원·기업회원 관리와 회원가입 화면(필요하면). 도입할 때 `/uss/umt/**` 중 가입 화면 URL 만 익명 허용하도록 URL 규칙을 조정

---

## 8. 아직 확인하지 못한 것

자동 테스트와 HTML 응답 확인 범위 밖입니다. 새 PC 에서 먼저 확인하세요.

- 브라우저에서의 화면 모양 전체
- 직원 수정·삭제·비밀번호 변경 화면, 권한 수정·삭제 버튼의 실제 동작(요청 형식과 등록만 테스트로 확인)
- 기업회원(`ENTERPRISE`) 로그인, 아이디·비밀번호 찾기, 5회 실패 시 계정 잠금, 동시 세션 제한(1개)
- 메일 발송(SMTP 설정 없이는 동작하지 않음)
- 권한을 바꾼 사용자의 세션 반영 시점
- `prod` 프로필(외부 PostgreSQL)로 실행, 외부 Tomcat 배포
- Linux 에서 내장 PostgreSQL 실행(Windows 에서만 확인), `run.bat` 더블클릭 실행
- 공식 호환성 확인 도구 검사(규칙은 가이드 문구를 코드로 옮긴 것)

---

## 9. 겪었던 문제 모음

같은 실수를 반복하지 않도록 실제로 겪은 순서대로 적습니다.

| 문제 | 원인 | 해결·교훈 |
|------|------|-----------|
| 앱이 시작 직후 `log4j-slf4j2-impl cannot be present with log4j-to-slf4j` 로 죽음 | eGovFrame 라이브러리가 log4j2 로깅을 끌고 옴 | 라이브러리마다 log4j 계열 4개를 `pom.xml` 에서 제외 |
| 공통컴포넌트 클래스를 복사했더니 컴파일 오류가 계속 남 | import 만 보면 인터페이스 구현체, 전체 이름 직접 참조, 매퍼 XML 의 OGNL 참조가 빠짐 | `tools/closure.py` 로 계산하고, XML·JSP 도 검색 |
| `java -jar` 로 실행하면 `Property file not found` | `EgovProperties` 가 `WEB-INF/classes` 를 실제 파일로 읽음 | 파일이 없으면 클래스패스에서 읽도록 수정 |
| 새 DB 에서 첫 실행하면 앱이 뜨지 않음 | 보안 모듈 빈이 Flyway 보다 먼저 테이블을 조회 | `SecurityDatabaseInitializationDetector`. **내 PC 에서만 잘 뜨는 문제는 데이터 폴더를 지우고 확인**해야 드러남 |
| 로그인 화면이 자기 자신으로 무한 리다이렉트 | PostgreSQL 에서 URL 정규식의 역슬래시가 두 개로 저장됨 | `V5` 로 보정 |
| MyBatis 가 갑자기 꺼짐 | 보안 모듈이 `DataSource` 타입 빈을 하나 더 만들어 "DataSource 가 하나일 때만" 켜지는 자동 설정이 꺼짐 | 기본 DataSource 를 `@Primary` |
| 올바른 비밀번호로 로그인이 안 됨 | 실행환경 5.0.0 은 `hash=egov-sha256` 을 몰라 BCrypt 로 처리 | 실행환경을 공통컴포넌트가 요구하는 5.0.2 로 |
| `@RequireAdmin` 이 아무 일도 안 함 | 원본 XML 의 AOP 설정을 Java Config 로 옮기지 않음 | `CommonComponentsAspect`. **검사를 임시로 꺼서 테스트가 실패하는지 확인** |
| 일반회원이 직원 목록을 볼 수 있음 | 공통컴포넌트 기본 URL 규칙이 회원관리를 익명에 열고 나머지는 `ROLE_USER` 에 엶 | `V7`, `V8`. 새 화면마다 URL 규칙 점검 |
| 화면이 500 (`No message found under code ...`) | 복사한 메시지 파일을 메시지 소스 basename 에 등록하지 않음 | `CommonComponentsConfig.messageSource()` 에 등록 |
| 등록 화면이 500 | JSP 가 `<c:import>` 로 가져오는 다른 JSP 를 복사하지 않음 | JSP 의 import/include 확인 |
| 테스트는 통과하는데 실제 화면은 500 | **MockMvc 는 JSP 를 그리지 않음** | 새 화면은 반드시 `java -jar` 로 실제 확인 |
| 앱을 강제 종료하면 다음 실행이 실패 | 내장 PostgreSQL 프로세스가 데이터 폴더를 쥔 채 남음. "실패 후 재시도"는 파일 잠금 때문에 불가 | 시작 전에 `postmaster.pid` 의 프로세스를 정리(`EmbeddedPostgresConfig`) |
| 비밀번호가 `{validation.pwdCheck}` 로 보임, 암호화 기본 키 "치명 경고" | 실행환경 jar 안의 메시지 번들 미등록 / 공개된 기본 키 | 번들 등록, 첫 실행 때 무작위 키 생성 |
| 이미 적용된 마이그레이션의 주석을 고쳤더니 Flyway 검증 실패 | 체크섬이 달라짐 | 적용된 마이그레이션은 절대 수정하지 않음(원본으로 되돌림) |
| PR 링크가 "Your request URL is too long" | 링크에 본문을 넣어 길이 제한 초과 | 제목만 링크에 넣고 본문은 붙여 넣기 |
| Windows 에서 공통컴포넌트 클론이 일부 실패 | 경로가 너무 김 | `git -c core.longpaths=true` (ckeditor 샘플 등 무관한 파일만 빠짐) |

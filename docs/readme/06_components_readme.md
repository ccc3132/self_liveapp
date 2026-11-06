# 6. 공통컴포넌트 도입 현황과 설계

> [메인 README](../../README.md) · 이전: [5. 표준프레임워크 호환성 규칙](05_compliance_readme.md) · 다음: [7. 로드맵](07_roadmap_readme.md)

| 모듈 | 상태 | 비고 |
|------|------|------|
| 로그인·로그아웃 (`uat.uia`) | 도입 | Spring Security 방식 인증(`Globals.Auth=security`) |
| 사용자 권한 지정, `@RequireAdmin` | 도입 | |
| 업무사용자 관리 (`uss.umt`) | 도입 | **관리자 전용**. 일반회원·기업회원 관리, 회원가입 화면은 도입하지 않음 |
| 권한그룹 관리 (`sec.rgm`) | 도입 | **관리자 전용** |
| 권한·롤·그룹 관리 화면 (`sec.ram`, `sec.rmt`, `sec.gmt`) | 예정 | |
| 공지 게시판 (`cop.bbs`, 게시판+게시물만) | 도입 | 게시판 설정은 **관리자 전용**, 글쓰기는 로그인한 누구나. 댓글·만족도·템플릿 화면은 제외 |
| 일정관리(`cop.smt`, 9개 하위 모듈 전부) | 도입 | sdm/djm 은 원본 자체의 `@RequireAdmin` 으로 관리자 전용 |
| 쪽지(`uss.ion.ntm/ntr/nts`) | 도입 | 원본의 `@RequireAdmin` 5곳은 제거함(일반회원도 쓸 수 있어야 해서). "쪽지 쓰기" 메뉴 추가 완료 |
| 주소록(`cop.adb`) | 도입 | |
| 메일 발송 화면·이력(`cop.ems.web`) | 도입 | SMTP 미설정 시 20초 하드 타임아웃으로 실패 처리(원래는 무한정 멈추던 버그 수정) |
| 메인 화면(대시보드) | 도입 | V1 의 system_check 샘플은 정리(V12 로 드롭), 카드형 레이아웃 |
| 포털 셸(사이드바+헤더+테마) | 직접 개발 | iframe 기반, 상속된 JSP 를 건드리지 않고 공통 틀만 씌움(아래 "포털 셸" 참고) |
| 메신저(1:1, 직접 개발) | 도입 | WebSocket 실시간 push 는 없음(화면 재방문으로 갱신) |
| 메일 수신(IMAP, 직접 개발) | 도입 | 실제 IMAP 서버 연결 자체는 미확인(연결 실패 경로만 확인) |
| 모바일 앱용 REST API(JWT, 직접 개발) | 도입 | 기존 웹 세션 로그인과 별개 체인(`/api/**`), Expo 앱(`mobile/`)에서 사용(아래 "모바일 API(JWT)" 참고) |

**화면 주소:** 로그인 `/uat/uia/egovLoginUsr.do`, 로그인 후 랜딩(포털 셸) `/shell.do`, 대시보드 `/main.do`,
직원 관리 `/uss/umt/EgovEmplyrManage.do`, 권한 지정 `/sec/rgm/EgovAuthorGroupList.do`,
게시판 관리(관리자) `/cop/bbs/selectBBSMasterInfs.do`, 게시글 목록 `/cop/bbs/selectArticleList.do?bbsId=BBSMSTR_000000000001`.
사이드바 전체 메뉴 목록은 `src/main/webapp/WEB-INF/jsp/shell.jsp` 를 보세요.

## Flyway 마이그레이션

| 번호 | 내용 |
|------|------|
| `V1` | 샘플 테이블(`system_check`), `/main.do` 임시 화면용 |
| `V2`, `V3` | 로그인 모듈의 공통컴포넌트 DDL(테이블 17개)과 초기 데이터(샘플 계정 포함) |
| `V4` | 사용자별 권한 테이블(`COMTNEMPLYRSCRTYESTBS`)과 초기 권한 |
| `V5` | **URL 권한 패턴 보정.** 원본 DML 은 정규식을 `'\\A/uat/uia/.*\\.do.*\\Z'` 처럼 역슬래시 두 개로 적었는데, MySQL/Oracle 과 달리 PostgreSQL 은 두 개를 그대로 저장해 모든 URL 이 거부됩니다(로그인 화면 무한 리다이렉트) |
| `V6` | 업무사용자 정보 변경 이력 테이블 |
| `V7` | `/uss/umt/**` 를 관리자 전용으로 제한 |
| `V8` | `/sec/rgm/**` 를 관리자 전용으로 제한 |
| `V9` | 공지 게시판(`cop.bbs`) 테이블(COMTNBBSMASTER, COMTNBBS, COMTNBBSUSE), 매퍼가 LEFT JOIN 하는 테이블(COMTNCOMMENT, COMTNTMPLATINFO, COMTNBLOG, COMTNBLOGUSER, 데이터 없음), 초기 "공지사항" 게시판 1개, 게시판 설정 URL 을 관리자 전용으로 제한 |
| `V10` | 일정관리(`cop.smt`) 9개 모듈 테이블 10개(COMTNSCHDULINFO 등) |
| `V11` | 쪽지(`uss.ion`)·주소록(`cop.adb`) 테이블 6개 |
| `V12` | V1 의 `system_check` 샘플 테이블 드롭(메인 화면이 실제 대시보드가 되면서 불필요해짐) |
| `V13` | 메신저(직접 개발) `MSGR_MESSAGE` 테이블 |
| `V14` | 메일 수신(직접 개발) `MAIL_ACCOUNT` 테이블(IMAP 계정, 비밀번호는 암호화 저장) |

## 인증·권한 구조

- **Spring Security**: 필터 체인은 실행환경 `EgovSecurityConfiguration` 이 만들고(`SecurityConfig` 에서 `@Import`), 설정값은 `egovframework/egovProps/conf/egov-security-config.properties` 에 있습니다.
- **URL 권한은 DB 에서 읽습니다**: `COMTNROLEINFO`(URL 패턴), `COMTNAUTHORROLERELATE`(권한-롤), `COMTNEMPLYRSCRTYESTBS`(사용자-권한). 앱 시작 때 읽습니다(실행 중에 갱신되는지는 확인하지 않았으니, 바꾼 뒤에는 앱을 재시작해서 확인하세요). 패턴은 `ROLE_SORT` 순서로 처음 맞는 규칙이 적용됩니다.
- **CSRF 보호가 켜져 있습니다.** POST 폼에는 `_csrf` 값이 필요합니다. 공통컴포넌트의 `<form:form>` 은 자동으로 포함합니다. 직접 만드는 폼은 `<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>` 를 넣으세요.
- **`@RequireAdmin`**: 관리자(`ROLE_ADMIN`)만 호출할 수 있는 메서드에 붙입니다. 원본에서는 XML(`context-aspect.xml`)의 AOP 가 강제하던 것을 `CommonComponentsAspect` 로 옮겼습니다. 이 AOP 가 없으면 어노테이션만 있고 검사는 일어나지 않습니다. 권한이 없으면 403, 로그인이 필요하면 로그인 화면으로 이동합니다(`SecurityExceptionAdvice`).
- 공통컴포넌트의 **세션 방식 인증(`Globals.Auth=session`)은 쓰지 않습니다.** 5.0.7 에서 권한 조회가 서로를 끝없이 호출하는 구조(정적 헬퍼 → 서비스 → 정적 헬퍼)라 관리자 판정이 불가능합니다(코드를 읽어 확인했고, 실행으로 재현하지는 않았습니다).
- 권한을 바꾸면 **해당 사용자가 다시 로그인해야** 반영된다고 보지만 직접 확인하지는 않았습니다.

## 업무사용자 관리 (`uss.umt`)

- **관리자만 접근합니다.** 공통컴포넌트 초기 데이터는 `/uss/umt/**` 를 익명에게 열어 두었고(회원가입 화면용), 직원 목록 화면은 "로그인했는지"만 확인할 뿐 관리자인지는 확인하지 않습니다. 그대로 두면 **로그인한 일반회원이 직원 목록(개인정보)을 볼 수 있어서** `V7` 로 제한했습니다. 회원가입 화면을 도입하면 해당 URL 만 따로 익명 허용하세요.
- **새로 등록한 업무사용자는 권한을 지정하기 전까지 로그인할 수 없습니다.** 등록할 때 권한 행을 만들지 않는 것이 공통컴포넌트 설계입니다. 권한 지정 화면에서 지정하세요.
- **비밀번호 규칙**: 연속된 숫자·문자(`1234`, `abcd`)가 있으면 거부됩니다(`validation.pwdCheck`).
- **암호화 키**: 실행환경의 기본 키(`egovframe`)는 공개된 값이라 그대로 쓰면 "치명 경고"가 납니다. 첫 실행 때 무작위 키를 만들어 `./data/egov-crypto-config.properties` 에 저장합니다(`CryptoConfigEnvironmentPostProcessor`). 위치는 환경변수 `APP_CRYPTO_CONFIG_FILE` 로 바꿀 수 있고, 여러 서버가 같은 암호화 ID 를 주고받아야 하면 같은 파일을 공유하세요.
- 우편번호 검색 팝업(`sym/ccm/zip`)은 도입하지 않아 동작하지 않습니다. 주소는 직접 입력합니다.

## 권한 지정 (`sec.rgm`)

- 관리자가 사용자(업무사용자·일반회원·기업회원)에게 `ROLE_USER`, `ROLE_ADMIN` 등을 지정합니다. 요청 값: `userIds`(고유 ID, `;` 로 연결), `authorCodes`, `mberTyCodes`, `regYns`(`N`=아직 권한 없음, `Y`=이미 있음).
- **권한 상승을 이중으로 막습니다.** 모든 핸들러의 `@RequireAdmin` + URL 규칙 `V8`. 둘 중 하나를 빼도 일반회원이 막힌다는 것을 확인했습니다.
- 마지막 관리자의 권한을 `ROLE_USER` 로 바꾸면 관리자가 없어집니다(복구는 DB 에서 직접).

## 포털 셸(사이드바+헤더+테마)

- `/shell.do`(`shell.jsp`)가 로그인 후 랜딩 화면입니다. 왼쪽 고정 사이드바 + 상단 헤더 + 가운데 `<iframe>` 으로 구성되고, 사이드바 메뉴를 누르면 iframe 안의 `src` 만 바뀝니다 — **기존에 가져온 ~150개 공통컴포넌트 JSP 는 단 하나도 고치지 않고** 공통 틀을 씌운 구조입니다.
- 라이트/다크/블루 테마는 셸(사이드바·헤더)에만 적용됩니다(쿠키에 저장). iframe 안의 상속 JSP 들은 `com.css` 에 추가한 공통 스타일 오버레이(폰트·여백·둥근모서리 등)만 적용되고 테마 색상까지 따라가지는 않습니다.
- 대시보드(`/main.do`)는 이 셸 구조와 별개로 자체 CSS 를 갖는 카드형 레이아웃입니다 — `com.css` 를 링크하지 않으므로, com.css 쪽만 고치면 대시보드에는 반영되지 않는다는 점에 주의하세요(한 번 이 문제로 "대시보드만 구식으로 보임" 버그가 났었습니다 — [9. 겪었던 문제 모음](09_issues_readme.md)).

## 모바일 API(JWT)

- `/api/**` 요청만 가로채는 별도 Spring Security 체인(`ApiSecurityConfig`, `@Order(1)` + `securityMatcher`)이 있습니다. 기존 웹 세션/쿠키 로그인과는 완전히 분리되어 있고(STATELESS, CSRF 비활성화), `POST /api/auth/login` 으로 JWT 를 발급해 `Authorization: Bearer` 헤더로 인증합니다.
- 비밀번호 검증은 웹 로그인과 같은 `EgovIdSaltSha256PasswordEncoder` 를 그대로 재사용합니다. 이 인코더의 salt 는 로그인 시 넘긴 `userSe+id` 가 아니라 `UserDetails.getUsername()` 값(= `jdbcUsersByUsernameQuery` 의 첫 컬럼인 raw id)이어야 합니다 — 틀리면 항상 인증 실패합니다.
- `GET /api/dashboard` 는 웹 대시보드와 같은 `DashboardService` 를 그대로 재사용합니다.
- Expo(React Native) 모바일 앱은 `mobile/` 폴더에 별도 npm 프로젝트로 있습니다(로그인+대시보드 화면까지, `mobile/README.md` 참고). 사이드바의 다른 메뉴는 아직 모바일 화면이 없습니다.

## Spring Boot 에 맞춘 방식

- 원본은 서블릿(`WebApplicationInitializer`) + XML(`context-*.xml`) 구조라서, 필요한 빈만 Java Config(`CommonComponentsConfig`, `SecurityConfig`, `CommonComponentsAspect`)로 옮겼습니다. 새 모듈이 `@Resource(name=...)` 로 요구하는 빈은 `CommonComponentsConfig` 에 같은 방식으로 추가합니다.
- 원본이 서블릿에 직접 등록하던 필터는 `FilterRegistrationBean` 으로 등록합니다. 로그인·로그아웃 브리지 필터의 순서는 `RequestContextFilter`(-105) 다음, 보안 필터 체인(-100) 앞(-102)이어야 합니다.
- 보안 모듈의 일부 빈은 생성할 때 DB 를 조회합니다. 새 DB 에서는 Flyway 보다 먼저 만들어지면 앱이 뜨지 않으므로 `SecurityDatabaseInitializationDetector`(+ `META-INF/spring.factories`)로 순서를 지정합니다. 새 보안 관련 빈을 추가할 때 같은 문제가 생기면 이 목록에 넣으세요.
- 보안 모듈이 `DataSource` 타입 빈을 하나 더 만들기 때문에 기본 DataSource 를 `@Primary` 로 명시합니다. 그렇지 않으면 MyBatis 자동 설정이 꺼집니다.
- `egov-security-config.properties` 의 접근 허용 목록에 `/WEB-INF/**`(컨트롤러가 JSP 로 이동하는 내부 요청도 보안 필터를 거침)와 `/error` 를 추가했습니다.

## 원본에서 수정한 부분 (공통컴포넌트를 업데이트할 때 다시 확인)

- `EgovProperties`: `java -jar` 로 실행하면 `WEB-INF/classes` 가 실제 파일이 아니므로, 파일이 없으면 클래스패스에서 `globals.properties` 를 읽도록 보완
- `EgovLoginController`: 로그아웃 후 이동 주소를 포털 메인(`/EgovContent.do`)에서 로그인 화면으로 변경
- `EgovCsrfSecurityConfig`: `springSecurityFilterChain` 빈 제거(Boot 의 `@EnableWebSecurity` 가 같은 이름의 빈을 만듦). CSRF 토큰 노출 필터는 유지
- `EgovFileMngController`: 오래된 `commons-lang`(2.x) 대신 `commons-lang3` 의 `StringUtils` 를 import
- `globals.properties`: 사용하는 키만 남기고 `Globals.DbType=postgres`, `Globals.Auth=security`
- `egov-security-config.properties`: `dataSource` 빈 이름, 로그인 후 이동 주소(`/shell.do`), 접근 허용 목록
- 제외한 구현: 세션 방식 사용자 서비스(`EgovUserDetailsSessionServiceImpl`), 기본 암호화 설정 파일(`egov-crypto-config.properties`, 공개된 기본 키 포함)
- 일정관리 9개 모듈(sim/sdm/lsm) 달력 JSP: `parent.document` 로 상위 포털 프레임의 폼을 찾던 로직을 자기 문서 안에 폼을 만드는 방식으로 교체(이 프로젝트에는 그런 상위 프레임이 없어서) — [9. 겪었던 문제 모음](09_issues_readme.md)
- 게시글/일정 등록·수정 JSP 7개: CKEditor 미도입 시에도 나머지 초기화(날짜 선택기, 첨부파일 등)가 계속되도록 `typeof CKEDITOR !== 'undefined'` 가드 추가

---
[메인 README](../../README.md) · 이전: [5. 표준프레임워크 호환성 규칙](05_compliance_readme.md) · 다음: [7. 로드맵](07_roadmap_readme.md)

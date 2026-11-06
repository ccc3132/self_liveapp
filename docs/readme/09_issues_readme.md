# 9. 겪었던 문제 모음

> [메인 README](../../README.md) · 이전: [8. 아직 확인하지 못한 것](08_todo_readme.md) · 다음: [10. 외부 서버에 배포하기](10_deploy_readme.md)

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
| 게시판 모듈 추가 후 `EgovHtmlSanitizer`/`EgovFileMngUtil` 컴파일 실패 | jsoup 의존성이 `pom.xml` 에 없음, `commons-lang`(2.x) import 오타 | jsoup 1.18.3 추가, `commons-lang` → `commons-lang3` 로 수정 |
| `EgovFileMngUtil` 빈 생성 실패 (`No bean named 'egovFileIdGnrService'`) | 첨부파일 ID 생성기 빈이 없었음(원본 `context-idgn-File.xml`에만 있던 설정) | `CommonComponentsConfig` 에 `fileStrategy`/`egovFileIdGnrService` Java Config 로 추가 |
| 게시글 목록 조회가 500 (`relation "comtncomment" does not exist`) | 댓글 기능은 빼기로 했지만, `EgovArticle` 매퍼가 댓글 수를 세는 서브쿼리에서 `COMTNCOMMENT` 를 직접 LEFT JOIN 함 | 댓글·만족도·템플릿 **기능**(화면/서비스)은 빼도, 매퍼가 LEFT JOIN 하는 테이블(`COMTNCOMMENT`, `COMTNTMPLATINFO`, `COMTNBLOG`, `COMTNBLOGUSER`)은 데이터 없이 테이블만 만들어야 함 |
| 로그인 직후 CSRF 토큰으로 POST 하면 "CSRF Error" | 로그인 성공 시 세션 고정 공격 방지로 `request.changeSessionId()` 를 호출해 세션이 바뀌어, 로그인 전에 받은 CSRF 토큰이 무효가 됨 | 로그인 **후에** 아무 GET 응답에서 새 CSRF 토큰을 다시 받아서 써야 함 |
| 게시판 통합 테스트에서 관리자 전용 URL 에 대해 `status().isForbidden()` 이 실패(실제론 200) | MockMvc 는 보안 모듈의 접근 거부 forward 를 실행하지 않음(이미 `EmplyrManageIntegrationTest`/`AuthorGroupIntegrationTest` 의 주석에 적혀 있던 내용) | `forwardedUrl("/sec/ram/accessDenied.do")` 로 대상만 확인하고, 실제 403 은 `java -jar` 로 확인. 쓰기 성공 여부도 렌더링된 내용이 아니라 `JdbcTemplate` 으로 DB 를 조회해서 확인 |
| `@RequireAdmin` 만 있는(URL 규칙 없는) 핸들러의 접근거부 forward 대상이 모듈마다 다름 | `SecurityExceptionAdvice`(전역, `/WEB-INF/jsp/egovframework/com/cmm/error/accessDenied.jsp`)와 `EgovEmplyrManageController`/`EgovAuthorGroupController` 가 자체적으로 forward 하는 `/sec/ram/accessDenied.do` 는 서로 다른 코드 경로 | 테스트에서 어느 컨트롤러인지에 따라 `forwardedUrl` 기대값을 맞는 걸로 써야 함 |
| `resultType="map"` 으로 MyBatis 조회 시 JSP 의 `${s.schdulNm}` 값이 비어 보임 | PostgreSQL 이 unquoted 별칭(`AS schdulNm`)을 전부 소문자로 접어버려(`schdulnm`) EL 과 안 맞음(반면 `resultType=`클래스`" 는 `map-underscore-to-camel-case: true` 설정으로 자동 변환되어 문제 없음) | Map 조회는 별칭을 snake_case 로 통일, 클래스 바인딩은 snake_case 컬럼명 그대로 둬도 됨 |
| 일정관리(smt) 추가 후 테스트에서 `is3xxRedirection()` 이 실패(실제론 200) | 등록 성공 시 컨트롤러가 `forward:`(200)를 쓰는 핸들러와 `redirect:`(302)를 쓰는 핸들러가 섞여 있음 — 코드를 직접 읽지 않고 추측하면 틀림 | 컨트롤러의 `return` 문을 직접 확인해서 맞는 상태코드로 assert |
| 메일 발송 화면이 500 (`No message found under code 'comCopSymEms.regist.title'`) | 메시지 파일 경로가 `com/cop/ems` 가 아니라 공통컴포넌트 원본 구조상 `com/cop/sym/ems` 였음 | 올바른 경로로 등록 |
| 메일 발송 후 XML 요청 파일을 만들 때 `ClassCastException`(XmlAnyTypeImpl → SndngMailDocument) | XMLBeans 가 Spring Boot 실행 가능 jar 의 커스텀 클래스로더에서 생성 클래스의 타입시스템을 못 찾음 — 발송 자체(SMTP)는 이미 끝난 뒤에 터짐 | 감사 로그용 XML 기록을 try-catch 로 감싸서 실패해도 발송 결과에 영향 없게 함 |
| 쪽지 모듈 이름을 README 초안에서 `cop.ncm` 으로 잘못 짐작함 | `cop.ncm` 은 명함관리(NameCard)였고, 실제 쪽지는 `uss.ion.ntm/ntr/nts` | 코드에서 `NoteManageService`/`NoteRecptn`/`NoteTrnsmit` 클래스명으로 검색해서 진짜 위치를 찾음 |
| 쪽지 핵심 기능(보내기/상세조회)이 원본 소스에서부터 관리자만 쓸 수 있게 되어 있었음 | 다른 모듈의 보안 점검 커밋에서 과하게 적용된 `@RequireAdmin` 으로 보임(일반회원용 기능인데 관리자 전용이면 기능의 목적과 안 맞음) | 사용자 확인 받고 5곳 제거. 소유권 검증(`assertOwner`)은 원본에 이미 있어서 안전 |
| 일정관리(sim/sdm/lsm) 달력에서 날짜를 클릭해 등록·상세보기를 열면 아무 반응이 없음 | JSP가 `parent.document.getElementById('...SchdulManageVO')`로 상위 프레임의 폼을 찾으려 했는데, 그 폼은 원래 eGovFrame 예제의 포털 메인 프레임에만 있고 이 프로젝트의 셸(`shell.jsp`) 구조에는 없어 `gOpener`가 null | 9개 JSP가 상위 프레임에 의존하지 않도록 자기 문서 안에 숨겨진 폼을 직접 만들어 쓰도록 수정(CSRF 토큰 포함). **사이드바 메뉴를 눈으로만 보지 말고 실제로 클릭해서 끝까지 확인**해야 드러나는 종류의 버그였다 |
| 사이드바의 일부 메뉴(내 일정/부서 일정/주소록)가 빈 화면으로 연결됨 | 포털 메인화면에 작은 위젯으로 박아 넣는 용도의 JSP(`...MainList.do`, `selectAdbkMainList.do`)를 독립 화면인 줄 알고 그대로 연결함 — 본문이 사실상 빈 테이블이라 겉보기엔 "로드는 되는데 아무 내용도 없는" 상태라 쉽게 지나침 | 실제 목록/달력 화면(`...MonthList.do`, `selectAdbkList.do`)으로 교체. 새 메뉴를 사이드바에 추가할 때는 그 URL의 응답 HTML 본문 길이까지 확인 |
| 게시글/일정 등록·수정 화면에서 CKEditor 미도입 시 날짜 선택기·첨부파일 위젯까지 전부 먹통 | `CKEDITOR.replace(...)`를 가드 없이 호출해 CKEDITOR 가 없으면 즉시 예외가 나고, 같은 `onload` 함수 안의 나머지 초기화 코드가 전부 중단됨 — 일정 등록은 날짜 입력란이 readonly 라 날짜 선택기가 안 뜨면 등록 자체가 불가능했음 | 7개 JSP에 `typeof CKEDITOR !== 'undefined'` 가드 추가(초기화 호출 + 저장 시 양쪽 다) |
| 일정·메일·보고서·업무일지·부서업무·게시글 **상세보기 화면 9개 전부**가 500 에러 | 공통으로 `<c:import url="/cmm/fms/selectFileInfs.do">` 를 쓰는데, 그 컨트롤러가 forward 하는 `egovframework/com/cmm/fms/EgovFileList.jsp` 파일 자체가 프로젝트에 없었음(공통컴포넌트 모듈을 가져올 때 이 파일만 빠짐) | 업스트림 공통컴포넌트 소스에서 그대로 복사해 추가. **여러 모듈이 공유하는 작은 include 파일 하나가 빠지면 파급력이 크다**는 전형적인 사례 |
| 쪽지 수신자 검색 팝업이 브라우저 팝업 차단 설정이면 크래시 | `window.open()` 이 차단되면 null 을 반환하는데 바로 `.focus()` 를 호출해서 "Cannot read properties of null" 로 죽음 | null 체크 후 안내 문구(alert)로 바꿈 |
| **메일 발송이 SMTP 미설정 시 9분 넘게 무한정 멈춤** | `EgovMultiPartEmail` 에 설정해 둔 60초 소켓 타임아웃(`setSocketConnectionTimeout`/`setSocketTimeout`)이 실제로는 지켜지지 않음(Commons Email2 라이브러리 내부 동작 — 정확한 원인은 특정하지 못함) | 라이브러리에 기대지 않고, 메일 전송을 별도 스레드에서 실행해 20초 하드 타임아웃을 거는 방식으로 근본 차단(`Future.get(20, SECONDS)`). 타임아웃/실패 시 이동하는 `egovError.jsp` 도 파일이 없어서 404 였던 것을 같이 발견해 고침 |

---
[메인 README](../../README.md) · 이전: [8. 아직 확인하지 못한 것](08_todo_readme.md) · 다음: [10. 외부 서버에 배포하기](10_deploy_readme.md)

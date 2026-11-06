# 7. 로드맵 (다음 할 일)

> [메인 README](../../README.md) · 이전: [6. 공통컴포넌트 도입 현황과 설계](06_components_readme.md) · 다음: [8. 아직 확인하지 못한 것](08_todo_readme.md)

추천 순서였습니다: **① 화면 확인 → ② 공지 게시판 → ③ 일정 → ④ 쪽지·주소록 → ⑤ 메일 발송 → ⑥ 메인 화면 → ⑦ 메신저 → ⑧ 메일 수신 → ⑨ 운영 준비.** 전부 완료되었고, 아래는 그 과정 기록입니다. 각 항목은 PR 하나(또는 둘)로 나눴습니다.

### ① 화면 확인 (제일 먼저) — 완료

[화면 확인 체크리스트](02_setup_readme.md#화면-확인-체크리스트)를 브라우저로 진행했습니다. 발견한 문제는 `fix/` 브랜치로 고쳤습니다(자세한 목록은 [9. 겪었던 문제 모음](09_issues_readme.md)).

### ② 공지 게시판 (`cop.bbs`) — 완료 (스코프 B)

"게시판 + 게시물만" 으로 들어왔습니다. 가져온 것: `bbs.service`/`bbs.web`(컨트롤러 2개), `EgovHtmlSanitizer`,
`EgovFileMngUtil`, 매퍼 3개(`EgovArticle`, `EgovBBSAddedOptions`, `EgovBBSMaster`), JSP 19개(블로그·방명록 화면
포함 — 컨트롤러가 그대로 들어와서 JSP 도 같이 복사했으나 화면 확인은 안 함). 제외한 것: `cmt.service`(댓글),
`tpl.service`(게시판 템플릿 화면), `stf.service`(만족도). 단 `COMTNCOMMENT`, `COMTNTMPLATINFO`, `COMTNBLOG`,
`COMTNBLOGUSER` 테이블은 매퍼가 SQL에서 직접 LEFT JOIN 하므로 데이터 없이 테이블만 `V9` 에 추가했습니다.

- 게시판 설정(등록/수정/삭제/목록/상세)은 **관리자 전용**(`@RequireAdmin` + `V9` URL 규칙 이중 방어). 게시물
  조회·작성은 로그인한 누구나(기본 `ROLE_USER` 규칙), 수정·삭제는 작성자 본인만(`EgovAuthorizationHelper.assertOwner`,
  원본 설계 그대로).
- 통합 테스트: `NoticeBoardIntegrationTest`. **MockMvc 는 보안 모듈의 접근 거부 forward 를 실행하지 않으므로
  ([9. 겪었던 문제 모음](09_issues_readme.md) 참고) 상태 코드 403 을 직접 assert 하지 말고 `forwardedUrl`/DB 조회로 확인했습니다.**
- 이후 브라우저 점검에서 게시글 등록이 CKEditor 미도입 시 먹통되는 문제, 상세조회가 500 나는 문제(공통 `EgovFileList.jsp` 누락)를 찾아 고쳤습니다 — [9. 겪었던 문제 모음](09_issues_readme.md).

### ③ 일정관리 (`cop.smt`) — 완료

9개 하위 모듈(sim/sdm/sam/lsm/dsm/djm/mrm/mtm/wmr) 전부 도입(사용자가 전체 도입을 선택). 부서일정(sdm)·부서업무(djm)는
원본 소스 자체에 `@RequireAdmin` 이 이미 있어 관리자 전용으로 동작한다(새 URL 규칙 추가 안 함, 원본 설계 그대로).
나머지는 로그인한 누구나, 수정/삭제는 `EgovAuthorizationHelper.assertOwner`/`assertSameDept` 로 원본이 이미 보호.

이후 브라우저 점검에서 날짜 클릭으로 등록/상세보기가 열리지 않는 문제(상위 포털 프레임 의존)와 상세조회 500 에러를 찾아 고쳤습니다 — [9. 겪었던 문제 모음](09_issues_readme.md).

### ④ 쪽지(`uss.ion.ntm/ntr/nts`)·주소록(`cop.adb`) — 완료

README 초안의 모듈명 추정(`cop.ncm`)이 틀렸다 — 실제 쪽지 모듈은 `uss.ion.ntm`(보내기)/`ntr`(받은쪽지함)/`nts`(보낸쪽지함)이고,
`cop.ncm`은 명함관리(이번에 도입 안 함)였다. **원본 소스 자체에 쪽지 핵심 기능(보내기/상세조회/수신자 선택)에
`@RequireAdmin`이 걸려 있어서, 그대로면 관리자만 쪽지를 쓸 수 있었다** — 사용자 확인을 받아 5곳을 제거했다
(소유권 검증은 원본에 이미 있어 안전). 이후 "쪽지 쓰기" 메뉴가 사이드바에 빠져 있던 것도 추가했습니다.

### ⑤ 메일 발송 (`cop.ems`) — 완료

발송 흐름 확인: `insertSndngMail` → DB 기록(R) → `EgovMultiPartEmail` 로 동기 발송 → XML 요청 파일 기록
(`trnsmitXmlData`) → 그 파일을 바로 다시 읽어 완료(C) 처리. **별도 스케줄러/배치 없음**, 한 요청 안에서 전부 끝난다.
`trnsmitXmlData`(감사 로그용 XMLBeans 문서 생성)가 Spring Boot 실행 가능 jar 의 클래스로더에서 생성 클래스를
못 찾아 500을 내는 버그가 있어 try-catch 로 방어(발송 자체는 이미 끝난 뒤라 결과에 영향 없음). Mailpit(로컬
가짜 SMTP, `winget install axllent.mailpit`)으로 실제 발송을 확인했다.

이후 SMTP 미설정 시 요청이 9분 넘게 무한정 멈추는 심각한 버그를 발견해 20초 하드 타임아웃으로 고쳤습니다 — [9. 겪었던 문제 모음](09_issues_readme.md).

### ⑥ 메인 화면(대시보드) — 완료

`/main.do` 를 오늘 일정·최근 공지·안 읽은 쪽지를 모아 보여주는 대시보드로 교체했고, `V1` 의 `system_check`
샘플은 `V12` 로 테이블을 드롭하고 서비스/DAO/매퍼를 지웠다(`V1` 파일 자체는 수정 안 함). MyBatis
`resultType="map"` 조회에서 PostgreSQL 이 unquoted 별칭을 소문자로 접어버리는 것 때문에 처음에 값이
안 보였다 — 별칭을 snake_case 로 통일해서 해결([9. 겪었던 문제 모음](09_issues_readme.md)).

이후 포털 셸(사이드바+헤더+테마), 카드형 레이아웃, 실시간 접속자 위젯을 추가했습니다 — [6. 공통컴포넌트 도입 현황과 설계](06_components_readme.md).

### ⑦ 메신저 (직접 개발) — 완료, 설계는 바뀜

WebSocket/STOMP 대신 **REST 기반**으로 구현했다(1:1 대화만, 그룹 없음, 실시간 push 없음 — 화면을
새로고침/재방문해야 새 메시지가 보인다). 범위를 좁게 잡아 테스트 가능하고 안정적인 형태를 먼저 들여오는
쪽을 선택했다. `egovframework.egovwebpractice.msg` 패키지, 표준프레임워크 규칙(Controller → Service → DAO)을
그대로 따름. 새 테이블 `MSGR_MESSAGE`(`V13`).

### ⑧ 메일 수신 (직접 개발) — 완료, 실제 IMAP 연결은 미확인

사용자별 IMAP 계정(호스트/포트/아이디/비밀번호/SSL) 설정 화면 + 받은편지함 조회(`egovframework.egovwebpractice.mail`
패키지). 비밀번호는 `egovEnvCryptoService` 로 암호화해서 `MAIL_ACCOUNT`(`V14`)에 저장. 받은 메일은 이 앱에
저장하지 않고 매번 IMAP 서버에 새로 연결해서 읽기 전용으로 조회한다. 연결/인증 실패는 500 대신 에러 화면으로
처리. **실제 IMAP 서버 연결 자체는 확인하지 못했다** — Mailpit 은 SMTP 만 지원해서 연결 실패 경로(에러 화면)만
확인했다.

### ⑨ 운영 준비 — 일부 완료

완료: CI(GitHub Actions, `mvn clean package` 를 push/PR 마다 실행), 세션 쿠키(`Secure`/`SameSite=Lax`/`HttpOnly`,
`prod` 프로필은 `Secure=true`, `COOKIE_SECURE` 환경변수로 덮어쓰기 가능), 로그 파일 설정(`prod` 프로필, 롤링 50MB/30개),
Docker/Render 배포 구성([10. 외부 서버에 배포하기](10_deploy_readme.md)).

아직: `prod` 프로필로 실제 외부 PostgreSQL 에 실행해 본 적은 없음(로컬 테스트로만 확인). 초기 계정(`rhdxhd12`)
비밀번호 변경/삭제는 실제 배포 시점에 해야 함(코드로 강제하면 로컬 개발이 불편해져서 넣지 않음). 암호화 키
파일 백업, HTTPS(리버스 프록시 또는 `server.ssl.*`), macOS/Apple Silicon 대응, DB 백업, 일반회원·기업회원
관리와 회원가입 화면은 손대지 않았다.

### 로드맵 이후 추가 작업

로드맵 ①~⑨가 모두 끝난 뒤에도 사용자 요청으로 계속 작업했습니다. 자세한 내용은 해당 장을 참고하세요.

- UI 현대화(포털 셸, 테마, 카드형 대시보드), 사이드바 누락 메뉴 보강 — [6. 공통컴포넌트 도입 현황과 설계](06_components_readme.md)
- 모바일 앱용 REST API(JWT) + Expo 앱 — [6. 공통컴포넌트 도입 현황과 설계](06_components_readme.md), `mobile/README.md`
- 브라우저로 전체 메뉴·등록/저장까지 직접 점검해 발견한 버그 다수 수정(CKEditor 크래시, 누락 JSP, 메일 발송 무한 멈춤 등) — [9. 겪었던 문제 모음](09_issues_readme.md)
- 외부 서버 배포 구성(Docker, Render) — [10. 외부 서버에 배포하기](10_deploy_readme.md)

---
[메인 README](../../README.md) · 이전: [6. 공통컴포넌트 도입 현황과 설계](06_components_readme.md) · 다음: [8. 아직 확인하지 못한 것](08_todo_readme.md)

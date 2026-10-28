# self_liveapp

전자정부프레임워크(eGovFrame) 기반 웹 사이트 프로젝트입니다. 기존 포트폴리오용 코드를 모두 정리하고 뼈대부터 다시 시작합니다.

## 빠른 시작

```shell
mvn clean package
```

- 필요 환경: JDK 17 이상, Maven 3.x
- 산출물: `target/self_liveapp-0.0.1-SNAPSHOT.war` (Tomcat 10.1 등 Jakarta Servlet 6.0 컨테이너에 배포)
- 접속: `http://localhost:8080/<컨텍스트>/main.do`
- 주의: Jakarta EE 기반이므로 Tomcat 9 이하(javax)에서는 동작하지 않습니다.

## 기술 스택

| 항목 | 기술 |
|------|------|
| 언어 | Java 17 |
| 프레임워크 | 전자정부프레임워크 실행환경 5.0.0 (Spring Framework 6.2) |
| 빌드 | Maven (war), 부모 pom `org.egovframe.web:egovframe-web-config-parent:5.0.0` |
| 뷰 | JSP + JSTL (Jakarta) |
| 서블릿 | Jakarta Servlet 6.0 |
| 데이터 접근 | MyBatis 3.5 (`egovframe-rte-psl-dataaccess`, 의존성만 포함, 아직 미사용) |

> DB 연결, 인증 등은 사이트 요구사항이 정해진 뒤 추가합니다.

## 프로젝트 구조

```text
src/
├─ main/
│  ├─ java/egovframework/selfliveapp/
│  │  └─ web/MainController.java      # /main.do
│  ├─ resources/egovframework/spring/
│  │  └─ context-common.xml           # 공통 빈 (Controller 제외)
│  └─ webapp/
│     ├─ index.jsp                    # /main.do 로 이동
│     └─ WEB-INF/
│        ├─ web.xml
│        ├─ config-servlet.xml        # MVC, ViewResolver, Controller 스캔
│        └─ jsp/main.jsp
└─ test/java/
```

**규칙:** Controller 는 `config-servlet.xml`, 그 외 Service/DAO 는 `context-*.xml` 에서 스캔합니다.

## 브랜치·PR 규칙

`master`에 직접 push하지 않고, 모든 변경은 PR을 거쳐 머지합니다.

1. `master`에서 작업 브랜치를 만듭니다. 이름: `feat/<설명>`, `fix/<설명>`, `docs/<설명>` (예: `feat/board-list`)
2. 작업 브랜치를 push하고 `master` 대상으로 PR을 생성합니다.
3. 빌드(`mvn clean package`)가 통과하는지 확인한 뒤 머지합니다.
4. 머지한 작업 브랜치는 삭제합니다.

## 참고

- 이전 코드(eGovFrame 3.10 + 로그인/게시판 화면)는 git 기록에서 확인할 수 있습니다. 마지막 커밋: `4fd2adc`
- 3.10(Java 8, javax)에서 5.0(Java 17, jakarta)으로 올렸습니다. 이전 코드를 가져올 때는 `javax.*` → `jakarta.*` 패키지 변경이 필요합니다.

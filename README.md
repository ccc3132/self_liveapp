# self_liveapp

전자정부프레임워크(eGovFrame) 기반 웹 사이트 프로젝트입니다. 기존 포트폴리오용 코드를 모두 정리하고 뼈대부터 다시 시작합니다.

## 빠른 시작

```shell
mvn clean package
```

- 산출물: `target/self_liveapp-0.0.1-SNAPSHOT.war` (Tomcat 8/9 등 서블릿 3.1 컨테이너에 배포)
- 접속: `http://localhost:8080/<컨텍스트>/main.do`

## 기술 스택

| 항목 | 기술 |
|------|------|
| 언어 | Java 8 (빌드 JDK 8 이상) |
| 프레임워크 | 전자정부프레임워크 실행환경 3.10.0 (Spring 4.3 기반) |
| 빌드 | Maven (war) |
| 뷰 | JSP + JSTL |
| 서블릿 | 3.1 |

> DB, ORM(MyBatis 등), 인증 등은 사이트 요구사항이 정해진 뒤 추가합니다.

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

## 참고

- 이전 코드(eGovFrame 3.10 + 로그인/게시판 화면)는 git 기록에서 확인할 수 있습니다. 마지막 커밋: `4fd2adc`

# egov-web-practice

전자정부프레임워크(eGovFrame) 기반 업무 시스템(그룹웨어: 메일 발송, 일정·업무, 메신저 등) 연습 프로젝트입니다. 로컬에서는 설치 없이 바로 실행하고, 외부 DB와 연결하면 다른 사람도 접속해서 쓸 수 있는 구조를 목표로 합니다.

## 빠른 시작 (로컬)

JDK 17 이상, Maven 3.x 가 필요합니다.

```shell
mvn clean package        # 최초 1회 빌드
java -jar target/egov-web-practice.war
```

- 위 명령 대신 `run.bat`(Windows) / `run.sh`(Linux·Mac)를 실행해도 됩니다. 빌드 파일이 없으면 먼저 빌드합니다.
- 접속: `http://localhost:8080/` (`/main.do` 로 이동)
- DB: 설치 없이 H2 파일 DB가 `./data/` 에 생성되고, 테이블은 첫 실행 때 자동으로 만들어집니다.
- 포트 변경: 환경변수 `SERVER_PORT=9090`

## 외부 DB로 실행 (운영·다른 사람 접속용)

환경변수로 `prod` 프로필과 접속 정보를 주고 실행합니다. 비밀번호는 파일에 적지 않습니다.

```shell
SPRING_PROFILES_ACTIVE=prod \
DB_URL=jdbc:postgresql://localhost:5432/egovweb \
DB_USER=egov \
DB_PASSWORD=비밀번호 \
java -jar target/egov-web-practice.war
```

- 앱이 시작할 때 Flyway 가 DB의 버전을 확인하고, 비어 있으면 테이블을 전부 만들고 오래됐으면 부족한 부분만 추가합니다.
- 지원 DB: H2(로컬), PostgreSQL. MariaDB 는 드라이버와 마이그레이션 폴더를 추가하면 확장할 수 있습니다(미구현).
- 외장 Tomcat(10.1 이상, Jakarta)에 war 를 배포하는 방식도 가능합니다.

## 기술 스택

| 항목 | 기술 |
|------|------|
| 언어 | Java 17 |
| 프레임워크 | Spring Boot 3.4 (Spring Framework 6.2) + 전자정부프레임워크 실행환경 5.0.0 (`egovframe-rte-ptl-mvc`) |
| 빌드 | Maven (실행 가능한 war, 내장 Tomcat) |
| 뷰 | JSP + JSTL (Jakarta) |
| 데이터 접근 | MyBatis 3.5 (`mybatis-spring-boot-starter` 3.0.5) |
| DB 마이그레이션 | Flyway |
| DB | H2 2.3 (로컬, PostgreSQL 호환 모드) / PostgreSQL (외부) |
| 로깅 | logback (eGovFrame 이 끌고 오는 log4j2 계열은 충돌하므로 제외) |

## 프로젝트 구조

```text
src/main/
├─ java/egovframework/egovwebpractice/
│  ├─ EgovWebPracticeApplication.java   # Boot 진입점
│  ├─ web/MainController.java           # /, /main.do
│  └─ common/service/                   # Service 인터페이스, VO
│     └─ impl/                          # ServiceImpl, MyBatis Mapper
├─ resources/
│  ├─ application.yml                   # 공통 설정
│  ├─ application-local.yml             # H2 (기본 프로필)
│  ├─ application-prod.yml              # 외부 DB (환경변수로 주입)
│  ├─ mapper/*.xml                      # MyBatis SQL
│  └─ db/migration/
│     ├─ common/V1__init.sql            # 모든 DB 공통 (ANSI 호환)
│     └─ <vendor>/                      # DB별로 다른 SQL (h2, postgresql ...)
└─ webapp/WEB-INF/jsp/                  # 화면
```

**규칙**
- Controller → Service(인터페이스) → ServiceImpl → Mapper 순으로 호출합니다.
- 테이블 변경은 항상 새 마이그레이션 파일(`V2__...sql`)로 추가합니다. 이미 적용된 `V1` 같은 파일은 수정하지 않습니다.
- DB마다 문법이 달라지는 SQL만 `db/migration/<vendor>/` 에 둡니다. 페이징은 `LIMIT/OFFSET` 처럼 공통 문법을 우선 사용합니다.

## 브랜치·PR 규칙

`master`에 직접 push하지 않고, 모든 변경은 PR을 거쳐 머지합니다.

1. `master`에서 작업 브랜치를 만듭니다. 이름: `feat/<설명>`, `fix/<설명>`, `docs/<설명>` (예: `feat/board-list`)
2. 작업 브랜치를 push하고 `master` 대상으로 PR을 생성합니다.
3. 빌드(`mvn clean package`)가 통과하는지 확인한 뒤 머지합니다.
4. 머지한 작업 브랜치는 삭제합니다.

## 로드맵

1. 회원·로그인·권한(Spring Security)
2. 일정·업무(달력)
3. 메일 발송 (외부 SMTP)
4. 메신저(WebSocket)
5. 공지 게시판, 대시보드

## 참고

- 이전 코드(eGovFrame 3.10 + 로그인/게시판 화면)는 git 기록에서 확인할 수 있습니다. 마지막 커밋: `4fd2adc`
- `./data/` (H2 DB 파일)는 git 에 올리지 않습니다.

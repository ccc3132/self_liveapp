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
- DB: 설치 없이 **PostgreSQL 이 앱 안에서 함께 실행**되고(내장 PostgreSQL 14), 데이터는 `./data/pg/` 에 저장됩니다. 테이블은 첫 실행 때 자동으로 만들어집니다.
- 첫 실행은 DB 초기화 때문에 10초 안팎 걸리고, 이후에는 5초 안팎입니다.
- 앱을 강제 종료해도 다음 실행 때 남은 PostgreSQL 프로세스를 정리하고 다시 시작합니다(데이터 유지).
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
- 지원 DB: PostgreSQL (로컬은 내장, 운영은 외부). 로컬과 운영이 같은 DB 라서 SQL 호환 문제가 없습니다.
- MariaDB 등 다른 DB 는 드라이버와 `db/migration/<vendor>/` 를 추가하면 확장할 수 있습니다(미구현).
- 외장 Tomcat(10.1 이상, Jakarta)에 war 를 배포하는 방식도 가능합니다.

## 기술 스택

| 항목 | 기술 |
|------|------|
| 언어 | Java 17 |
| 프레임워크 | Spring Boot 3.5.6 (Spring Framework 6.2.11) + 전자정부프레임워크 실행환경 5.0.0 (`ptl-mvc`, `fdl-cmmn`, `fdl-logging`, `psl-dataaccess`) |
| 빌드 | Maven (실행 가능한 war, 내장 Tomcat) |
| 뷰 | JSP + JSTL (Jakarta) |
| 데이터 접근 | MyBatis 3.5 (`mybatis-spring-boot-starter` 3.0.5) |
| DB 마이그레이션 | Flyway |
| DB | PostgreSQL 14 내장(로컬, zonky `embedded-postgres` 2.2.2) / PostgreSQL (외부) |
| 로깅 | logback (eGovFrame 이 끌고 오는 log4j2 계열은 충돌하므로 제외) |

## 프로젝트 구조

```text
src/main/
├─ java/egovframework/egovwebpractice/
│  ├─ EgovWebPracticeApplication.java   # Boot 진입점
│  ├─ config/                           # FrameworkConfig(leaveaTrace, 트랜잭션), MyBatisConfig(databaseId)
│  ├─ web/                              # XxxController
│  └─ <업무>/service/                   # XxxService(인터페이스), XxxVO
│     └─ impl/                          # XxxServiceImpl, XxxDAO
├─ resources/
│  ├─ application.yml                   # 공통 설정 (메시지, 커넥션 풀)
│  ├─ application-local.yml             # 내장 PostgreSQL (기본 프로필)
│  ├─ application-prod.yml              # 외부 DB (환경변수로 주입)
│  ├─ messages/message.properties       # 예외 메시지 키
│  ├─ mapper/*.xml                      # MyBatis SQL (namespace = DAO 빈 이름)
│  └─ db/migration/
│     ├─ common/V1__init.sql            # 모든 DB 공통 (ANSI 호환)
│     └─ <vendor>/                      # DB별로 다른 SQL (postgresql ...)
└─ webapp/WEB-INF/jsp/                  # 화면
src/test/java/                          # EgovComplianceRulesTest(규칙 자동 검증), ApplicationContextTest
```

## 표준프레임워크 호환성 규칙 준수

[전자정부 표준프레임워크 호환성 가이드(2024.11.14)](https://maven.egovframe.go.kr/publist/HDD1/public/documents/%ED%98%B8%ED%99%98%EC%84%B1%ED%99%95%EC%9D%B8_%EA%B0%80%EC%9D%B4%EB%93%9C%EB%9D%BC%EC%9D%B8_20241114.pdf)의 7개 규칙을 따릅니다. 아래 표의 "자동 검증"은 `mvn package` 때 `EgovComplianceRulesTest` 가 확인하므로, 규칙을 어기면 빌드가 실패합니다.

| # | 규칙 | 이 프로젝트의 적용 | 자동 검증 |
|---|------|--------------------|-----------|
| 1-1 | 실행환경 라이브러리 변경 금지 | `org.egovframe.rte` jar 를 Maven 에서 받은 그대로 사용 (수정·재패키징 안 함). 제3자 log4j2 계열만 제외 | - (수동) |
| 1-2 | 라이브러리 적용 범위·버전 | 필수 4종(`ptl-mvc`, `fdl-cmmn`, `fdl-logging`, `psl-dataaccess`)을 모두 5.0.0 으로 통일. Spring Framework 는 실행환경 적용 버전과 같은 6.2.11 (Boot 3.5.6 이 관리하는 버전) | - (수동) |
| 2 | 실행환경 설정 | 트랜잭션: `@EnableTransactionManagement` + `@Transactional` (Java Config). 커넥션 풀: HikariCP (`spring.datasource.hikari`). 메시지·`leaveaTrace`: `FrameworkConfig` | - (수동) |
| 3 | MVC 아키텍처 | Controller 는 `@Controller` + `@GetMapping` 등 매핑, DAO·MyBatis·DataSource 직접 호출 금지, 서비스 인터페이스 주입으로만 처리 | O |
| 4 | 서비스 아키텍처 | `@Service` 클래스는 `EgovAbstractServiceImpl` 상속 + `..service` 패키지의 인터페이스 구현. 예외는 `processException(키)` | O |
| 5 | 데이터 액세스 아키텍처 | `@Repository` DAO 는 `EgovAbstractMapper` 상속, 제공 메서드(`selectOne` 등)로 SQL 호출 | O |
| 6 | 실행환경 확장 | 실행환경 클래스를 상속한 클래스는 `org.egovframe.rte` 패키지에 두지 않고 이름을 `Egov` 로 시작하지 않음 | O |
| 7 | 표준프레임워크 적용·명명 규칙 통일 | 프로젝트에 Controller·Service·DAO 가 모두 있고, 같은 명명 규칙 사용 | O |

**명명 규칙** (프로젝트 전체 동일)

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
- 테이블 변경은 항상 새 마이그레이션 파일(`V2__...sql`)로 추가합니다. 이미 적용된 `V1` 같은 파일은 수정하지 않습니다.
- DB마다 문법이 달라지는 SQL만 `db/migration/<vendor>/` 에 두고, 매퍼에서는 `databaseId` 로 구분합니다. 페이징은 `LIMIT/OFFSET` 처럼 공통 문법을 우선 사용합니다.

> 이 규칙은 위 가이드에 적힌 내용을 코드로 옮긴 것이며, 공식 호환성 확인 도구로 검사한 결과는 아닙니다. 규칙 1-1, 1-2, 2 는 사람이 확인해야 합니다.

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
- `./data/` (내장 PostgreSQL 데이터)는 git 에 올리지 않습니다.
- 내장 PostgreSQL 바이너리는 Windows·Linux(glibc, amd64)만 포함합니다. macOS/Alpine 에서 로컬 실행하려면 `pom.xml` 의 제외 항목을 지우세요. 그 때문에 war 크기가 약 78MB 입니다.

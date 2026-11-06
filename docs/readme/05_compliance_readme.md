# 5. 표준프레임워크 호환성 규칙

> [메인 README](../../README.md) · 이전: [4. 작업 방식](04_workflow_readme.md) · 다음: [6. 공통컴포넌트 도입 현황과 설계](06_components_readme.md)

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
- 테이블 변경은 항상 **새 마이그레이션 파일**로 추가합니다. **이미 적용된 마이그레이션은 주석 한 줄도 고치지 않습니다**(Flyway 체크섬 검증이 실패해 기존 DB 에서 앱이 뜨지 않음).
- DB 마다 문법이 달라지는 SQL 만 `db/migration/<vendor>/` 에 둡니다. 페이징은 `LIMIT/OFFSET` 처럼 공통 문법을 우선 사용합니다.
- `egovframework.com` 아래(공통컴포넌트 소스)는 위 명명 규칙과 자동 검증의 대상이 아닙니다("공통컴포넌트 재사용" 예외). 우리가 만든 코드와 섞지 마세요.

> 이 규칙은 가이드에 적힌 내용을 코드로 옮긴 것이며, 공식 호환성 확인 도구로 검사한 결과는 아닙니다. 규칙 1-1, 1-2, 2 는 사람이 확인해야 합니다. 가이드 외에 표준프레임워크 위키의 개발 가이드(코딩 규약 등)는 접속이 막혀 읽지 못했습니다.

---
[메인 README](../../README.md) · 이전: [4. 작업 방식](04_workflow_readme.md) · 다음: [6. 공통컴포넌트 도입 현황과 설계](06_components_readme.md)

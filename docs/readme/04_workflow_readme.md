# 4. 작업 방식

> [메인 README](../../README.md) · 이전: [3. 기술 스택과 프로젝트 구조](03_stack_readme.md) · 다음: [5. 표준프레임워크 호환성 규칙](05_compliance_readme.md)

## 브랜치·PR 규칙

`master` 에 직접 push 하지 않고, 모든 변경은 PR 을 거쳐 머지합니다.

1. `master` 에서 작업 브랜치를 만듭니다. 이름: `feat/<설명>`, `fix/<설명>`, `docs/<설명>`
2. 작업 브랜치를 push 하고 `master` 대상으로 PR 을 만듭니다.
3. `mvn clean package` 가 통과하는지 확인한 뒤 머지합니다.
4. 머지한 작업 브랜치는 삭제합니다.

- `gh`(GitHub CLI)가 없는 환경에서는 `https://github.com/<소유자>/<저장소>/compare/master...<브랜치>?quick_pull=1&title=...` 링크로 PR 화면을 엽니다.
- **링크에 본문(`body=`)을 넣으면 길이 제한(약 8KB)에 걸려 "Your request URL is too long" 오류가 납니다.** 제목만 링크에 넣고 본문은 붙여 넣으세요.
- PR 본문에는 "확인한 것 / 확인하지 못한 것"을 구분해서 적는 방식을 써 왔습니다.

## 작업을 시작하기 전에

- 새로 작업을 시작하기 전에는 먼저 [메인 README](../../README.md)와 관련 `_readme` 항목 파일을 읽어서 현재 상태·진행 상황부터 파악하세요([1. 현재 상태 한눈에](01_status_readme.md), [7. 로드맵](07_roadmap_readme.md), [8. 아직 확인하지 못한 것](08_todo_readme.md) 순으로 보면 빠릅니다).
- 문서가 길어지면 주제별로 `docs/readme/<번호>_<주제>_readme.md` 파일로 나눠서 관리합니다. 메인 README 는 목차/인덱스 역할만 하고, 특정 주제만 볼 때는 그 파일 하나만 읽으면 되게 유지하세요. 새 주제를 추가할 때는 이 폴더에 새 `_readme.md` 파일을 만들고 메인 README 의 목차에 링크를 추가하세요.

## 새 모듈을 공통컴포넌트에서 가져오는 순서

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
   - JSP 의 `<c:import>`, `<%@ include %>` 로 가져오는 다른 JSP — **이 부분이 조용히 빠지기 가장 쉽다.** 공통으로 여러 화면이 `<c:import>` 하는 작은 보조 JSP(예: 첨부파일 목록) 하나가 빠지면, 그걸 쓰는 화면 전부가 한꺼번에 500 에러가 난다([9. 겪었던 문제 모음](09_issues_readme.md) 참고).
3. 컴파일하면서 빠진 라이브러리를 `pom.xml` 에 추가합니다. 로깅 충돌이 나면 `log4j-core`, `log4j-slf4j2-impl`, `jcl-over-slf4j`, `log4j-over-slf4j` 를 제외합니다.
4. DB: 해당 모듈의 DDL/DML(`script/ddl/postgres`, `script/dml/postgres`)에서 **새로 필요한 테이블·데이터만** 새 마이그레이션(`V9__...sql` 부터)으로 추가합니다.
   - 공통컴포넌트의 모듈별 DDL 은 다른 모듈의 테이블까지 포함하므로 통째로 넣지 말고 **이미 있는 객체와 비교해 차이만** 넣으세요.
   - MySQL/Oracle 방식의 이스케이프(`'\\A...'` 처럼 역슬래시 두 개)가 있는지 확인합니다(PostgreSQL 은 그대로 저장함).
5. `@Resource(name=...)` 로 요구하는 빈을 `CommonComponentsConfig` 에 Java Config 로 추가합니다(원본은 `context-*.xml`).
6. JSP·CSS·JS·이미지·메시지·매퍼 XML 을 복사합니다. **메시지 파일은 `CommonComponentsConfig.messageSource()` 의 basename 에도 등록**해야 합니다.
7. **URL 권한을 반드시 점검합니다.** 공통컴포넌트 기본 규칙은 너무 넓습니다(회원관리 URL 은 익명 허용, 나머지 `*.do` 는 `ROLE_USER` 에도 허용). 새 관리 화면은 `COMTNROLEINFO` 에 URL 규칙을 추가해 `ROLE_ADMIN` 전용으로 만들고(`V7`, `V8` 참고), 핸들러마다 `@RequireAdmin` 또는 코드 안의 권한 검사가 있는지 확인합니다.
8. 통합 테스트를 추가합니다. `@RequireAdmin` 이 필요한 기능은 **관리자 / 일반회원 / 비로그인 세 경우**를 모두 검증하고, 권한 검사를 임시로 꺼서 테스트가 실제로 실패하는지도 확인하세요(테스트가 형식적이지 않은지).
9. **MockMvc 는 JSP 를 그리지 않으므로, 반드시 `java -jar` 로 실제 실행해서 화면을 확인합니다.** 새 DB(`./data/pg` 삭제)로 첫 실행도 확인합니다. 메시지 누락, 태그 오류, `c:import` 실패는 이때만 드러납니다. **메뉴를 열어보는 것만으로는 부족하고, 등록 폼은 실제로 저장 버튼까지 눌러 DB에 반영되는지 확인해야 진짜 동작 여부를 알 수 있습니다**([9. 겪었던 문제 모음](09_issues_readme.md)의 CKEditor·팝업 관련 사례 참고).
10. 원본 코드를 고쳤다면 [6. 공통컴포넌트 도입 현황과 설계의 "원본에서 수정한 부분"](06_components_readme.md)에 기록합니다.

---
[메인 README](../../README.md) · 이전: [3. 기술 스택과 프로젝트 구조](03_stack_readme.md) · 다음: [5. 표준프레임워크 호환성 규칙](05_compliance_readme.md)

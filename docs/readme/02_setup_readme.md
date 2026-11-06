# 2. 새 PC 에서 시작하기

> [메인 README](../../README.md) · 이전: [1. 현재 상태 한눈에](01_status_readme.md) · 다음: [3. 기술 스택과 프로젝트 구조](03_stack_readme.md)

> **2026.11 부터 개발은 이 PC(Windows) 한 대로만 고정합니다.** 이 장은 그래서 더 이상
> 다른 환경과의 호환을 검증하는 절차가 아니라, 이 PC에 이미 깔려 있는 도구의 기록입니다. 아래 "이 PC의 고정 환경"을 참고하세요.

## 이 PC의 고정 환경 (2026.11 ~)

개발을 이 Windows PC 한 대로 고정하기로 하면서, 아래 도구들을 이 PC에 직접 설치했습니다(모두 시스템 PATH 에는
없어서, 새 셸을 열 때마다 아래처럼 `$env:Path` 에 추가해야 합니다 — PowerShell 세션 사이에 변수나 PATH 가
유지되지 않습니다).

| 도구 | 경로 | 설치 방법 |
|------|------|-----------|
| JDK 21 (Microsoft Build of OpenJDK) | `C:\Program Files\Microsoft\jdk-21.*` | `winget install Microsoft.OpenJDK.21` |
| Maven 3.9.9 | `C:\Tools\apache-maven-3.9.9` | winget 에 패키지가 없어 `archive.apache.org` 에서 직접 압축 해제 |
| Git | `C:\Program Files\Git` | `winget install Git.Git` |
| GitHub CLI (`gh`) | `C:\Program Files\GitHub CLI` | `winget install GitHub.cli`, `gh auth login --web` 로 ccc3132 계정 인증(디바이스 코드), `gh auth setup-git` 로 git 자격 증명 연결 |
| Node.js LTS | `C:\Program Files\nodejs` | `winget install OpenJS.NodeJS.LTS` (모바일 앱(`mobile/`) 빌드용) |
| 저장소 로컬 경로 | `C:\workspace\self_liveapp` | `git clone https://github.com/ccc3132/self_liveapp.git` |

매번 새 셸에서 빌드/실행하려면:
```powershell
$jdkPath = Get-ChildItem "C:\Program Files\Microsoft" -Directory -Filter "jdk*" | Select-Object -First 1 -ExpandProperty FullName
$env:JAVA_HOME = $jdkPath
$env:Path = "$jdkPath\bin;C:\Tools\apache-maven-3.9.9\bin;C:\Program Files\Git\cmd;C:\Program Files\GitHub CLI;$env:Path"
```

Git 작성자 정보는 전역이 아니라 이 저장소에만 로컬로 설정되어 있습니다(`choi seong keun <ccc3132@naver.com>`,
저장소 폴더에서 `git config user.name`/`user.email`로 확인).

## 준비물

| 항목 | 버전 | 비고 |
|------|------|------|
| JDK | 17 이상 | 21 에서 빌드·테스트함 |
| Maven | 3.x | 3.8.5 에서 확인 |
| Git | 최신 | |
| OS | Windows(amd64) 또는 Linux(glibc, amd64) | 내장 PostgreSQL 바이너리가 이 둘만 포함됨. macOS/Alpine 은 아래 참고 |

인터넷이 필요합니다(첫 빌드 때 라이브러리와 내장 PostgreSQL 바이너리를 받음). 표준프레임워크 Maven 저장소(`maven.egovframe.go.kr`)에서 일부 라이브러리를 받습니다.

## 1) 받기 전에: Git 작성자 설정 (중요)

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

## 2) 빌드와 실행

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

## 3) 로그인 정보

공통컴포넌트의 초기 데이터입니다. 비밀번호는 모두 `rhdxhd12`.

| 아이디 | 구분(`userSe`) | 권한 |
|--------|----------------|------|
| `TEST1`, `webmaster` | 업무사용자(`USR`) | `ROLE_ADMIN` |
| `USER` | 일반회원(`GNR`) | `ROLE_USER` |

> **주의:** 이 계정과 비밀번호는 공통컴포넌트 문서에 공개된 값입니다. **다른 사람이 접속할 수 있는 환경에 배포하기 전에 반드시 비밀번호를 바꾸거나 계정을 삭제하세요.** 연속 5회 로그인에 실패하면 계정이 잠깁니다.

## 화면 확인 체크리스트

새 PC 에서 처음 한 번, 아래를 브라우저로 열어 모양이 이상한 곳을 찾으세요(CSS·이미지 누락 여부).

1. `http://localhost:8080/` 로그인 화면 (구분을 "업무사용자"로 선택하고 `TEST1` 로 로그인)
2. `/uss/umt/EgovEmplyrManage.do` 직원 목록, 등록 화면, 페이지 번호
3. `/sec/rgm/EgovAuthorGroupList.do` 권한 지정 화면
4. 직원 등록 → 권한 지정 → 그 직원으로 로그인하는 흐름
5. 사이드바 전체 메뉴를 하나씩 클릭해 끝까지 들어가 보고, 등록 화면은 실제로 저장까지 눌러 DB 반영을 확인(화면만 뜨는지 확인하는 것만으론 부족함 — [9. 겪었던 문제 모음](09_issues_readme.md) 참고)

## 테스트와 DB 조회

- `mvn clean package` 가 모든 테스트를 실행합니다. 테스트는 각자 별도의 내장 PostgreSQL(`target/test-pg-*`)을 띄웁니다.
- 실행 중인 로컬 DB 를 들여다보려면 `tools/DbQuery.java` 를 쓰세요(포트는 앱 로그의 `postmaster started ... on port N`).
- macOS/Alpine 에서 실행하려면 `pom.xml` 의 `embedded-postgres` 제외 항목(`darwin-amd64`, `linux-amd64-alpine`)을 지우세요. 제외한 이유는 war 용량(약 120MB → 약 90MB)입니다. Apple Silicon 용 바이너리는 포함된 적이 없어 별도 확인이 필요합니다.

## 외부 DB 로 실행 (운영·다른 사람 접속용)

환경변수로 `prod` 프로필과 접속 정보를 주고 실행합니다. 비밀번호는 파일에 적지 않습니다.

```shell
SPRING_PROFILES_ACTIVE=prod \
DB_URL=jdbc:postgresql://localhost:5432/egovweb \
DB_USER=egov \
DB_PASSWORD=비밀번호 \
java -jar target/egov-web-practice.war
```

- 앱이 시작할 때 Flyway 가 DB 버전을 확인하고, 비어 있으면 테이블을 전부 만들고 오래됐으면 부족한 부분만 추가합니다.
- **`prod` 프로필로 실제 실행해 본 적은 없습니다**([8. 아직 확인하지 못한 것](08_todo_readme.md)).
- 외부 SMTP(메일 발송)는 환경변수 `APP_MAIL_HOST`, `APP_MAIL_PORT`, `APP_MAIL_USERNAME`, `APP_MAIL_PASSWORD`, `APP_MAIL_SENDER_NAME`, `APP_MAIL_SENDER_ADDRESS` 로 줍니다(`CommonComponentsConfig`). 환경변수 이름은 Spring 의 이름 변환 규칙을 따른 것입니다. SMTP 미설정 시에는 20초 안에 실패 처리됩니다([9. 겪었던 문제 모음](09_issues_readme.md) 참고).
- 외장 Tomcat(10.1 이상, Jakarta)에 war 를 올리는 방식도 가능하지만 확인하지 않았습니다.
- **내 서버가 없거나 설치 과정 없이 바로 올리고 싶다면 [10. 외부 서버에 배포하기](10_deploy_readme.md)** 를 보세요(Docker Compose 한 줄 실행, Render 원클릭 배포).

---
[메인 README](../../README.md) · 이전: [1. 현재 상태 한눈에](01_status_readme.md) · 다음: [3. 기술 스택과 프로젝트 구조](03_stack_readme.md)

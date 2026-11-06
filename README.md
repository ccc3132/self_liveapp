# egov-web-practice

전자정부 표준프레임워크(eGovFrame) 기반 **업무 시스템(그룹웨어) 연습 프로젝트**입니다. 목표는 로그인·직원·권한 같은 기본 기능 위에 게시판, 일정, 쪽지, 메일 발송, 메신저를 쌓는 것입니다. 로컬에서는 설치 없이 바로 실행되고, 외부 PostgreSQL 에 연결하거나 Docker/Render 로 외부 서버에 올리면 다른 사람도 접속해서 쓸 수 있는 구조입니다.

> GitHub 저장소 이름은 `self_liveapp` 입니다(코드 안의 프로젝트 이름은 `egov-web-practice`).
> **2026.11 부터 개발은 이 PC(Windows) 한 대로만 고정합니다.** [2. 새 PC 에서 시작하기](docs/readme/02_setup_readme.md)의
> "새 PC" 절차는 그래서 더 이상 다른 환경과의 호환을 검증하는 절차가 아니라, 이 PC에 이미 깔려 있는 도구의 기록입니다.
> **이 문서는 목차만 담은 인덱스입니다.** 각 항목의 실제 내용은 `docs/readme/` 아래 번호별 파일에 나뉘어 있습니다 — 전체를 다 읽을 필요 없이, 필요한 항목 파일 하나만 열어 보면 됩니다. 처음이면 0번부터 순서대로 읽으세요.

## 목차

| # | 항목 | 파일 |
|---|------|------|
| 0 | 화면 구성 둘러보기 (처음 보는 사람용) | [docs/readme/00_screens_readme.md](docs/readme/00_screens_readme.md) |
| 1 | 현재 상태 한눈에 | [docs/readme/01_status_readme.md](docs/readme/01_status_readme.md) |
| 2 | 새 PC 에서 시작하기 (설치·실행·로그인 정보) | [docs/readme/02_setup_readme.md](docs/readme/02_setup_readme.md) |
| 3 | 기술 스택과 프로젝트 구조 | [docs/readme/03_stack_readme.md](docs/readme/03_stack_readme.md) |
| 4 | 작업 방식 (브랜치·PR·새 모듈 도입 절차) | [docs/readme/04_workflow_readme.md](docs/readme/04_workflow_readme.md) |
| 5 | 표준프레임워크 호환성 규칙 | [docs/readme/05_compliance_readme.md](docs/readme/05_compliance_readme.md) |
| 6 | 공통컴포넌트 도입 현황과 설계 (인증·포털 셸·모바일 API 등) | [docs/readme/06_components_readme.md](docs/readme/06_components_readme.md) |
| 7 | 로드맵 (다음 할 일) | [docs/readme/07_roadmap_readme.md](docs/readme/07_roadmap_readme.md) |
| 8 | 아직 확인하지 못한 것 | [docs/readme/08_todo_readme.md](docs/readme/08_todo_readme.md) |
| 9 | 겪었던 문제 모음 | [docs/readme/09_issues_readme.md](docs/readme/09_issues_readme.md) |
| 10 | 외부 서버에 배포하기 (Docker/Render) | [docs/readme/10_deploy_readme.md](docs/readme/10_deploy_readme.md) |
| 11 | 디자인/UX 가이드라인 (모달/팝업 등) | [docs/readme/11_design_readme.md](docs/readme/11_design_readme.md) |

모바일 앱(Expo/React Native)은 `mobile/` 폴더의 별도 프로젝트입니다 — [mobile/README.md](mobile/README.md) 참고.

## 새 항목을 추가하려면

문서가 길어질 때는 주제별로 `docs/readme/<번호>_<주제>_readme.md` 파일을 새로 만들고, 위 목차 표에 한 줄을 추가하세요. 메인 README(이 파일)는 인덱스 역할만 유지하고, 실제 설명은 해당 항목 파일 안에만 둡니다. 작업을 시작하기 전에는 이 README 와 관련 `_readme` 파일을 먼저 읽어 현재 상태를 파악하세요.

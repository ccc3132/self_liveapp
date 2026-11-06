# 10. 외부 서버에 배포하기 (Docker)

> [메인 README](../../README.md) · 이전: [9. 겪었던 문제 모음](09_issues_readme.md)

`Dockerfile`, `docker-compose.yml`, `render.yaml` 을 추가했습니다. **이 PC에 Docker 가 설치되어 있지 않아
실제로 이미지가 빌드되는지는 아직 확인하지 못했습니다** — 코드와 `pom.xml`을 보고 작성한 구성이니, 처음
쓸 때는 로그를 보면서 진행하세요.

## 방법 A: 내 서버에서 `docker compose up` (사설 서버·VPS 등 직접 관리하는 서버용)

```shell
git clone https://github.com/ccc3132/self_liveapp.git
cd self_liveapp
cp .env.example .env
# .env 를 열어 DB_PASSWORD, APP_JWT_SECRET 을 직접 정한 값으로 바꾸세요
docker compose up -d --build
```

- `http://서버주소:8080/` 으로 접속합니다. 포트를 바꾸려면 `.env` 의 `APP_PORT`.
- PostgreSQL 데이터는 `db_data` 라는 Docker volume 에 남아서, 컨테이너를 내렸다 올려도 유지됩니다.
- **HTTPS 리버스 프록시(Caddy·nginx·Cloudflare Tunnel 등)를 앞에 두는 것을 권장합니다.** 프록시 없이
  평문 HTTP 로 바로 열면 `.env` 의 `COOKIE_SECURE=false` 를 유지해야 로그인이 됩니다(기본값). HTTPS
  뒤에 두면 `COOKIE_SECURE=true` 로 바꾸세요.
- 로그 확인: `docker compose logs -f app`. 중지: `docker compose down`(데이터 유지) / `docker compose down -v`(데이터까지 삭제).

## 방법 B: Render 에 원클릭 배포 (내 서버가 없어도 됨)

[![Deploy to Render](https://render.com/images/deploy-to-render-button.svg)](https://render.com/deploy?repo=https://github.com/ccc3132/self_liveapp)

버튼을 누르면 Render 가 `render.yaml` 을 읽어서 **웹 서비스(Docker 빌드) + PostgreSQL** 을 한 번에 만듭니다.
DB 접속정보와 `APP_JWT_SECRET` 은 Render 가 자동으로 생성해서 연결해 줍니다(`docker-entrypoint.sh` 가
Render 의 `DATABASE_URL`, `PORT` 형식을 이 앱이 읽는 `DB_URL`, `SERVER_PORT` 형식으로 변환합니다).

- 무료(Free) 플랜 기준입니다. 일정 시간 요청이 없으면 서비스가 잠들고(다음 요청 때 다시 깨는 데 수십 초
  걸릴 수 있음), 무료 PostgreSQL 은 일정 기간(가입 시점 기준 약 30일) 후 만료됩니다 — Render 정책이 바뀔
  수 있으니 콘솔에서 확인하세요. 계속 쓰려면 유료 플랜으로 올리거나 DB 를 새로 연결하세요.
- 빌드는 `Dockerfile` 을 그대로 씁니다(Maven 으로 war 를 빌드 → JRE 이미지에 올림). 첫 빌드는 라이브러리를
  새로 받기 때문에 몇 분 걸립니다.
- 배포 후 접속 → 로그인은 [2. 새 PC 에서 시작하기의 "로그인 정보"](02_setup_readme.md#3-로그인-정보)의 초기 계정으로 됩니다. **다른 사람도 볼 수 있는
  주소가 되는 순간, 반드시 비밀번호를 바꾸거나 계정을 정리하세요.**
- Render 외에 Railway 등 `DATABASE_URL`(postgres://user:pass@host:port/db) 환경변수로 DB 를 연결해 주는
  다른 PaaS 에도 같은 `Dockerfile`/`docker-entrypoint.sh` 로 배포할 수 있을 것으로 보이나(동일한 변환
  로직), 실제로 확인한 것은 아닙니다.

## 구성 파일 설명

| 파일 | 역할 |
|------|------|
| `Dockerfile` | 2단계 빌드: `maven:3.9-eclipse-temurin-21` 로 war 를 빌드하고, `eclipse-temurin:21-jre-alpine` 에 war 하나만 올려서 이미지를 가볍게 유지 |
| `docker-entrypoint.sh` | PaaS 가 주는 `DATABASE_URL`(postgres://...)을 이 앱의 `DB_URL`(jdbc:postgresql://...) 형식으로, `PORT` 를 `SERVER_PORT` 로 변환한 뒤 `java -jar` 실행. 둘 다 없으면 그대로 통과(기존 `DB_URL`/`SERVER_PORT` 방식도 계속 됨) |
| `docker-compose.yml` | 앱 컨테이너 + PostgreSQL 컨테이너를 한 번에 띄움(`prod` 프로필) |
| `.env.example` | `docker-compose.yml` 이 읽는 값의 틀. `cp .env.example .env` 로 복사해서 실제 값을 채움(`.env` 자체는 git에 올라가지 않음) |
| `render.yaml` | Render 블루프린트. "Deploy to Render" 버튼이 이 파일을 읽어 서비스를 구성 |
| `.dockerignore` | 이미지 빌드에 불필요한 것(`target/`, `data/`, `mobile/`, 문서 등) 제외 |

## 아직 확인하지 못한 것 (이 장 전용)

- `docker build`/`docker compose up` 이 실제로 성공하는지(이 PC에 Docker 미설치)
- Render 원클릭 배포가 실제로 끝까지 되는지(버튼을 눌러 실행해 보지 않음)
- Railway 등 다른 PaaS 와의 호환성(형식만 맞춰 작성, 실제 배포는 안 해봄)
- 외장 PostgreSQL 에 `prod` 프로필로 실제 접속해 본 적이 없다는 점은 [8. 아직 확인하지 못한 것](08_todo_readme.md)과 동일하게 적용됨

---
[메인 README](../../README.md) · 이전: [9. 겪었던 문제 모음](09_issues_readme.md)

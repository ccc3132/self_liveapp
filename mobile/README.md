# egov-web-practice 모바일 앱 (Expo / React Native)

기존 JSP 웹 화면과는 별개로, 백엔드의 `/api/**` JWT REST API(PR #23)를 쓰는 네이티브 앱입니다.
Phase 1 범위: 로그인, 대시보드(오늘 일정/최근 공지/안 읽은 쪽지/접속 중인 사용자) 2개 화면만 구현되어 있습니다.

## 실행 방법

1. 백엔드를 먼저 띄운다: 저장소 루트에서 `java -jar target/egov-web-practice.war` (포트 8080)
2. `src/api.ts`의 `API_BASE_URL`을 PC의 LAN IP로 맞춘다(휴대폰의 `localhost`는 휴대폰 자신을 가리키므로 동작하지 않음). PC와 휴대폰이 같은 Wi-Fi에 있어야 한다.
3. PC 방화벽에서 8080(백엔드), 8081(Expo 개발 서버) 포트를 사설망(Private)에 한해 허용한다.
4. `npm install` (최초 1회)
5. `npx expo start --lan`
6. 휴대폰에 Expo Go 앱을 설치하고, 터미널에 뜨는 QR코드를 스캔한다.

## 확인한 것
- 로그인(업무/일반/기업 탭) → 토큰을 `expo-secure-store`에 저장 → 대시보드 조회까지 코드 레벨에서는 완성.
- `npx tsc --noEmit` 통과.

## 아직 확인하지 못한 것
- 실제 휴대폰(Expo Go)에서의 수동 확인은 이 세션에서 하지 못함(개발 PC에만 접근 가능) — 다음 단계로 직접 확인이 필요합니다.
- 사이드바의 다른 메뉴(게시판, 일정관리, 쪽지, 메일 등)는 아직 API/화면이 없습니다. 이번 PR은 로그인+대시보드까지의 1단계입니다.

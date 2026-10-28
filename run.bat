@echo off
rem 로컬 실행: 더블클릭하면 H2 DB로 바로 실행된다. (JDK 17 이상 필요)
rem 외부 DB: 환경변수 SPRING_PROFILES_ACTIVE=prod, DB_URL, DB_USER, DB_PASSWORD 를 지정한 뒤 실행
chcp 65001 >nul
cd /d "%~dp0"
if not exist target\egov-web-practice.war (
    echo 빌드된 파일이 없어 먼저 빌드합니다...
    call mvn -q -DskipTests clean package || exit /b 1
)
java -jar target\egov-web-practice.war %*
pause

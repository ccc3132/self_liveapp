#!/bin/sh
# 로컬 실행: 내장 PostgreSQL 로 바로 실행된다. (JDK 17 이상 필요)
# 외부 DB: SPRING_PROFILES_ACTIVE=prod DB_URL=... DB_USER=... DB_PASSWORD=... ./run.sh
cd "$(dirname "$0")" || exit 1
if [ ! -f target/egov-web-practice.war ]; then
    echo "빌드된 파일이 없어 먼저 빌드합니다..."
    mvn -q -DskipTests clean package || exit 1
fi
exec java -jar target/egov-web-practice.war "$@"

#!/bin/sh
# Render/Railway 같은 PaaS 는 DB 접속정보를 DB_URL(JDBC 형식)이 아니라
# DATABASE_URL=postgres://user:pass@host:port/dbname 형태로 준다. 앱은 DB_URL/DB_USER/DB_PASSWORD
# 형식만 읽으므로(application-prod.yml), 여기서 있으면 변환해 준다.
if [ -n "$DATABASE_URL" ] && [ -z "$DB_URL" ]; then
  rest="${DATABASE_URL#postgres://}"
  rest="${rest#postgresql://}"
  userpass="${rest%%@*}"
  hostpart="${rest#*@}"
  export DB_USER="${userpass%%:*}"
  export DB_PASSWORD="${userpass#*:}"
  export DB_URL="jdbc:postgresql://${hostpart}"
fi

# Render/Railway 는 리스닝 포트를 PORT 환경변수로 지정한다. 앱은 SERVER_PORT 를 읽는다.
if [ -n "$PORT" ] && [ -z "$SERVER_PORT" ]; then
  export SERVER_PORT="$PORT"
fi

exec java -jar /app/app.war "$@"

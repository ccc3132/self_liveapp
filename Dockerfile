# 빌드 단계: Maven + JDK 21 로 실행 가능한 war 를 만든다.
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /build
COPY pom.xml .
RUN mvn -q -B dependency:go-offline || true
COPY src ./src
RUN mvn -q -B clean package -DskipTests

# 실행 단계: JRE 만 있는 가벼운 이미지에 war 하나만 올린다.
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /build/target/egov-web-practice.war app.war
COPY docker-entrypoint.sh /app/docker-entrypoint.sh
RUN chmod +x /app/docker-entrypoint.sh
EXPOSE 8080
ENTRYPOINT ["/app/docker-entrypoint.sh"]

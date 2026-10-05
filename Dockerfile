# Build stage
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app
COPY . .
COPY .mvn .mvn
COPY mvnw .
COPY pom.xml .
RUN ./mvnw dependency:go-offline -B
COPY src ./src
RUN ./mvnw clean package -DskipTests

# Run Time stage
FROM eclipse-temurin:21-jre

ARG PROFILE=dev
ARG APP_VERSION=1.0.0

WORKDIR /app

RUN groupadd -r spring && useradd -r -g spring spring

COPY --from=builder /app/target/SupplyChainX-${APP_VERSION}.jar /app/app.jar

RUN mkdir -p /app/logs && chown -R spring:spring /app

USER spring

EXPOSE 8080

ENV DB_URL=jdbc:postgresql://db:5432/supply_db
ENV ACTIVE_PROFILE=${PROFILE}
ENV JAR_VERSION=${APP_VERSION}

HEALTHCHECK --interval=30s --timeout=5s \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

CMD java -jar -Dspring.profiles.active=${ACTIVE_PROFILE} -Dspring.datasource.url=${DB_URL} app.jar
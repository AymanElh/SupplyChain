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
WORKDIR /app
RUN groupadd -r spring && useradd -r -g spring spring
COPY --from=builder /app/target/SupplyChainX-0.0.1-SNAPSHOT.jar /app/myapp.jar
RUN chown -R spring:spring /app
USER spring
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=5s \
  CMD curl -f http://localhost:8080/health || exit 1
ENTRYPOINT ["java", "-jar", "/app/myapp.jar"]
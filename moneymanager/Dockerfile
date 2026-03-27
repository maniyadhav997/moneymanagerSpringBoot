# Stage 1: Build
FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /app
COPY .. .
RUN mvn clean package -DskipTests

# Stage 2: Run
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/target/moneymanager-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 9090
ENTRYPOINT ["java", "-jar", "app.jar"]
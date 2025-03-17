# Dockerfile para el backend Spring Boot
FROM gradle:8.5-jdk17-alpine AS build
WORKDIR /app
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY src src
RUN ls -la
RUN chmod +x ./gradlew
RUN ./gradlew bootJar --info --stacktrace

FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"] 
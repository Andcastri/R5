# Dockerfile para el backend Spring Boot
FROM gradle:7.6.1-jdk17-alpine AS build
WORKDIR /app
COPY backend/gradlew .
COPY backend/gradle gradle
COPY backend/build.gradle.kts .
COPY backend/settings.gradle.kts .
COPY backend/src src
RUN ls -la
RUN chmod +x ./gradlew
RUN ./gradlew bootJar --info --stacktrace

FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"] 
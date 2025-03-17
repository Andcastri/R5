# Dockerfile para el backend Spring Boot
FROM gradle:8.5-jdk17-alpine AS build
WORKDIR /app
COPY backend .
RUN ls -la
RUN pwd
RUN chmod +x ./gradlew
RUN ./gradlew clean bootJar --info --stacktrace --no-daemon

FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"] 
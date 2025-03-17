# Dockerfile para el backend Spring Boot
FROM gradle:7.6.1-jdk17-alpine AS build
WORKDIR /app
COPY backend .
RUN gradle bootJar --no-daemon --info --stacktrace

FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"] 
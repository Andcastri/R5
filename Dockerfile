# Dockerfile para el backend Spring Boot
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app
COPY backend .
RUN ls -la
RUN pwd
RUN find . -type f
RUN apk add --no-cache gradle
RUN gradle --version
RUN gradle clean bootJar --info --stacktrace --no-daemon

FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"] 
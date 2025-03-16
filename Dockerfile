# Dockerfile para el backend Spring Boot
FROM gradle:7.6.1-jdk17-alpine AS build
WORKDIR /app
COPY backend .
RUN chmod +x ./gradlew
RUN ./gradlew bootJar --no-daemon

FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
ENTRYPOINT ["java","-jar","/app/app.jar"] 
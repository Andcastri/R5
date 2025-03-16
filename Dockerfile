# Dockerfile para el backend Spring Boot
FROM gradle:7.6.1-jdk17-alpine AS build
WORKDIR /backend
COPY backend/ .
RUN chmod +x gradlew
RUN ./gradlew build --info --stacktrace

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /backend/build/libs/*.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"] 
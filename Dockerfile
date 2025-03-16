# Dockerfile para el backend Spring Boot
FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
COPY backend/build/libs/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"] 
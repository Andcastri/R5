# Dockerfile para la aplicación Android
FROM gradle:7.6.1-jdk17-alpine AS build
WORKDIR /app
COPY . .
RUN chmod +x gradlew
RUN ./gradlew :app:build --info --stacktrace

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/app/build/libs/*.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"] 
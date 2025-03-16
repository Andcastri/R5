FROM gradle:7.6.1-jdk17-alpine
WORKDIR /app
COPY . .
RUN chmod +x gradlew
RUN ./gradlew :app:build --info --stacktrace

EXPOSE 8080
CMD ["java", "-jar", "app/build/libs/*.jar"] 
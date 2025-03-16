FROM gradle:7.6.1-jdk17-alpine
WORKDIR /app
COPY . .
RUN chmod +x gradlew
RUN ./gradlew build

EXPOSE 8080
CMD ["java", "-jar", "build/libs/*.jar"] 
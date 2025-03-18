FROM maven:3.8.4-openjdk-17
WORKDIR /app
COPY . .
RUN cd backend && mvn clean package -DskipTests
CMD ["java", "-jar", "backend/target/*.jar"] 
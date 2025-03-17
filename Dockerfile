# Dockerfile para el backend Spring Boot
FROM eclipse-temurin:17 AS build

WORKDIR /app

# Copiar solo los archivos necesarios primero para aprovechar la caché de capas
COPY backend/gradlew .
COPY backend/gradle gradle
COPY backend/settings.gradle.kts .
COPY backend/build.gradle.kts .

# Dar permisos de ejecución al Gradle Wrapper
RUN chmod +x ./gradlew

# Mostrar información del entorno
RUN pwd && ls -la
RUN java -version
RUN ./gradlew --version

# Copiar el resto del código fuente
COPY backend/src src

# Mostrar estructura del proyecto
RUN echo "Estructura del proyecto:"
RUN find . -type f

# Construir el proyecto
RUN ./gradlew clean bootJar --info --stacktrace --no-daemon

FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"] 
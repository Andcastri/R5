# Dockerfile para el backend Spring Boot
FROM eclipse-temurin:17 AS build

# Instalar herramientas necesarias
RUN apt-get update && apt-get install -y \
    bash \
    curl \
    unzip \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Copiar archivos del proyecto
COPY backend/gradlew .
COPY backend/gradle gradle
COPY backend/settings.gradle.kts .
COPY backend/build.gradle.kts .
RUN chmod +x ./gradlew

# Mostrar información del entorno
RUN pwd && ls -la
RUN echo "Contenido del directorio gradle:"
RUN ls -la gradle
RUN echo "Contenido del directorio gradle/wrapper:"
RUN ls -la gradle/wrapper
RUN java -version

# Copiar el código fuente
COPY backend/src src

# Mostrar estructura del proyecto
RUN echo "Estructura del proyecto:"
RUN find . -type f
RUN echo "Contenido de build.gradle.kts:"
RUN cat build.gradle.kts
RUN echo "Contenido de settings.gradle.kts:"
RUN cat settings.gradle.kts

# Construir el proyecto
RUN ./gradlew --version
RUN ./gradlew clean bootJar --info --stacktrace --no-daemon --debug

FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"] 
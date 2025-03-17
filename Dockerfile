# Dockerfile para el backend Spring Boot
FROM eclipse-temurin:17 AS build

# Instalar herramientas necesarias
RUN apt-get update && apt-get install -y \
    bash \
    curl \
    unzip \
    && rm -rf /var/lib/apt/lists/*

# Instalar Gradle
ENV GRADLE_VERSION=8.5
ENV GRADLE_HOME=/opt/gradle
RUN curl -L https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip -o gradle.zip && \
    mkdir -p ${GRADLE_HOME} && \
    unzip gradle.zip && \
    mv gradle-${GRADLE_VERSION}/* ${GRADLE_HOME} && \
    rm gradle.zip
ENV PATH=$PATH:${GRADLE_HOME}/bin

WORKDIR /app

# Copiar archivos del proyecto
COPY backend/settings.gradle.kts .
COPY backend/build.gradle.kts .

# Mostrar información del entorno
RUN pwd && ls -la
RUN java -version
RUN gradle --version

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
RUN gradle clean build --info --stacktrace --no-daemon --debug

FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"] 
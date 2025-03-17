FROM eclipse-temurin:17-jdk AS build

WORKDIR /app

# Instalar Maven
RUN apt-get update && \
    apt-get install -y maven && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Copiar el proyecto completo
COPY backend .

# Verificar estructura del proyecto
RUN echo "Verificando estructura del proyecto..." && \
    mkdir -p src/main/kotlin/com/example/backend && \
    mkdir -p src/main/resources && \
    echo "Estructura del proyecto:" && \
    find . -type f && \
    echo "Contenido de pom.xml:" && \
    cat pom.xml && \
    echo "Contenido de src/main/kotlin:" && \
    ls -la src/main/kotlin/ && \
    echo "Contenido de src/main/resources:" && \
    ls -la src/main/resources/

# Intentar el build con más información y sin caché
RUN mvn clean package -DskipTests -X -U

# Imagen final
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"] 
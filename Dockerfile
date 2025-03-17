FROM eclipse-temurin:17-jdk AS build

WORKDIR /app

# Instalar Maven
RUN apt-get update && \
    apt-get install -y maven && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Copiar solo los archivos necesarios para el build
COPY backend/pom.xml .
COPY backend/src ./src

# Mostrar estructura y contenido
RUN echo "Estructura del proyecto:" && \
    find . -type f && \
    echo "Contenido de pom.xml:" && \
    cat pom.xml

# Intentar el build con más información
RUN mvn clean package -DskipTests -X

# Imagen final
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"] 
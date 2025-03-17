FROM eclipse-temurin:17-jdk AS build

WORKDIR /app

# Instalar Maven
RUN apt-get update && \
    apt-get install -y maven && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Copiar el proyecto completo
COPY backend .

# Mostrar estructura y contenido
RUN echo "Estructura del proyecto:" && \
    find . -type f && \
    echo "Contenido de pom.xml:" && \
    cat pom.xml

# Intentar el build con más información y sin caché
RUN mvn clean package -DskipTests -X -U

# Imagen final
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"] 
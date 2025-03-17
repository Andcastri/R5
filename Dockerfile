FROM eclipse-temurin:17-jdk AS build

WORKDIR /app

# Instalar Maven
RUN apt-get update && \
    apt-get install -y maven && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Copiar el proyecto
COPY backend .

# Mostrar estructura
RUN echo "Estructura del proyecto:" && \
    find . -type f

# Intentar el build
RUN mvn clean package -DskipTests

# Imagen final
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"] 
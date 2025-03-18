# Etapa de build
FROM eclipse-temurin:17-jdk AS builder
WORKDIR /app

# Instalar Maven
RUN apt-get update && \
    apt-get install -y maven && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Copiar el proyecto
COPY backend /app/backend

# Verificar estructura del proyecto
RUN ls -la /app/backend && \
    cat /app/backend/pom.xml

# Intentar el build
RUN cd /app/backend && \
    mvn clean package -DskipTests

# Etapa final
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/backend/target/*.jar app.jar
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/ || exit 1
ENTRYPOINT ["java", "-jar", "app.jar"] 
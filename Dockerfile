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

# Intentar el build
RUN cd /app/backend && \
    mvn clean package -DskipTests

# Etapa final
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Instalar herramientas de diagnóstico
RUN apk add --no-cache curl wget

# Copiar el JAR
COPY --from=builder /app/backend/target/*.jar app.jar

# Exponer puerto
EXPOSE 8080

# Health check mejorado
HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/ || exit 1

# Comando de inicio con más información de diagnóstico
ENTRYPOINT ["sh", "-c", "java -jar app.jar 2>&1 | tee /app/app.log"] 
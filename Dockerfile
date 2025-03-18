# Build stage
FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /app

# Copiar el pom.xml primero para aprovechar la caché de dependencias
COPY backend/pom.xml .
RUN mvn dependency:go-offline

# Copiar el código fuente y construir
COPY backend/src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Instalar herramientas básicas para diagnóstico
RUN apk add --no-cache curl

# Copiar el JAR desde la etapa de build
COPY --from=build /app/target/*.jar app.jar

# Crear directorio para uploads con permisos adecuados
RUN mkdir -p /uploads && chmod 777 /uploads

# Variables de entorno por defecto
ENV PORT=8080
ENV UPLOAD_DIR=/uploads
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=30s --retries=3 \
  CMD curl -f http://localhost:${PORT}/ || exit 1

# Exponer puerto
EXPOSE ${PORT}

# Comando de inicio
CMD java $JAVA_OPTS -jar app.jar 
FROM eclipse-temurin:17-jdk-alpine as build
WORKDIR /workspace/app

# Copiar archivos del proyecto
COPY backend/mvnw .
COPY backend/.mvn .mvn
COPY backend/pom.xml .
COPY backend/src src

# Dar permisos de ejecución al mvnw
RUN chmod +x ./mvnw

# Construir el proyecto
RUN ./mvnw install -DskipTests

# Imagen final
FROM eclipse-temurin:17-jre-alpine
VOLUME /tmp
COPY --from=build /workspace/app/target/*.jar app.jar

# Crear directorio para uploads
RUN mkdir -p /uploads && chmod 777 /uploads

# Variables de entorno por defecto
ENV PORT=8080
ENV UPLOAD_DIR=/uploads

EXPOSE ${PORT}
ENTRYPOINT ["java","-jar","/app.jar"] 
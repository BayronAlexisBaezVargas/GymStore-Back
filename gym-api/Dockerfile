# ==========================================
# Etapa 1: Construcción (Build)
# ==========================================
# Usamos una imagen que tenga Maven y Java 17 para compilar
FROM maven:3.9-eclipse-temurin-17-alpine AS builder

# Establecemos el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiamos el pom.xml primero para aprovechar la caché de Docker
COPY pom.xml .

# Descargamos las dependencias (esto acelera futuras construcciones)
RUN mvn dependency:go-offline

# Copiamos el código fuente de tu API
COPY src ./src

# Compilamos y empaquetamos la aplicación (Saltamos los tests aquí porque
# GitHub Actions se encargará de correrlos más adelante)
RUN mvn clean package -DskipTests

# ==========================================
# Etapa 2: Producción (Runtime)
# ==========================================
# Usamos una imagen súper liviana que solo tiene Java (JRE), sin Maven
FROM eclipse-temurin:17-jre-alpine

# Directorio de trabajo
WORKDIR /app

# Copiamos SOLAMENTE el archivo .jar compilado desde la Etapa 1
COPY --from=builder /app/target/*.jar app.jar

# Exponemos el puerto estándar de Spring Boot
EXPOSE 8080

# Comando de inicio del contenedor.
# Nota: Las variables de entorno (DB_URL, etc.) se inyectarán mágicamente
# cuando levantemos esto con Docker Compose.
ENTRYPOINT ["java", "-jar", "app.jar"]
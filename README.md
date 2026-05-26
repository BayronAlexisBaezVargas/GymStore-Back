# 🏋️ GymStore

API REST para la gestión y venta de productos deportivos, construida con Spring Boot 3 y PostgreSQL.

---

## 📋 Tabla de contenidos

- [Descripción](#descripción)
- [Tecnologías](#tecnologías)
- [Requisitos previos](#requisitos-previos)
- [Configuración](#configuración)
- [Ejecución con Docker](#ejecución-con-docker)
- [Endpoints principales](#endpoints-principales)
- [Tests](#tests)
- [Cobertura de código](#cobertura-de-código)

---

## Descripción

GymStore es una aplicación backend que expone una API REST para gestionar la venta de productos deportivos. Permite administrar el catálogo de productos, procesar pedidos y gestionar la información relacionada con la tienda.

---

## Tecnologías

| Tecnología | Versión |
|---|---|
| Java | 17 |
| Spring Boot | 3.4.3 |
| Spring Data JPA | — |
| Spring Validation | — |
| Spring Actuator | — |
| PostgreSQL | 42.7.5 |
| Lombok | — |
| JaCoCo | 0.8.12 |
| H2 (tests) | — |

---

## Requisitos previos

- [Docker](https://www.docker.com/) y [Docker Compose](https://docs.docker.com/compose/) instalados
- Java 17+ (solo si se desea ejecutar localmente sin Docker)
- Maven 3.8+ (solo si se desea ejecutar localmente sin Docker)

---

## Configuración

Crea un archivo `.env` en la raíz del proyecto con las siguientes variables (o configúralas directamente en `docker-compose.yml`):

```env
POSTGRES_DB=gymstore
POSTGRES_USER=tu_usuario
POSTGRES_PASSWORD=tu_contraseña
SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/gymstore
SPRING_DATASOURCE_USERNAME=tu_usuario
SPRING_DATASOURCE_PASSWORD=tu_contraseña
```

---

## Ejecución con Docker

### 1. Clonar el repositorio

```bash
git clone https://github.com/tu-usuario/gymstore.git
cd gymstore
```

### 2. Construir y levantar los contenedores

```bash
docker compose up --build
```

La aplicación estará disponible en: `http://localhost:8080`

### 3. Detener los contenedores

```bash
docker compose down
```

> Para eliminar también los volúmenes de base de datos:
> ```bash
> docker compose down -v
> ```

---

## Endpoints principales

La API base es: `http://localhost:8080/api`

> Documentación completa disponible vía Actuator en `http://localhost:8080/actuator`

---

## Tests

El proyecto usa **H2** como base de datos en memoria para los tests, por lo que no requiere PostgreSQL al correrlos.

```bash
# Ejecutar tests
./mvnw test
```

---

## Cobertura de código

El proyecto tiene integrado **JaCoCo** para medir la cobertura. El reporte se genera automáticamente al correr los tests:

```bash
./mvnw test
```

El reporte HTML estará disponible en:

```
target/site/jacoco/index.html
```

---

## 📁 Estructura del proyecto

```
gymstore/
├── src/
│   ├── main/
│   │   ├── java/Store/Gym/gymstore/
│   │   └── resources/
│   │       └── application.properties
│   └── test/
├── docker-compose.yml
├── Dockerfile
├── pom.xml
└── README.md
```

---

## Licencia

Este proyecto se encuentra bajo la licencia [MIT](LICENSE).

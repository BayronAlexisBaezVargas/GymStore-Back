# 🏋️ GymStore

API REST para la gestión y venta de productos deportivos, construida con Spring Boot 3 y PostgreSQL.

![CI/CD](https://img.shields.io/badge/CI%2FCD-GitHub%20Actions-blue?logo=githubactions)
![Java](https://img.shields.io/badge/Java-17-orange?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.3-green?logo=springboot)
![Docker](https://img.shields.io/badge/Docker-Hub-blue?logo=docker)

---

## 📋 Tabla de contenidos

- [Descripción](#descripción)
- [Tecnologías](#tecnologías)
- [Requisitos previos](#requisitos-previos)
- [Configuración](#configuración)
- [Ejecución con Docker](#ejecución-con-docker)
- [Endpoints principales](#endpoints-principales)
- [Tests](#tests)
- [Calidad y Seguridad](#calidad-y-seguridad)
  - [JaCoCo — Cobertura de código](#jacoco--cobertura-de-código)
  - [SonarCloud — Análisis de calidad](#sonarcloud--análisis-de-calidad)
  - [Snyk — Análisis de vulnerabilidades](#snyk--análisis-de-vulnerabilidades)
- [Pipeline CI/CD](#pipeline-cicd)
- [Estructura del proyecto](#estructura-del-proyecto)
- [Licencia](#licencia)

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

Para el pipeline de GitHub Actions, estos secretos deben estar configurados en **Settings → Secrets and variables → Actions**:

| Secret | Descripción |
|---|---|
| `DB_USER` | Usuario de la base de datos PostgreSQL |
| `DB_PASSWORD` | Contraseña de la base de datos |
| `SONAR_TOKEN` | Token de autenticación de SonarCloud |
| `PROJECT_KEY_SONAR` | Clave del proyecto en SonarCloud |
| `ORGANIZATION_SONAR` | Organización en SonarCloud |
| `SNYK_TOKEN` | Token de autenticación de Snyk |
| `DOCKER_PASSWORD` | Contraseña de Docker Hub |

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

La imagen también está publicada en Docker Hub y puede ejecutarse directamente:

```bash
docker pull bayronbaez/gymstore-api:latest
docker run -p 8080:8080 bayronbaez/gymstore-api:latest
```

---

## Endpoints principales

La API base es: `http://localhost:8080/api`

> Documentación completa disponible vía Actuator en `http://localhost:8080/actuator`

---

## Tests

El proyecto usa **H2** como base de datos en memoria para los tests, por lo que no requiere PostgreSQL al correrlos.

```bash
./mvnw test
```

---

## Calidad y Seguridad

El proyecto integra tres herramientas que garantizan la calidad del código y la seguridad de las dependencias. Todas se ejecutan automáticamente en el pipeline de CI/CD.

### JaCoCo — Cobertura de código

[JaCoCo](https://www.jacoco.org/) (Java Code Coverage) mide qué porcentaje del código fuente es ejecutado durante las pruebas. Genera un reporte detallado por clase, método y línea, permitiendo identificar zonas sin cobertura.

**Ejecutar localmente:**

```bash
./mvnw test
```

El reporte HTML se genera en:

```
target/site/jacoco/index.html
```

---

### SonarCloud — Análisis de calidad

[SonarCloud](https://sonarcloud.io/) analiza el código en busca de:

- **Bugs** — errores que pueden causar comportamientos inesperados en producción
- **Code Smells** — código que funciona pero es difícil de mantener
- **Duplicaciones** — bloques de código repetido
- **Security Hotspots** — puntos del código que requieren revisión de seguridad

El análisis se ejecuta automáticamente en cada push a `main`, después de que los tests pasen. Los resultados están disponibles en el dashboard de SonarCloud de la organización.

**Ejecutar el análisis manualmente:**

```bash
mvn verify sonar:sonar \
  -Dsonar.projectKey=TU_PROJECT_KEY \
  -Dsonar.organization=TU_ORGANIZATION \
  -Dsonar.host.url=https://sonarcloud.io \
  -Dsonar.login=TU_SONAR_TOKEN
```

---

### Snyk — Análisis de vulnerabilidades

[Snyk](https://snyk.io/) escanea las dependencias del `pom.xml` en busca de **vulnerabilidades conocidas (CVEs)**. Está configurado para fallar el pipeline si detecta vulnerabilidades de severidad **alta o crítica**, bloqueando así un despliegue inseguro.

- Umbral de severidad configurado: `high`
- El reporte JSON se guarda como artefacto del pipeline en GitHub Actions (`snyk-report.json`)
- El paso usa `continue-on-error: true`, lo que permite que el reporte se guarde incluso si Snyk encuentra problemas

---

## Pipeline CI/CD

El pipeline se activa automáticamente con cada **push a la rama `main`** y ejecuta 4 etapas en orden:

```
Push a main
     │
     ▼
┌─────────────────────┐
│  1. build-and-test  │  → Levanta PostgreSQL con Docker Compose y ejecuta mvn test
└─────────┬───────────┘
          │
     ┌────┴────┐
     ▼         ▼
┌─────────┐ ┌──────────┐
│ 2. Sonar│ │ 3. Snyk  │  → Se ejecutan en paralelo, ambos dependen de build-and-test
└────┬────┘ └─────┬────┘
     └─────┬──────┘
           ▼
┌──────────────────────┐
│  4. docker-publish   │  → Construye y sube la imagen a Docker Hub (bayronbaez/gymstore-api:latest)
└──────────────────────┘
```

### Descripción de cada etapa

| Etapa | Descripción |
|---|---|
| `build-and-test` | Configura Java 17, levanta la BD con Docker Compose y ejecuta los tests con Maven |
| `security-sonar` | Analiza la calidad del código con SonarCloud (bugs, smells, duplicaciones) |
| `security-snyk` | Escanea dependencias en busca de CVEs y guarda el reporte como artefacto |
| `docker-publish` | Construye la imagen Docker y la publica en Docker Hub solo si las etapas anteriores pasan |

> La imagen solo se publica si **todas** las etapas de calidad y seguridad fueron exitosas.

---

## 📁 Estructura del proyecto

```
gymstore/
├── .github/
│   └── workflows/
│       └── ci.yml
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

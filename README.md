🏋️‍♂️ GymStore API - Arquitectura DevOps y CI/CD

Este repositorio contiene el microservicio de gestión de inventario para la tienda de accesorios de gimnasio GymStore. En esta fase del proyecto, la aplicación ha sido contenerizada, orquestada y automatizada mediante un pipeline robusto de Integración y Entrega Continua (CI/CD).

🏗️ 1. Arquitectura de Contenedores (Docker)

El proyecto utiliza Docker y Docker Compose para garantizar la portabilidad, el aislamiento y la fácil ejecución en cualquier entorno.

API (Spring Boot): Se utiliza un Dockerfile optimizado con la técnica de Multi-stage build. La primera etapa utiliza Maven para compilar el código fuente, y la segunda etapa empaqueta únicamente el archivo .jar resultante en una imagen ligera de Java (JRE), reduciendo drásticamente el peso final y la superficie de ataque.

Base de Datos (PostgreSQL): Orquestada junto a la API a través de docker-compose.yml. Se implementaron Healthchecks para asegurar que la base de datos esté completamente inicializada y sana antes de que la API intente conectarse. Se utilizan volúmenes (postgres_data) para garantizar la persistencia de la información.

Ejecución Local

Para levantar la arquitectura completa en un entorno local, se deben inyectar las credenciales mediante variables de entorno en la terminal (sin usar archivos .env por seguridad):

DB_USER=postgres DB_PASSWORD=admin docker compose up -d --build


⚙️ 2. Pipeline CI/CD (GitHub Actions)

Se implementó un pipeline automatizado (ci.yml) que se activa con eventos push y pull_request hacia las ramas develop y main. El pipeline consta de 4 etapas críticas:

Build & Test: Levanta un entorno efímero usando Docker Compose (API + BD simulada) y ejecuta las pruebas unitarias y de integración de Maven (mvn clean test).

Análisis de Código Estático (SonarCloud): Inspecciona el código en busca de vulnerabilidades, code smells y bugs. Existe un Quality Gate estricto que bloquea el pipeline si el código no cumple con los estándares corporativos.

Escaneo de Dependencias (Snyk): Analiza el archivo pom.xml en busca de vulnerabilidades conocidas (CVEs) en librerías de terceros.

Umbral de bloqueo: Está configurado para fallar el pipeline si detecta vulnerabilidades de nivel ALTO o CRÍTICO (--severity-threshold=high).

Artefactos: Genera un archivo snyk-report.json que se sube automáticamente como un artefacto descargable en GitHub Actions para futuras auditorías.

Entrega Continua (Docker Hub): Si (y solo si) el código pasa todas las pruebas anteriores y es fusionado a la rama main, el pipeline construye la imagen Docker final y la publica automáticamente en el registro público bajo la etiqueta bayronbaez/gymstore-api:latest.

🔍 3. Garantía de Calidad y Trazabilidad

Para cumplir con los más altos estándares de desarrollo, la trazabilidad y la calidad se garantizan de la siguiente manera:

Trazabilidad del Código a la Producción: Cada imagen subida a Docker Hub es el resultado directo de un commit específico en la rama main. No hay despliegues manuales; todo pasa por el historial auditable de GitHub Actions.

Calidad de Código Inquebrantable: La integración de SonarCloud actúa como un juez imparcial. Si un desarrollador introduce código duplicado o inseguro, el Quality Gate falla automáticamente, impidiendo que ese código llegue a producción.

Seguridad Proactiva: Al utilizar Snyk en cada PR, nos aseguramos de que no ingresen librerías obsoletas (ej. Tomcat vulnerables). Además, al guardar los reportes JSON como Artifacts, el equipo de seguridad puede trazar históricamente qué vulnerabilidades se detectaron en cada ejecución.

Entornos Idénticos: Gracias a la orquestación con Docker Compose, el entorno donde se ejecutan las pruebas automáticas en la nube es exactamente el mismo entorno donde el desarrollador programa y donde la aplicación se ejecutará en producción.

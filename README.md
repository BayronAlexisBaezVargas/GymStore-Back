# 🏋️‍♂️ API Tienda de Accesorios Gym (Evaluación DevOps)

Este proyecto es una API RESTful desarrollada en **Spring Boot (Java 17)** para la gestión de inventario de una tienda de accesorios de gimnasio. Está diseñado siguiendo estrictas prácticas de cultura DevOps, incluyendo metodologías de integración continua y control de versiones.

## 🚀 Tecnologías Utilizadas
* **Framework:** Spring Boot 3 (Spring Web, Spring Data JPA, Actuator)
* **Base de Datos:** PostgreSQL (Alojado en AlwaysData)
* **CI/CD:** GitHub Actions
* **Control de Versiones:** Git (Metodología GitFlow)

---

## 🏗️ Flujo de Trabajo (GitFlow)
Este repositorio utiliza **GitFlow** para aislar el desarrollo y mantener un historial limpio.

### Nomenclatura de Ramas
* `main`: Código estable y desplegado en producción.
* `develop`: Rama de pre-producción e integración. Todas las nuevas características llegan aquí.
* `feature/*`: Ramas temporales para nuevo desarrollo (ej. `feature/crud-accesorios`). Nacen de `develop` y vuelven a `develop`.
* `hotfix/*`: Ramas de emergencia para solucionar errores en producción (ej. `hotfix/reparar-cors`). Nacen de `main` y se fusionan en `main` y `develop`.

### Convenciones de Commits (Conventional Commits)
Cada commit debe tener un propósito claro usando los siguientes prefijos:
* `feat:` Nueva funcionalidad o endpoint.
* `fix:` Solución de un error o bug.
* `chore:` Tareas de mantenimiento, dependencias o configuración (ej. base de datos).
* `docs:` Cambios en la documentación (como este README).

### Políticas de Integración y Revisión
1. **Bloqueo de Push Directo:** No se permite hacer push directamente a `main` o `develop`.
2. **Pull Requests (PR):** Todo el código se integra mediante PRs.
3. **Estrategia de Merge:**
    * Las `feature/*` usan *Squash and Merge* hacia `develop`.
    * Los `hotfix/*` usan *Merge Commit* hacia `main` y `develop`.

---

## ⚙️ Configuración para Desarrollo Local

El proyecto utiliza variables de entorno para proteger las credenciales de la base de datos. Para ejecutarlo en tu máquina local, debes configurar las
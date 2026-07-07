# 🏋️ GymStore Backend - Arquitectura de Microservicios

API REST y ecosistema de microservicios para la gestión y venta de productos deportivos, construida con Spring Boot 3, PostgreSQL y desplegada de forma automatizada en AWS.

![CI/CD](https://img.shields.io/badge/CI%2FCD-GitHub%20Actions-blue?logo=githubactions)
![Java](https://img.shields.io/badge/Java-17-orange?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.3-green?logo=springboot)
![Docker](https://img.shields.io/badge/Amazon%20ECR-Registry-blue?logo=amazonaws)
![Terraform](https://img.shields.io/badge/Terraform-IaC-purple?logo=terraform)
![AWS](https://img.shields.io/badge/AWS-EC2-orange?logo=amazon-aws)

---

## 📋 Tabla de contenidos

- [Arquitectura de Microservicios](#arquitectura-de-microservicios)
- [Tecnologías](#tecnologías)
- [Infraestructura y Despliegue (Terraform & AWS)](#infraestructura-y-despliegue-terraform--aws)
- [Pipeline CI/CD](#pipeline-cicd)
- [Configuración de Secrets en GitHub](#configuración-de-secrets-en-github)
- [Ejecución Local con Docker](#ejecución-local-con-docker)
- [Calidad y Seguridad](#calidad-y-seguridad)
- [Estructura del proyecto](#estructura-del-proyecto)

---

## 🏗️ Arquitectura de Microservicios

Este proyecto ha evolucionado hacia una arquitectura de microservicios para garantizar alta disponibilidad y separación de responsabilidades. Actualmente contamos con:

1. 📦 **Microservicio de Catálogo (`gym-api` | Puerto 8080)**: 
   - Se encarga exclusivamente de gestionar toda la información y el CRUD de los accesorios y productos de la tienda.
   
2. 👤 **Microservicio de Usuarios y Autenticación (`ms-usuario` | Puerto 8081)**: 
   - Manejo de registro e inicio de sesión (Login).
   - Implementa seguridad robusta basada en **JWT (JSON Web Tokens)** y `Spring Security`.
   - Protege rutas mediante tokens cifrados con HMAC SHA-256.

*Próximos microservicios a implementar:* `order-service` (pedidos), `inventory-service` (inventario) y `payment-service` (pagos).

---

## 🛠️ Tecnologías

| Tecnología | Versión / Uso |
|---|---|
| **Java** | 17 |
| **Spring Boot** | 3.4.3 |
| **Seguridad** | Spring Security 6 + JJWT (0.12.5) |
| **Base de Datos** | PostgreSQL 15 |
| **Infraestructura (IaC)**| Terraform |
| **Nube (Cloud)** | AWS EC2 (Amazon Linux 2023) |
| **Calidad / Seguridad** | SonarCloud, Snyk, JaCoCo |

---

## ☁️ Infraestructura y Despliegue (Terraform & AWS)

Toda la infraestructura de la nube se maneja a través de código (IaC) en la carpeta `terraform/`. 

El código de Terraform automatiza la creación de:
- Un **Security Group** con los puertos 8080, 8081 y 22 (SSH) abiertos.
- Un **Log Group** de CloudWatch.
- Una máquina **EC2 t2.micro** con `Amazon Linux 2023`.
- Un script de *User Data* que instala automáticamente **K3s (Kubernetes ligero)** y el **Agente de CloudWatch**.
- Un par de **Llaves SSH RSA** generadas criptográficamente.

### ¿Cómo levantar la infraestructura?
```bash
cd terraform
terraform init
terraform apply -auto-approve
```
Al finalizar, Terraform te entregará dos *Outputs* críticos (`ec2_public_ip` y `private_key_pem`) que usarás en tus variables de GitHub.

---

## 🚀 Pipeline CI/CD

El pipeline automatizado en `.github/workflows/ci.yml` se activa con cada **push a la rama `main`** y ejecuta el siguiente flujo de *DevSecOps*:

```text
Push a main
     │
     ▼
┌─────────────────────────────────┐
│ 1. build-and-test (Ambos MS)    │ → Levanta DB de pruebas y pasa mvn test
└───────────────┬─────────────────┘
                │
       ┌────────┴────────┐
       ▼                 ▼
┌────────────┐     ┌────────────┐
│  2. Sonar  │     │  3. Snyk   │ → Análisis de Calidad y CVEs en paralelo
└──────┬─────┘     └─────┬──────┘
       └────────┬────────┘
                ▼
┌─────────────────────────────────┐
│ 4. docker-publish (Amazon ECR)  │ → Sube las imágenes a Amazon ECR
└───────────────┬─────────────────┘
                ▼
┌─────────────────────────────────┐
│ 5. deploy-ec2-ssh (AWS EC2)     │ → Se conecta por SCP/SSH a la instancia,
└─────────────────────────────────┘   aplica manifiestos en K3s (Kubernetes).
```

---

## 🔐 Configuración de Secrets en GitHub

Para que el pipeline funcione de manera autónoma, configura los siguientes secretos en tu repositorio (`Settings → Secrets and variables → Actions`):

| Secret | Descripción |
|---|---|
| `EC2_HOST` | La IP pública de tu EC2 (Output de Terraform) |
| `EC2_SSH_KEY` | La llave privada RSA completa (Output de Terraform) |
| `AWS_ACCESS_KEY_ID` | Access Key de tu cuenta AWS |
| `AWS_SECRET_ACCESS_KEY` | Secret Key de tu cuenta AWS |
| `AWS_SESSION_TOKEN` | Token de sesión (Obligatorio en AWS Academy/Vocareum) |
| `DB_USER` | Usuario de la BD PostgreSQL |
| `DB_PASSWORD` | Contraseña segura para PostgreSQL |
| `SONAR_TOKEN` | Token de autenticación de SonarCloud |
| `PROJECT_KEY_SONAR` | Clave del proyecto en SonarCloud |
| `ORGANIZATION_SONAR`| Nombre de la organización en SonarCloud |
| `SNYK_TOKEN` | Token de acceso a Snyk |

---

## 💻 Ejecución Local con Docker

Puedes probar todo el ecosistema de microservicios en tu computadora con un solo comando.

### 1. Clonar y Levantar
```bash
git clone https://github.com/tu-usuario/gymstore.git
cd gymstore
docker-compose up -d --build
```

Esto levantará 3 contenedores:
1. `gym_postgres`: Base de datos en el puerto 5432.
2. `gym_api`: Catálogo de accesorios en `http://localhost:8080/api/accesorios`
3. `gym_user_api`: API de usuarios y JWT en `http://localhost:8081/api/auth/...`

### 2. Probar Endpoints de Usuarios (JWT)
*   **Registrar Usuario:** `POST http://localhost:8081/api/auth/register`
*   **Hacer Login:** `POST http://localhost:8081/api/auth/login`
*   **Ruta Protegida:** `GET http://localhost:8081/api/users/hello` (Requiere enviar el Token JWT en el Header `Authorization: Bearer <token>`).

---

## 🛡️ Calidad y Seguridad

Este proyecto cuenta con integraciones de calidad activas en el CI/CD:

- **SonarCloud:** Analiza bugs, duplicaciones y code smells. Requiere pasar el *Quality Gate* del proyecto.
- **Snyk:** Escanea las dependencias en busca de vulnerabilidades (CVEs) de criticidad alta o extrema en ambos microservicios.
- **JaCoCo:** Genera reportes de cobertura de código para los tests locales.

---

## 📁 Estructura del proyecto

```text
gymstore/
├── .github/workflows/
│   └── ci.yml                # Pipeline CI/CD automatizado
├── gym-api/                  # Microservicio 1: Catálogo
│   ├── src/main/java/...
│   └── Dockerfile
├── ms-usuario/               # Microservicio 2: Usuarios y JWT
│   ├── src/main/java/.../security/ # Filtros y JwtUtil
│   └── Dockerfile
├── terraform/                # Infraestructura como Código (IaC)
│   └── main.tf               # Configuración de AWS EC2, SG, Roles, y CloudWatch
├── docker-compose.yml        # Orquestador local de microservicios
└── README.md
```

---

## Licencia

Este proyecto se encuentra bajo la licencia [MIT](LICENSE).
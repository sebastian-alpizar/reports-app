# 🖥️ Backend - API Reporte de Accidentes

API REST desarrollada con Spring Boot para gestionar reportes de accidentes.

---

## 🛠️ Tecnologías

- IntelliJ IDEA
- Kotlin
- Spring Boot
- Spring Web
- Spring Security
- OAuth2 + JWT
- Spring Data JPA
- PostgreSQL
- MapStruct
- Docker
- Heroku
- Postman

---

## 🧱 Arquitectura

- Clean Architecture
  - Controllers
  - Services (UseCases)
  - Repositories
  - Entities
  - DTOs

---

## 🔐 Seguridad

- Autenticación con JWT
- Roles (USER, ADMIN)
- OAuth2

---

## 📦 Funcionalidades

- Registro de usuarios
- Login
- CRUD de reportes
- Subida de imágenes
- Gestión de estados de reportes
- Historial por usuario

---

## ⚙️ Configuración

### Requisitos

- Java 17+
- PostgreSQL
- Docker (opcional)

### Variables de entorno

```env
DB_URL=jdbc:postgresql://localhost:5432/accidentes
DB_USER=postgres
DB_PASS=postgres
JWT_SECRET=secret
```

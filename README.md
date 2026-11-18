# AI-Powered Enterprise Knowledge Assistant - Auth Service

## Overview
The **Auth Service** (`auth-service`) is the decentralized identity, authentication, and authorization provider for the **AI-Powered Enterprise Knowledge Assistant** microservices platform. Built using **Spring Boot 3.x**, **Spring Security**, and **Spring Data JPA**, it provides user registration, credential hashing with BCrypt, stateless JWT issuance, and RBAC authorization across downstream services.

---

## Architectural Decisions & Principles

1. **Decoupled Identity & Data Sovereignty**:
   - Manages its own dedicated persistence database (`auth_db` PostgreSQL), maintaining microservice data isolation.
2. **Stateless JWT Architecture**:
   - Issues signed HMAC-SHA256 JWT tokens containing identity claims (`userId`, `email`, `role`).
   - Downstream services (such as Document Service and AI Service) and API Gateway can independently inspect and validate tokens.
3. **Clean Architecture & SOLID**:
   - Thin controllers (`AuthController`), segregated service layer (`AuthService`, `JwtService`), centralized exception handling (`CentralizedExceptionHandler`), and domain records/DTOs with Bean Validation.
4. **Defense in Depth & Security**:
   - Passwords securely hashed with `BCryptPasswordEncoder`.
   - Credentials and signing keys externalized via `.env` and environment variables.
5. **Observability**:
   - Spring Boot Actuator endpoints enabled for health (`/actuator/health`) and metrics.
   - Dynamic service discovery registration via **Netflix Eureka**.

---

## REST API Specification

| Method | Endpoint | Access | Description |
|---|---|---|---|
| `POST` | `/api/v1/auth/register` | Public | Register new user account |
| `POST` | `/api/v1/auth/login` | Public | Authenticate user & issue JWT |
| `POST` | `/api/v1/auth/validate` | Public | Validate JWT token & inspect claims |
| `GET` | `/api/v1/auth/me` | Authenticated | Retrieve authenticated user profile |
| `GET` | `/api/v1/auth/users/{userId}` | Authenticated | Retrieve user profile by UUID |

---

## Environment Variables & `.env` Reference

Copy `.env.example` to create your local `.env`:
```bash
cp .env.example .env
```

| Variable | Description | Default Value |
|---|---|---|
| `SERVER_PORT` | HTTP port for Auth Service | `8081` |
| `SPRING_APPLICATION_NAME` | Service discovery application name | `auth-service` |
| `DB_HOST` | PostgreSQL host | `localhost` |
| `DB_PORT` | PostgreSQL port | `5432` |
| `DB_NAME` | PostgreSQL database name | `auth_db` |
| `DB_USERNAME` | PostgreSQL username | `postgres` |
| `DB_PASSWORD` | PostgreSQL password | `postgres` |
| `JWT_SECRET` | HMAC-SHA256 signing secret key | *(256-bit secret key)* |
| `JWT_EXPIRATION_MS` | Access token lifespan in ms | `86400000` (24h) |
| `EUREKA_SERVICE_URL` | Eureka discovery registry URL | `http://localhost:8761/eureka/` |

---

## Project Structure

```text
auth-service/
├── .env                  # Local environment configurations (git-ignored)
├── .env.example          # Environment template
├── .gitignore            # Git exclusion rules
├── pom.xml               # Maven POM with Spring Boot, Security, JPA & JJWT
├── README.md             # Auth service documentation
└── src/
    ├── main/
    │   ├── java/lk/gihanvs/knowledge/auth/
    │   │   ├── AuthServiceApplication.java
    │   │   ├── config/
    │   │   │   ├── JwtAuthenticationFilter.java
    │   │   │   └── SecurityConfig.java
    │   │   ├── controller/
    │   │   │   └── AuthController.java
    │   │   ├── dto/
    │   │   │   ├── request/
    │   │   │   │   ├── LoginRequest.java
    │   │   │   │   └── RegisterRequest.java
    │   │   │   └── response/
    │   │   │       ├── ApiResponse.java
    │   │   │       ├── AuthResponse.java
    │   │   │       ├── UserResponse.java
    │   │   │       └── ValidateTokenResponse.java
    │   │   ├── entity/
    │   │   │   ├── Role.java
    │   │   │   └── User.java
    │   │   ├── exception/
    │   │   │   ├── CentralizedExceptionHandler.java
    │   │   │   ├── ErrorResponse.java
    │   │   │   ├── InvalidTokenException.java
    │   │   │   ├── ResourceNotFoundException.java
    │   │   │   └── UserAlreadyExistsException.java
    │   │   ├── repository/
    │   │   │   └── UserRepository.java
    │   │   └── service/
    │   │       ├── AuthService.java
    │   │       ├── JwtService.java
    │   │       └── impl/
    │   │           ├── AuthServiceImpl.java
    │   │           └── JwtServiceImpl.java
    │   └── resources/
    │       └── application.yml
    └── test/
        ├── java/lk/gihanvs/knowledge/auth/
        │   └── AuthServiceApplicationTests.java
        └── resources/
            └── application.yml
```

---

## Setup & Running Instructions

### Prerequisites
- **JDK 21** installed and configured (`JAVA_HOME` pointing to Java 21)
- **Apache Maven 3.8+** installed
- **PostgreSQL 14+** running with `auth_db` created
- **Eureka Discovery Server** running on port `8761`

### 1. Build the Service
```bash
cd /home/gihanvs/projects/final/auth-service
mvn clean package
```

### 2. Run the Service
```bash
mvn spring-boot:run
```

Or run the packaged executable JAR:
```bash
java -jar target/auth-service-0.0.1-SNAPSHOT.jar
```

### 3. Verify Health & APIs
```bash
# Health check
curl http://localhost:8081/actuator/health

# Register a user
curl -X POST http://localhost:8081/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"jdoe","email":"jdoe@example.com","password":"Password123!","role":"ROLE_USER"}'
```

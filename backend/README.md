# AgriLink — Backend Application

The `backend` directory houses the core RESTful API and business logic for the **AgriLink** platform, implemented as a modular monolith using **Java 17** and **Spring Boot 3.3.x**.

---

## 1. Technology Stack

- **Runtime & Language**: Java 17 LTS
- **Framework**: Spring Boot 3.3.3
- **Core Dependencies**:
  - `spring-boot-starter-web`: High-performance RESTful web endpoints and Jackson JSON serialization.
  - `spring-boot-starter-data-jpa`: Object-Relational Mapping (ORM) and repository abstractions backed by Hibernate.
  - `postgresql`: PostgreSQL JDBC Driver for relational database connectivity.
  - `spring-boot-starter-security`: Declarative web security and authorization baseline.
  - `spring-boot-starter-validation`: Jakarta Bean Validation and constraint enforcement.
  - `flyway-core` & `flyway-database-postgresql`: Version-controlled database schema migrations.
  - `spring-boot-starter-test` & `spring-security-test`: JUnit 5, AssertJ, and MockMvc testing frameworks.
- **Build Tool**: Apache Maven (bundled via Maven Wrapper `mvnw` / `mvnw.cmd`).

---

## 2. Backend Architecture

AgriLink Backend is structured as a **modular monolith** adhering to clean layered architecture:

```text
HTTP Requests
      │
      ▼
┌──────────────┐
│  Controller  │  Handles HTTP routing, request validation, and returns DTO responses
└──────┬───────┘
       │
       ▼
┌──────────────┐
│   Service    │  Encapsulates core business rules and transactional boundaries
└──────┬───────┘
       │
       ▼
┌──────────────┐
│  Repository  │  Spring Data JPA interfaces for data access and querying
└──────┬───────┘
       │
       ▼
┌──────────────┐
│    Entity    │  JPA database models mapped to PostgreSQL tables
└──────────────┘
```

---

## 3. Package Structure

The Java source code is organized under the base package `com.agrilink`:

```text
backend/src/main/java/com/agrilink/
├── AgrilinkApplication.java      # Application entry point
├── config/                       # Application configuration classes
│   ├── SecurityConfig.java       # SecurityFilterChain, stateless sessions, route access
│   └── WebConfig.java            # WebMvcConfigurer & CORS rules for frontend
├── controller/                   # REST API controllers
│   └── HealthController.java     # GET /api/health endpoint
├── dto/                          # Data Transfer Objects (request/response models)
│   ├── HealthResponse.java       # Health payload structure
│   └── ErrorResponse.java        # Standardized API error format
├── entity/                       # JPA entities (.gitkeep placeholder)
├── repository/                   # Spring Data JPA repositories (.gitkeep placeholder)
├── service/                      # Business logic interfaces and services (.gitkeep placeholder)
├── mapper/                       # Entity-DTO mapping converters (.gitkeep placeholder)
├── security/                     # Security filters & JWT utilities (.gitkeep placeholder)
├── exception/                    # Custom exceptions and global handlers
│   ├── GlobalExceptionHandler.java  # @RestControllerAdvice for uniform errors
│   └── ResourceNotFoundException.java # Base 404 exception
└── util/                         # Shared utility functions and constants (.gitkeep placeholder)
```

---

## 4. Database & Flyway Migration Strategy

### Flyway as Schema Authority
To prevent schema drift and ensure reproducible environments, **Flyway is the sole authority for database schema changes**:
- `spring.jpa.hibernate.ddl-auto` is set to `validate`. Hibernate validates that entities match the existing database tables but will never auto-mutate the schema.
- All database modifications must be authored as versioned SQL migration scripts.

### Migration Source of Truth
Executable migrations reside inside the classpath at:
```text
backend/src/main/resources/db/migration/
```

- **`V1__init_schema.sql`**: Phase 1 foundation migration that creates a lightweight `system_metadata` verification table:
  ```sql
  CREATE TABLE IF NOT EXISTS system_metadata (
      id VARCHAR(50) PRIMARY KEY,
      property_key VARCHAR(100) NOT NULL UNIQUE,
      property_value VARCHAR(255) NOT NULL,
      created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
  );
  ```

---

## 5. Security Baseline

Spring Security is pre-configured with a clean, stateless REST API baseline in `SecurityConfig.java`:

- **Stateless Session Management**: `SessionCreationPolicy.STATELESS` (no HTTP server sessions).
- **CSRF Disabled**: Safe for stateless bearer-token REST architectures.
- **Permit All Routes**:
  - `GET /api/health` — Service health check.
  - `/api/auth/**` — Reserved for login/registration endpoints (Phase 2+).
  - `/v3/api-docs/**`, `/swagger-ui/**` — OpenAPI/Swagger documentation endpoints.
- **Secured Defaults**: All other API endpoints require authentication (`anyRequest().authenticated()`).

---

## 6. Web & CORS Configuration

In `WebConfig.java`, CORS is pre-configured for local client development:
- **Allowed Origin**: `http://localhost:5173` (Vite development server default)
- **Allowed Methods**: `GET`, `POST`, `PUT`, `DELETE`, `PATCH`, `OPTIONS`
- **Allowed Headers**: `*`
- **Allow Credentials**: `true`

---

## 7. Backend Environment Variables

Configuration is loaded from environment variables defined in `application.yml`:

| Environment Variable | Required | Description | Example / Default |
| :--- | :--- | :--- | :--- |
| `DATABASE_URL` | **Yes** | JDBC connection URL for PostgreSQL | `jdbc:postgresql://localhost:5432/agrilink` |
| `DATABASE_USERNAME` | **Yes** | Database username | `agrilink_user` |
| `DATABASE_PASSWORD` | **Yes** | Database user password | `your_secure_password` |
| `PORT` | No | HTTP server port | `8080` (default) |
| `CORS_ALLOWED_ORIGINS`| No | Comma-separated list of allowed CORS origins | `http://localhost:5173` (default) |

> [!IMPORTANT]
> Never hardcode database credentials or secrets inside `application.yml`. Always supply them through your local `.env` file or environment variables.

---

## 8. Exception Handling Foundation

All API exceptions are intercepted centrally by `GlobalExceptionHandler.java` and returned in a consistent JSON structure:

```json
{
  "timestamp": "2026-09-01T00:15:00",
  "status": 404,
  "error": "Not Found",
  "message": "Resource not found with id 123",
  "path": "/api/v1/example"
}
```

---

## 9. Development & Testing Commands

All build and run operations use the included Maven Wrapper scripts.

### Compiling the Code
```powershell
# Windows
.\mvnw.cmd clean compile

# Linux / macOS
./mvnw clean compile
```

### Running Automated Tests
The test suite includes `AgrilinkApplicationTests` and `HealthControllerTest`:
```powershell
# Windows
.\mvnw.cmd test

# Linux / macOS
./mvnw test
```

### Running the Application Locally
Make sure PostgreSQL is running (e.g. via `docker compose up -d postgres` from project root) and environment variables are loaded:

```powershell
# Windows (PowerShell)
cd d:\Projects\AgriLink
Get-Content .env | Where-Object { $_ -match '^\s*[^#]' -and $_ -match '=' } | ForEach-Object {
    $name, $value = $_.Split('=', 2)
    [System.Environment]::SetEnvironmentVariable($name.Trim(), $value.Trim(), "Process")
}
cd backend
.\mvnw.cmd spring-boot:run

# Linux / macOS
cd /path/to/AgriLink
export $(grep -v '^#' .env | xargs)
cd backend
./mvnw spring-boot:run
```

### Building a Production JAR
```powershell
# Windows
.\mvnw.cmd clean package -DskipTests

# Linux / macOS
./mvnw clean package -DskipTests
```
The resulting executable JAR will be located at `backend/target/agrilink-backend-0.0.1-SNAPSHOT.jar`.

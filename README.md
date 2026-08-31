# AgriLink — Market Linkage & Price Discovery Platform for Farmers

AgriLink is an intelligent agricultural market linkage and fair price discovery platform designed to connect farmers directly with buyers, eliminate exploitative intermediaries, and provide real-time price intelligence.

---

## Project Overview

Agriculture in India faces systemic challenges around fragmented supply chains, opaque pricing, and dependency on middlemen. AgriLink addresses these problems through:
- **Direct Market Linkage**: Empowering farmers to list their produce and connect directly with institutional buyers, retailers, and bulk purchasers.
- **Fair Price Discovery**: Leveraging intelligent price discovery mechanisms to ensure transparent pricing based on real-time market trends.
- **Trust & Quality Transparency**: Building digital trust through verified profiles, standard quality grading, and verifiable transactions.

### What This Repository Contains
This repository is organized as a monorepo containing:
- A **Spring Boot backend** providing RESTful APIs, security, and persistence.
- A **Frontend** web application workspace (for React/Vite client interface).
- An **ML Service** workspace (for price discovery and matching intelligence).
- A **Database** directory containing schema documentation and seed datasets.
- **Docker Compose** configuration for local development infrastructure.

---

## High-Level Project Structure

```text
AgriLink/
├── backend/              # Spring Boot backend application & backend documentation
├── frontend/             # Frontend web client workspace & documentation
├── ml-service/           # Machine learning & price discovery service
├── database/             # Database architecture, ER diagrams, and seed datasets
│   ├── seed/             # Seed data scripts for development
│   └── README.md         # Database design notes
├── docs/                 # General project documentation & architecture specs
├── scripts/              # Developer automation & utility scripts
├── docker/               # Dockerfiles and container build configurations
├── .env.example          # Environment variable template for local setup
├── .gitignore            # Git ignore rules for dependencies and secrets
├── docker-compose.yml    # Docker Compose orchestration for local services
└── README.md             # Project-level onboarding guide (this file)
```

---

## Prerequisites

Before running the project, verify that the following tools are installed on your system:

| Tool | Why It Is Needed | How to Verify Installation |
| :--- | :--- | :--- |
| **Git** | For source code version control and cloning the repository. | `git --version` |
| **Docker Desktop** | Runs local services (such as PostgreSQL) inside isolated containers without needing manual database installations. | `docker --version` |
| **Docker Compose** | Orchestrates and manages multi-container Docker environments. | `docker compose version` |
| **Java Development Kit (JDK 17+)** | Required to compile and run the Spring Boot backend. | `java -version` |

> [!NOTE]
> **Apache Maven** is bundled directly in the project via the Maven Wrapper (`mvnw` / `mvnw.cmd`). You do **not** need a separate global Maven installation.

---

## First-Time Setup

Follow these step-by-step instructions to get the project running on your machine for the first time:

### Step 1: Clone the Repository
```bash
git clone https://github.com/Boopathi7706/AgriLink.git
cd AgriLink
```

### Step 2: Create Your Local Environment File (`.env`)
The repository includes a template file named `.env.example`. Copy this file to create your own `.env` file:

- **Windows (PowerShell):**
  ```powershell
  Copy-Item .env.example .env
  ```
- **macOS / Linux:**
  ```bash
  cp .env.example .env
  ```

> [!IMPORTANT]
> The `.env` file is intentionally ignored by Git (configured in `.gitignore`) so your personal local passwords and secrets are never committed to GitHub.

### Step 3: Configure Your Environment Variables
Open the `.env` file in your text editor. Set your desired local database password and verify the connection settings:

```env
POSTGRES_DB=agrilink
DATABASE_USERNAME=agrilink_user
DATABASE_PASSWORD=your_secure_password_here
DATABASE_URL=jdbc:postgresql://localhost:5432/agrilink
PORT=8080
```

### Step 4: Start PostgreSQL with Docker Compose
Start the PostgreSQL container in the background:

```bash
docker compose up -d postgres
```

### Step 5: Verify That the Database Container is Running
Check container status:

```bash
docker compose ps
```
You should see `agrilink-postgres` with status `Up` (healthy) and port `5432` mapped.

### Step 6: Run the Backend Application

- **Windows (PowerShell):**
  ```powershell
  # 1. Load .env environment variables into current PowerShell session
  Get-Content .env | Where-Object { $_ -match '^\s*[^#]' -and $_ -match '=' } | ForEach-Object {
      $name, $value = $_.Split('=', 2)
      [System.Environment]::SetEnvironmentVariable($name.Trim(), $value.Trim(), "Process")
  }

  # 2. Run the Spring Boot backend via Maven Wrapper
  cd backend
  .\mvnw.cmd spring-boot:run
  ```

- **macOS / Linux:**
  ```bash
  # 1. Export .env variables
  export $(grep -v '^#' .env | xargs)

  # 2. Run the Spring Boot backend via Maven Wrapper
  cd backend
  ./mvnw spring-boot:run
  ```

### Step 7: Confirm the Application is Running
Once started, test the backend health endpoint in a new terminal window or web browser:

- **Browser / cURL:**
  ```bash
  curl http://localhost:8080/api/health
  ```
- **PowerShell:**
  ```powershell
  Invoke-RestMethod -Uri http://localhost:8080/api/health
  ```

**Expected JSON Response:**
```json
{
  "status": "UP",
  "service": "agrilink-backend"
}
```

---

## Docker and Database Setup (Beginner-Friendly)

In this project, Docker is used to manage local dependencies so every team member has an identical, isolated environment without conflicting software installations:

- **No Local PostgreSQL Installation Required**: You do not need to install or configure PostgreSQL server directly on your operating system. Docker runs PostgreSQL inside an isolated container.
- **Isolated Local Database**: Each developer runs their own containerized database instance. Your local data is completely private and is not automatically shared over the network.
- **Configuration from `.env`**: When Docker starts PostgreSQL, it reads the username, password, and database name directly from your local `.env` file.
- **Persistent Data Storage**: Database tables and records are saved inside a named Docker volume (`agrilink_postgres_data`). Stopping or restarting the container does **not** erase your database data.

### Essential Docker Commands

| Command | What It Does |
| :--- | :--- |
| `docker compose up -d postgres` | Starts the PostgreSQL database container in the background (detached mode). |
| `docker compose up -d` | Starts all configured services (database, backend) in the background. |
| `docker compose ps` | Lists all running containers and their current health status. |
| `docker compose logs -f postgres` | Streams live logs from the PostgreSQL container (press `Ctrl+C` to exit). |
| `docker compose stop` | Safely stops running containers without deleting any data. |
| `docker compose down` | Stops and removes containers and networks. Named volumes are preserved. |
| `docker compose down -v` | ⚠️ **Warning**: Stops containers and **deletes** the persistent database volume (resets DB to empty). |

---

## Daily Development Workflow

Once initial setup is completed, your typical daily development workflow is:

1. **Start the database container**:
   ```bash
   docker compose up -d postgres
   ```
2. **Confirm the database is healthy**:
   ```bash
   docker compose ps
   ```
3. **Start the Spring Boot backend**:
   - On Windows: `cd backend; .\mvnw.cmd spring-boot:run`
   - On Linux/macOS: `cd backend && ./mvnw spring-boot:run`
4. **Access the application**:
   - Backend API: `http://localhost:8080`
   - Health Endpoint: `http://localhost:8080/api/health`
5. **When finished working**:
   - Stop backend: Press `Ctrl+C` in the backend terminal.
   - Stop containers: `docker compose stop`

---

## Environment Variables Overview

AgriLink uses a two-tier configuration model:

1. **`.env.example` (Committed to Git)**:
   - Serves as the official template and documentation for all available environment variables.
   - Contains safe example values and placeholders.
   - **Never contains real secrets or passwords**.
2. **`.env` (Ignored by Git)**:
   - Created locally by each developer.
   - Holds your private local credentials and environment overrides.
   - **Never committed to GitHub** (enforced via `.gitignore`).

---

## Troubleshooting Common Setup Issues

### 1. "Cannot connect to the Docker daemon" / Docker Desktop Not Running
- **Cause**: Docker Desktop application is closed or Docker service has not finished starting.
- **Fix**: Open Docker Desktop on your machine and wait until the status icon shows "Engine running" before running `docker compose` commands.

### 2. Port 5432 or 8080 Already in Use
- **Cause**: Another local service (e.g. a locally installed PostgreSQL or previous backend process) is occupying the port.
- **Fix**:
  - For PostgreSQL: Either stop the local PostgreSQL service or change `POSTGRES_PORT=5433` in `.env` and `docker-compose.yml`.
  - For Backend: Identify and terminate the process using port 8080, or set `PORT=8081` in `.env`.

### 3. Spring Boot Cannot Connect to PostgreSQL
- **Cause**: Database container is not running, or credentials in `.env` do not match.
- **Fix**:
  1. Check container status with `docker compose ps`.
  2. Verify that `DATABASE_URL=jdbc:postgresql://localhost:5432/agrilink` matches the `POSTGRES_DB` name and credentials in `.env`.
  3. Ensure environment variables are loaded in your terminal session before launching Spring Boot.

### 4. Changed `.env` but Docker Still Uses Old Credentials
- **Cause**: PostgreSQL initializes credentials only when creating a new volume for the first time.
- **Fix**: Recreate the container and volume (⚠️ this resets local DB data):
  ```bash
  docker compose down -v
  docker compose up -d postgres
  ```

---

## Documentation Navigation

For deeper technical details, refer to the component-specific documentation:

- 📖 **[Backend Guide](file:///d:/Projects/AgriLink/backend/README.md)**: Spring Boot architecture, package layout, Flyway database migrations, security baseline, testing, and API guidelines.
- 🎨 **[Frontend Guide](file:///d:/Projects/AgriLink/frontend/README.md)**: Web application stack, component structure, and client setup.
- 🤖 **[ML Service Guide](file:///d:/Projects/AgriLink/ml-service/README.md)**: Price discovery engine, matching algorithms, and FastAPI service setup.
- 🗄️ **[Database Guide](file:///d:/Projects/AgriLink/database/README.md)**: ER diagrams, data dictionary, seed datasets, and migration source of truth.

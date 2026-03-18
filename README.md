# Qiromanager Backend

Backend of a digital platform designed to replace the paper-based workflow of a massage therapy center. It provides a secure, modular REST API for managing patients, clinical history, file attachments, and treatment sessions.

---

## 📋 Table of Contents

- [Overview](#-overview)
- [Tech Stack](#-tech-stack)
- [Architecture](#-architecture)
- [Getting Started](#-getting-started)
- [Environment Variables](#-environment-variables)
- [API Reference](#-api-reference)
- [Roles & Permissions](#-roles--permissions)
- [Auditing](#-auditing)
- [Running Tests](#-running-tests)

---

## 🏥 Overview

Qiromanager lets therapists and administrators manage:

- **Users** — therapist accounts with role-based access (ADMIN / USER)
- **Patients** — full profiles, status management, therapist assignment
- **Treatment Sessions** — log, update and delete session records per patient
- **Clinical Records** — typed notes (anamnesis, evolution, reports…) with optional file attachments via Cloudinary
- **Dashboard Stats** — session counts, patient stats; admins see global data, therapists see their own
- **Audit Log** — automatic tracking of critical actions (patient/user status changes, assignments, etc.)

Authentication is JWT-based. All endpoints except `/api/v1/auth/**` require a valid token.

---

## ⚙️ Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.5.8 |
| Security | Spring Security + JJWT 0.12.5 |
| Persistence | Spring Data JPA + MySQL |
| File Storage | Cloudinary |
| API Docs | SpringDoc OpenAPI 2 (Swagger UI) |
| Rate Limiting | Bucket4j |
| Build | Maven |
| Tests | JUnit 5 + Mockito + Spring Boot Test |

---

## 🏗️ Architecture

The project follows a **Hexagonal (Clean) Architecture**:

```
src/main/java/com/qiromanager/qiromanager_backend/
├── api/                   # Controllers, DTOs, mappers, exception handlers
├── application/           # Use cases (one class per use case)
├── domain/                # Entities, port interfaces, domain exceptions, enums
├── infrastructure/        # JPA adapters, Cloudinary adapter, bootstrap data
├── security/              # JWT filter, SecurityConfig, rate limiter
└── config/                # AuditorAware, app-level beans
```

Each use case lives in its own class under `application/`, depends only on domain port interfaces, and is injected into controllers. Infrastructure adapters implement those ports — the domain never depends on Spring Data or any framework.

---

## 🚀 Getting Started

### Prerequisites

- Java 21+
- Maven 3.9+
- MySQL 8+ running locally (or set `SPRING_PROFILES_ACTIVE=test` for H2 in-memory)

### Run locally

```bash
git clone https://github.com/alaw810/qiromanager-backend.git
cd qiromanager-backend

# Copy and fill in the required environment variables (see below)
cp .env.example .env   # or export them manually

./mvnw spring-boot:run
```

The API will be available at `http://localhost:8080`.
Swagger UI: `http://localhost:8080/swagger-ui.html`

### Profiles

| Profile | Database | DDL mode |
|---|---|---|
| `dev` (default) | MySQL `localhost:3306/qiromanager` | `update` |
| `test` | H2 in-memory | `create-drop` |
| `prod` | MySQL via `PRODUCTION_URL` | `validate` |

---

## 🔐 Environment Variables

| Variable | Required | Description |
|---|---|---|
| `DB_USERNAME` | ✅ | MySQL username |
| `DB_PASSWORD` | ✅ | MySQL password |
| `JWT_SECRET` | ✅ | Signing secret (≥ 32 chars) |
| `JWT_EXPIRATION` | ✅ | Token TTL in milliseconds (e.g. `3600000`) |
| `CLOUDINARY_CLOUD_NAME` | ✅ | Cloudinary cloud name |
| `CLOUDINARY_API_KEY` | ✅ | Cloudinary API key |
| `CLOUDINARY_API_SECRET` | ✅ | Cloudinary API secret |
| `CORS_ALLOWED_ORIGINS` | prod only | Comma-separated allowed origins |

---

## 📡 API Reference

All endpoints are prefixed with `/api/v1`. Protected endpoints require the header:

```
Authorization: Bearer <token>
```

### 🔑 Auth

| Method | Path | Auth | Description |
|---|---|---|---|
| `POST` | `/auth/register` | Public | Register a new user account |
| `POST` | `/auth/login` | Public | Login and receive a JWT token |

---

### 👤 Users

| Method | Path | Role | Description |
|---|---|---|---|
| `GET` | `/users` | ADMIN | List all users. Optional filter: `?role=USER\|ADMIN` |
| `GET` | `/users/{id}` | ADMIN | Get user by ID |
| `GET` | `/users/me` | USER, ADMIN | Get own profile |
| `PUT` | `/users/me` | USER, ADMIN | Update own name and/or password |
| `PUT` | `/users/{id}` | ADMIN | Update any user (including role) |
| `PATCH` | `/users/{id}/status` | ADMIN | Activate or deactivate a user |

---

### 🧑‍⚕️ Patients

| Method | Path | Role | Description |
|---|---|---|---|
| `POST` | `/patients` | USER, ADMIN | Create a patient (auto-assigned to caller) |
| `GET` | `/patients` | USER, ADMIN | List active patients (paginated, optional name filter) |
| `GET` | `/patients/search` | USER, ADMIN | Search patients by full name |
| `GET` | `/patients/{id}` | USER, ADMIN | Get patient by ID |
| `PUT` | `/patients/{id}` | USER, ADMIN | Update patient data |
| `PATCH` | `/patients/{id}/status` | ADMIN | Activate or deactivate a patient |
| `POST` | `/patients/{id}/assign` | USER, ADMIN | Assign calling therapist to patient |
| `DELETE` | `/patients/{id}/assign` | USER, ADMIN | Unassign calling therapist from patient |

---

### 🗓️ Treatment Sessions

| Method | Path | Role | Description |
|---|---|---|---|
| `POST` | `/patients/{patientId}/sessions` | USER, ADMIN | Log a new session |
| `GET` | `/patients/{patientId}/sessions` | USER, ADMIN | Get all sessions for a patient |
| `GET` | `/patients/{patientId}/sessions/{sessionId}` | USER, ADMIN | Get a session by ID |
| `PUT` | `/patients/{patientId}/sessions/{sessionId}` | USER, ADMIN | Update session date and/or notes |
| `DELETE` | `/patients/{patientId}/sessions/{sessionId}` | ADMIN | Delete a session |

---

### 📋 Clinical Records

| Method | Path | Role | Description |
|---|---|---|---|
| `POST` | `/patients/{patientId}/clinical-records` | USER, ADMIN | Create a clinical record (optional file attachment) |
| `GET` | `/patients/{patientId}/clinical-records` | USER, ADMIN | List all records for a patient |
| `GET` | `/patients/{patientId}/clinical-records/{recordId}` | USER, ADMIN | Get a record by ID |
| `DELETE` | `/patients/{patientId}/clinical-records/{recordId}` | ADMIN | Delete a clinical record |

Supported record types: `ANAMNESIS`, `EVOLUTION`, `MEDICAL_REPORT`, `CONSENT`, `RECOMMENDATION`.

File attachments are uploaded to Cloudinary. Maximum file size: **10 MB**.

---

### 📊 Stats

| Method | Path | Role | Description |
|---|---|---|---|
| `GET` | `/stats` | USER, ADMIN | Dashboard stats (global for ADMIN, own for USER) |

---

## 👥 Roles & Permissions

| Feature | USER (Therapist) | ADMIN |
|---|:---:|:---:|
| View / manage own profile | ✅ | ✅ |
| Create patients | ✅ | ✅ |
| View & update patients | ✅ | ✅ |
| Activate / deactivate patients | ❌ | ✅ |
| Log / update treatment sessions | ✅ | ✅ |
| Delete treatment sessions | ❌ | ✅ |
| Create / view clinical records | ✅ | ✅ |
| Delete clinical records | ❌ | ✅ |
| Manage users | ❌ | ✅ |
| View global stats | ❌ | ✅ |

---

## 🔍 Auditing

The application combines two auditing strategies:

**Spring Data Auditing** — `@CreatedBy` / `@LastModifiedBy` fields on `Patient` and `User` entities automatically record which user created or last modified each record.

**Custom Audit Log** — An `audit_log` table captures critical actions with entity type, entity ID, action, performer, timestamp and details. Tracked actions:

| Action | Trigger |
|---|---|
| `PATIENT_CREATED` | New patient registered |
| `PATIENT_UPDATED` | Patient data updated |
| `PATIENT_ACTIVATED` | Patient re-activated |
| `PATIENT_DEACTIVATED` | Patient deactivated |
| `PATIENT_ASSIGNED` | Therapist assigned to patient |
| `PATIENT_UNASSIGNED` | Therapist removed from patient |
| `USER_ACTIVATED` | User account activated |
| `USER_DEACTIVATED` | User account deactivated |

---

## 🧪 Running Tests

```bash
# Run all tests
./mvnw test

# Run only unit tests (fast, no Spring context)
./mvnw test -Dtest="*UseCaseTest,*Test"
```

Tests use an H2 in-memory database (`application-test.yml`) — no external dependencies required.
Unit tests use Mockito (`@ExtendWith(MockitoExtension.class)`) and do not load the Spring context.

---

## 📄 License

Private project — all rights reserved.

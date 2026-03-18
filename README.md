# 🏥 Qiromanager Backend

A comprehensive digital platform designed to modernize the management of physiotherapy and massage therapy clinics. It provides a secure, modular REST API for managing patients, clinical histories, medical documents, and treatment sessions.

---

## 📋 Table of Contents

- [Key Features](#-key-features)
- [Tech Stack](#-tech-stack)
- [Architecture](#-architecture)
- [Getting Started](#-getting-started)
- [Environment Variables](#-environment-variables)
- [API Reference](#-api-reference)
- [Roles & Permissions](#-roles--permissions)
- [Auditing](#-auditing)
- [Running Tests](#-running-tests)
- [Author](#-author)

---

## ✨ Key Features

### 👥 Patient Management
- **Complete CRUD:** Registration, editing, and advanced search of patients with pagination
- **Smart Assignment:** Therapists assign patients to themselves ("My Patients") for personalized tracking
- **Status Control:** Activation and deactivation of patient profiles (ADMIN)

### 🩺 Digital Clinical History
- **Typed Records:** Anamnesis, evolution notes, medical reports, consent forms, recommendations
- **File Attachments:** Secure upload of reports, X-rays, and documents integrated with **Cloudinary**
- **Automation:** Automatic history entries generated via Spring Events when a session is recorded

### 💆 Treatment Sessions
- Full CRUD for session records: log, update, and delete interventions with technical notes
- Automatic calculation of activity statistics per therapist and globally

### 📊 Smart Dashboard
- **Admin View:** Global metrics — total patients, monthly activity, inactive cases
- **Therapist View:** Personal metrics — assigned patients, my sessions this month

### 🔐 Security
- JWT authentication with rate-limited login (Bucket4j)
- Role-based access: `ADMIN` (global management) and `USER` (therapist)
- CORS configured per environment

### 🔍 Auditing
- Spring Data Auditing (`createdBy` / `updatedBy`) on Patient and User entities
- Custom `audit_log` table for critical actions

---

## ⚙️ Tech Stack

### Backend
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

### Frontend (separate repository)
- **Next.js** (React Framework) · **TypeScript** · **Tailwind CSS** & **ShadCN UI** · **Axios**

### Infrastructure & Quality
- **Docker & Docker Compose** (database containerization)
- **SLF4J** (structured console and file logging)

---

## 🏗️ Architecture

The project follows **Hexagonal (Clean) Architecture** to ensure scalability and maintainability:

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
- MySQL 8+ (or Docker for a quick setup)

### 1. Database with Docker

```bash
docker-compose up -d
```

### 2. Environment Variables

Copy and fill in the required values (see [Environment Variables](#-environment-variables) below):

```bash
export DB_USERNAME=root
export DB_PASSWORD=root
export JWT_SECRET=your_super_secure_secret_key_at_least_32_chars
export JWT_EXPIRATION=3600000
export CLOUDINARY_CLOUD_NAME=your_cloud_name
export CLOUDINARY_API_KEY=your_api_key
export CLOUDINARY_API_SECRET=your_api_secret
```

### 3. Run the backend

```bash
./mvnw spring-boot:run
```

API available at `http://localhost:8080` · Swagger UI at `http://localhost:8080/swagger-ui.html`

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

All endpoints are prefixed with `/api/v1`. Protected endpoints require:

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
| `POST` | `/patients/{patientId}/clinical-records` | USER, ADMIN | Create a record (optional file attachment) |
| `GET` | `/patients/{patientId}/clinical-records` | USER, ADMIN | List all records for a patient |
| `GET` | `/patients/{patientId}/clinical-records/{recordId}` | USER, ADMIN | Get a record by ID |
| `DELETE` | `/patients/{patientId}/clinical-records/{recordId}` | ADMIN | Delete a clinical record |

Supported record types: `ANAMNESIS`, `EVOLUTION`, `MEDICAL_REPORT`, `CONSENT`, `RECOMMENDATION`.
Maximum file size: **10 MB**.

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

**Custom Audit Log** — An `audit_log` table captures critical actions with entity type, entity ID, action, performer, timestamp and details.

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

Tests use an H2 in-memory database — no external dependencies required.

---

## 👤 Author

Developed by **Adrià Lorente** as an IT Academy – Java Back-End Development Bootcamp Final Project.

- [GitHub](https://github.com/alaw810)
- [LinkedIn](https://www.linkedin.com/in/adrialorente/)

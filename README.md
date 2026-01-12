# 🏥 Qiromanager

**Qiromanager** is a comprehensive digital platform designed to modernize the management of physiotherapy and massage therapy clinics. It enables therapists and administrators to manage patients, clinical histories, medical documents, and treatment sessions in a secure and efficient environment.

---

## 🚀 Key Features

### 👥 Patient Management
* **Complete CRUD:** Registration, editing, and advanced search of patients.
* **Smart Assignment:** Therapists can assign patients to themselves ("My Patients") for personalized tracking.
* **Status Control:** Activation and deactivation of patient profiles.

### 🩺 Digital Clinical History
* **Chronological Record:** Clear visualization of the patient's evolution.
* **File Attachments:** Secure upload of reports, X-rays, and consent forms (integrated with **Cloudinary**).
* **Automation:** Automatic generation of history entries when recording a session (Event-Driven).

### 💆 Treatment Sessions
* Detailed logging of interventions and technical notes.
* Automatic calculation of activity statistics.

### 📊 Smart Dashboard
* **Admin View:** Global business metrics (Total patients, monthly activity, inactive cases).
* **Therapist View:** Personal metrics (My assigned patients, my sessions this month).

### 🔐 Security & Users
* Robust authentication with **JWT**.
* Differentiated Roles: `ADMIN` (Global management) and `USER` (Therapist).
* Self-profile management.

---

## 🛠️ Tech Stack

### Backend (REST API)
* **Java 21** & **Spring Boot 3.5+**
* **Spring Security** + JWT (Auth)
* **Spring Data JPA** (Persistence)
* **MySQL** (Production) / **H2** (Testing)
* **Cloudinary** (Cloud file storage)
* **Spring Events** (Logic decoupling)
* **Swagger / OpenAPI** (Live documentation)
* **Maven** (Dependency management)

### Frontend (SPA)
* **Next.js** (React Framework)
* **TypeScript**
* **Tailwind CSS** & **ShadCN UI** (Design System)
* **Axios** (HTTP Client)
* **Lucide React** (Iconography)

### Infrastructure & Quality
* **Docker & Docker Compose** (Database containerization)
* **JUnit 5 & Mockito** (Unit and Integration Testing)
* **SLF4J** (Advanced file and console logging)

---

## 🏗️ Architecture

The project follows **Clean Architecture (Hexagonal Architecture)** principles to ensure scalability and maintainability:

* `domain`: Entities, Business Rules, Repository Interfaces (Pure core, framework-agnostic).
* `application`: Use Cases (Orchestrating logic), Input/Output DTOs.
* `infrastructure`: Repository implementations (JPA), External adapters (Cloudinary, Email).
* `api`: REST Controllers, Exception Handling, Security configuration.

---

## ⚙️ Installation & Setup

### Prerequisites
* Java 21 JDK
* Node.js 18+ and npm
* Docker (optional, for the Database)

### 1. Environment Variables Configuration
Create a `.env` file in the backend root or configure the variables in your IDE/System:

```properties
# Database
DB_USERNAME=root
DB_PASSWORD=root

# Security (Must be a long 32-byte string)
JWT_SECRET=your_super_secure_secret_key_base64_etc
JWT_EXPIRATION=3600000

# Cloudinary (For file uploads)
CLOUDINARY_CLOUD_NAME=your_cloud_name
CLOUDINARY_API_KEY=your_api_key
CLOUDINARY_API_SECRET=your_api_secret

```

### 2. Start the Database (Docker)

If you have Docker installed, spin up MySQL quickly:

```bash
docker-compose up -d

```

### 3. Run the Backend

```bash
# From the backend project root
./mvnw spring-boot:run

```

The server will start at: `http://localhost:8080`

### 4. Run the Frontend

```bash
# From the frontend folder
npm install
npm run dev

```

The web application will start at: `http://localhost:3000`

---

## 📚 API Documentation

Once the backend is running, you can explore and test all endpoints using Swagger UI:

👉 **[http://localhost:8080/swagger-ui/index.html](https://www.google.com/search?q=http://localhost:8080/swagger-ui/index.html)**

---

## 🧪 Testing

The project includes extensive **Integration Test** coverage (`@SpringBootTest`) to ensure the robustness of critical flows (Auth, Patients, Clinical Records).

To run the tests:

```bash
./mvnw test

```

---

## 👤 Author

Developed by **Adrià Lorente** as an IT Academy – Java Back-End Development Bootcamp Final Project.

* [GitHub](https://github.com/alaw810)
* [LinkedIn](https://www.linkedin.com/in/adrialorente/)

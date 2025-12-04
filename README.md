# Qiromanager Backend

Qiromanager is the backend system for a digital platform designed to replace the current paper-based workflow of a massage therapy center.  
The application provides secure management of patients, clinical history, documents, and treatment sessions.

## 📘 Project Overview
The goal of this backend is to offer a clean, modular, and scalable API that allows therapists and administrators to manage:

- Patient profiles  
- Clinical notes and medical history  
- Uploaded documents (reports, X-rays, signed consent forms)  
- Treatment sessions  
- Basic activity statistics  

Authentication is based on **JWT**, and user access is controlled through **ROLE_ADMIN** and **ROLE_USER**.

## ⚙️ Tech Stack
- **Java 21**  
- **Spring Boot**  
- **Spring Web**  
- **Spring Security + JWT**  
- **Spring Data JPA**  
- **MySQL**  
- **Maven**  
- **Swagger / OpenAPI**  

## 🏗️ Architecture
The project will follow a **Clean Architecture** approach with layers such as:

- `domain`
- `application`
- `infrastructure`
- `api`
- `security`
- `config`

This helps maintain high cohesion, low coupling, and clear separation of concerns.

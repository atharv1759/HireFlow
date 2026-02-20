# HireFlow Backend — Spring Boot REST API

Full backend for the HireFlow job portal with JWT security, MySQL/H2 database, and role-based access control.

---

## 🚀 Quick Start

### Prerequisites
- Java 17+
- Maven 3.8+
- (Optional) MySQL 8+ for production

### Run with H2 (default, no setup needed)
```bash
cd job-portal-backend
mvn spring-boot:run
```

API available at: `http://localhost:8080/api`  
H2 Console: `http://localhost:8080/h2-console`  
H2 JDBC URL: `jdbc:h2:file:./data/hireflow_db`

### Switch to MySQL
Edit `application.properties`:
```properties
# Comment out H2 lines and uncomment MySQL lines
spring.datasource.url=jdbc:mysql://localhost:3306/hireflow_db?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=yourpassword
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
```

---

## 🔑 Demo Credentials
| Role | Email | Password |
|------|-------|----------|
| Job Seeker | jobseeker@demo.com | demo123 |
| Company | company@demo.com | demo123 |

---

## 🔒 Security Architecture

- **JWT Bearer Token** — stateless authentication
- **BCrypt (strength 12)** — password hashing
- **Role-based access** — `ROLE_JOBSEEKER` / `ROLE_COMPANY`
- **CORS** — configured for `localhost:5173` (Vite frontend)
- **Method-level security** — `@PreAuthorize` on sensitive endpoints
- **Global exception handling** — consistent error responses

---

## 📡 API Reference

### Base URL: `http://localhost:8080/api`

All protected routes require: `Authorization: Bearer <token>`

---

### AUTH `/api/auth`

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/auth/register` | Public | Register new user |
| POST | `/auth/login` | Public | Login and get JWT |
| POST | `/auth/refresh` | Public | Refresh JWT token |
| GET | `/auth/me` | 🔐 Any | Check token validity |

**Register body:**
```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "secret123",
  "role": "JOBSEEKER"
}
```

**Login response:**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "refreshToken": "eyJhbGciOiJIUzUxMiJ9...",
  "type": "Bearer",
  "id": 1,
  "name": "John Doe",
  "email": "john@example.com",
  "role": "JOBSEEKER",
  "expiresIn": 86400000
}
```

---

### JOBS `/api/jobs`

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/jobs` | Public | Browse jobs (filterable + paginated) |
| GET | `/jobs/:id` | Public | Get job detail |
| POST | `/jobs` | 🏢 Company | Create job listing |
| PUT | `/jobs/:id` | 🏢 Company (owner) | Update job listing |
| DELETE | `/jobs/:id` | 🏢 Company (owner) | Delete job listing |

**GET /jobs query parameters:**
- `search` — full-text search on title, description
- `type` — `FULL_TIME`, `PART_TIME`, `CONTRACT`, `INTERNSHIP`, `REMOTE`
- `level` — `ENTRY_LEVEL`, `MID_LEVEL`, `SENIOR`, `LEAD`, `EXECUTIVE`
- `category` — `ENGINEERING`, `DESIGN`, `MARKETING`, `DATA`, etc.
- `location` — partial text match
- `page` — page number (default 0)
- `size` — page size (default 10)

---

### COMPANY `/api/company`

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/company/dashboard` | 🏢 Company | Stats overview |
| GET | `/company/jobs` | 🏢 Company | All own job listings |
| GET | `/company/jobs/:jobId/applications` | 🏢 Company | Applications for a job |
| PATCH | `/company/applications/:id/status` | 🏢 Company | Update applicant status |

**PATCH /company/applications/:id/status body:**
```json
{
  "status": "SHORTLISTED",
  "notes": "Strong candidate, great portfolio"
}
```

Status values: `PENDING`, `REVIEWED`, `SHORTLISTED`, `ACCEPTED`, `REJECTED`

---

### APPLICATIONS `/api/applications`

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/applications/jobs/:jobId` | 👤 JobSeeker | Apply to a job |
| GET | `/applications/my` | 👤 JobSeeker | Get all my applications |
| GET | `/applications/jobs/:jobId/check` | 👤 JobSeeker | Check if already applied |

**POST body:**
```json
{
  "coverLetter": "I'm excited about this role...",
  "phone": "+1 (555) 000-0000",
  "portfolioUrl": "https://myportfolio.com"
}
```

---

### USERS `/api/users`

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/users/profile` | 🔐 Any | Get current user profile |
| PUT | `/users/profile` | 🔐 Any | Update profile |

---

## 📁 Project Structure

```
src/main/java/com/hireflow/
├── config/
│   ├── SecurityConfig.java       — Spring Security + JWT + CORS
│   └── DataSeeder.java           — Demo data on startup
├── controller/
│   ├── AuthController.java       — /api/auth
│   ├── JobController.java        — /api/jobs
│   ├── CompanyController.java    — /api/company
│   ├── ApplicationController.java — /api/applications
│   └── UserController.java       — /api/users
├── dto/
│   ├── request/                  — Input DTOs with validation
│   └── response/                 — Output DTOs
├── entity/
│   ├── BaseEntity.java           — Audit fields (createdAt, updatedAt)
│   ├── User.java                 — User with JOBSEEKER/COMPANY roles
│   ├── Job.java                  — Job listing
│   └── Application.java         — Job application
├── exception/
│   ├── GlobalExceptionHandler.java — Unified error responses
│   └── *Exception.java            — Custom exceptions
├── repository/                    — JPA repositories with custom queries
├── security/
│   ├── JwtUtils.java             — Token generation & validation
│   ├── JwtAuthenticationFilter.java — Request filter
│   ├── JwtAuthEntryPoint.java    — 401 handler
│   └── UserDetailsServiceImpl.java — Load user from DB
└── service/impl/
    ├── AuthService.java           — Registration, login
    ├── UserService.java           — Profile management
    ├── JobService.java            — Job CRUD + search
    └── ApplicationService.java    — Apply, track applications
```

---

## 🗄️ Database Schema

```
users              jobs                  applications
─────────────      ────────────────      ────────────────
id (PK)            id (PK)               id (PK)
name               title                 job_id (FK)
email (unique)     description           applicant_id (FK)
password           requirements          cover_letter
role               location              phone
is_active          salary                portfolio_url
title              type (enum)           status (enum)
bio                level (enum)          company_notes
location           category (enum)       created_at
phone              deadline              updated_at
skills             status (enum)
company_name       company_id (FK)
industry           created_at
company_size       updated_at
website
company_description
created_at
updated_at
```

---

## ⚙️ Environment Variables

| Property | Default | Description |
|----------|---------|-------------|
| `app.jwt.secret` | (set in properties) | JWT signing key |
| `app.jwt.expiration-ms` | `86400000` | Token lifetime (24h) |
| `app.cors.allowed-origins` | `http://localhost:5173` | Frontend URL |
| `server.port` | `8080` | Server port |

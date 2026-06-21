---
baseline_commit: 93b5ea5abee1b64be27becf544fc76b131c71405
---

# Story 1.1: Project Scaffolding & Docker Compose Setup

Status: done

## Story

As a **Developer**,
I want a fully configured monorepo with backend (Spring Boot 4.0.x, Java 21), frontend (Angular 17, TypeScript, NG-ZORRO), and Docker Compose,
So that the team can start developing features on a consistent, reproducible environment.

## Acceptance Criteria

1. **AC-1: Docker Compose khởi động thành công**
   - `docker compose up -d` → MySQL (3306), backend (8080), nginx (80) khởi động
   - `ng serve` → frontend dev server (4200)
   - Backend trả health check tại `/actuator/health`
   - Frontend render trang NG-ZORRO tại `http://localhost:4200`

2. **AC-2: Monorepo structure chính xác**
   - `backend/` — Maven, pom.xml với tất cả ARCH-1 dependencies
   - `frontend/` — package.json với tất cả ARCH-2 dependencies
   - `nginx/nginx.conf` — reverse proxy config
   - `docker-compose.yml`, `docker-compose.dev.yml`
   - `.env.example` liệt kê: JWT_SECRET, ENCRYPTION_KEY, DB_ROOT_PASSWORD, SMTP_HOST, SMTP_PORT, SMTP_USER, SMTP_PASS
   - `.gitignore` bao phủ .env, target/, node_modules/, dist/, uploads/

3. **AC-3: Spring profiles hoạt động**
   - dev: console DEBUG, mailhog SMTP, MySQL local, ddl-auto=update
   - prod: JSON structured INFO, real SMTP, ddl-auto=validate
   - test: H2 in-memory, ddl-auto=create-drop

4. **AC-4: Backend package structure tồn tại**
   - `com.hrms.common.entity/` — BaseEntity
   - `com.hrms.common.dto/` — ApiResponse, PageData
   - `com.hrms.common.security/` — SecurityConfig, JwtAuthenticationFilter, CustomUserDetails
   - `com.hrms.common.config/` — CacheConfig, CorsConfig, OpenApiConfig, AuditConfig
   - `com.hrms.common.exception/` — GlobalExceptionHandler, BusinessException, ResourceNotFoundException
   - `com.hrms.common.util/` — AesEncryptConverter, JwtUtil
   - `com.hrms.user/` — package tồn tại (entities/services sẽ tạo ở Story 1.3+)

## Tasks / Subtasks

- [x] Task 1: Verify & fix backend scaffolding (AC: #2, #4)
  - [x] 1.1: Xác nhận pom.xml có đủ ARCH-1 dependencies (spring-boot-starter-web, data-jpa, security, validation, mail, aspectj, actuator, mysql-connector-j, jjwt, springdoc-openapi, poi-ooxml, caffeine, lombok, h2 test)
  - [x] 1.2: Xác nhận HrmsApplication.java có @SpringBootApplication, @EnableJpaAuditing, @EnableAsync, @EnableScheduling
  - [x] 1.3: Xác nhận tất cả common packages tồn tại với files đúng vị trí
  - [x] 1.4: Tạo empty `com.hrms.user` package (nếu chưa có) — placeholder cho Story 1.3+
  - [x] 1.5: Xác nhận Dockerfile multi-stage build hoạt động (Java 21 eclipse-temurin)

- [x] Task 2: Verify & fix frontend scaffolding (AC: #2)
  - [x] 2.1: Xác nhận package.json có Angular 17, ng-zorro-antd, dayjs, xlsx
  - [x] 2.2: Xác nhận angular.json đúng config (SCSS, standalone components, esbuild)
  - [x] 2.3: Xác nhận app.config.ts có NG-ZORRO Vietnamese locale (vi_VN), icon imports, JWT interceptor
  - [x] 2.4: Xác nhận styles.scss có CSS custom properties (--primary, --sidebar-bg, etc.) và NG-ZORRO overrides

- [x] Task 3: Verify & fix Docker Compose (AC: #1)
  - [x] 3.1: Xác nhận docker-compose.yml: mysql (8.4, healthcheck, volumes), backend (depends_on mysql healthy), nginx (reverse proxy, serve Angular dist), mailhog (dev profile)
  - [x] 3.2: Xác nhận docker-compose.dev.yml: override cho dev environment
  - [x] 3.3: Xác nhận nginx.conf: /api/ → backend:8080, /actuator/ → backend, /swagger-ui/ → backend, / → Angular SPA (try_files)
  - [x] 3.4: Test `docker compose up -d` chạy thành công

- [x] Task 4: Verify & fix Spring profiles (AC: #3)
  - [x] 4.1: application.yml — base config, JWT/encryption env vars, CORS, springdoc
  - [x] 4.2: application-dev.yml — MySQL local, ddl-auto=update, show-sql true, mail localhost:1025, DEBUG logging
  - [x] 4.3: application-prod.yml — MySQL via env, ddl-auto=validate, real SMTP, JSON structured INFO logging
  - [x] 4.4: application-test.yml — H2 in-memory, create-drop, test JWT secret

- [x] Task 5: Verify .env.example & .gitignore (AC: #2)
  - [x] 5.1: .env.example liệt kê tất cả required environment variables
  - [x] 5.2: .gitignore bao phủ .env, target/, node_modules/, dist/, uploads/, IDE files

- [x] Task 6: End-to-end smoke test (AC: #1)
  - [x] 6.1: `docker compose up -d` → tất cả services healthy
  - [x] 6.2: Backend `/actuator/health` trả 200
  - [x] 6.3: Frontend `ng serve` → render trang NG-ZORRO tại localhost:4200
  - [x] 6.4: Nginx proxy `/api/` → backend hoạt động

## Dev Notes

### Current State Analysis

**Backend — ĐÃ SCAFFOLDING gần hoàn chỉnh.** Tất cả files dưới đây ĐÃ TỒN TẠI và đã được verify nội dung:

| File | Trạng thái | Ghi chú |
|------|-----------|---------|
| `pom.xml` | OK | Spring Boot 4.0.6, Java 21, tất cả ARCH-1 deps |
| `Dockerfile` | OK | Multi-stage build, eclipse-temurin:21 |
| `HrmsApplication.java` | OK | @SpringBootApplication + @EnableJpaAuditing + @EnableAsync + @EnableScheduling |
| `BaseEntity.java` | OK | id, active, createdAt/updatedAt, createdBy/updatedBy, @SQLRestriction |
| `ApiResponse.java` | OK | code, message, data + factory methods (success, created, error) |
| `PageData.java` | OK | Pagination wrapper |
| `SecurityConfig.java` | OK | Spring Security filter chain |
| `JwtAuthenticationFilter.java` | OK | OncePerRequestFilter, JWT validation |
| `CustomUserDetails.java` | OK | userId, role, departmentId |
| `JwtUtil.java` | OK | HMAC-SHA256 token generation/validation |
| `CorsConfig.java` | OK | Dev: localhost:4200, configurable origins |
| `CacheConfig.java` | OK | Caffeine in-memory cache |
| `OpenApiConfig.java` | OK | Springdoc Swagger UI config |
| `AuditConfig.java` | OK | JPA auditing config |
| `GlobalExceptionHandler.java` | OK | @ControllerAdvice → ApiResponse |
| `BusinessException.java` | OK | Custom runtime exception |
| `ResourceNotFoundException.java` | OK | 404 exception |
| `AesEncryptConverter.java` | OK | AES-256-GCM JPA converter |
| `application.yml` | OK | Base config with JWT, encryption, CORS |
| `application-dev.yml` | OK | MySQL local, ddl-auto=update, DEBUG |
| `application-prod.yml` | OK | MySQL env, ddl-auto=validate, JSON logging |
| `application-test.yml` | OK | H2 in-memory, create-drop |

**Frontend — ĐÃ SCAFFOLDING hoàn chỉnh:**

| File | Trạng thái | Ghi chú |
|------|-----------|---------|
| `package.json` | OK | Angular 17.3, ng-zorro-antd 17.4.1, dayjs, xlsx |
| `angular.json` | OK | SCSS, esbuild, standalone components |
| `app.config.ts` | OK | vi_VN locale, NZ icons, JWT interceptor |
| `app.routes.ts` | OK | Lazy-loaded routes, authGuard, roleGuard |
| `styles.scss` | OK | CSS custom properties, NG-ZORRO overrides |
| `main-layout/` | OK | Sidebar + topbar layout |
| `auth-layout/` | OK | Login page layout |
| Page stubs | OK | dashboard, employee, department, position, contract, attendance, leave, payroll, approval, report, config, profile, login, 403, 404 |
| Core services | OK | auth.service.ts, menu.service.ts |
| Guards | OK | auth.guard.ts, role.guard.ts |
| Interceptors | OK | jwt.interceptor.ts |
| Models | OK | api-response, menu, user models |

**Infrastructure — ĐÃ SCAFFOLDING hoàn chỉnh:**

| File | Trạng thái | Ghi chú |
|------|-----------|---------|
| `docker-compose.yml` | OK | mysql 8.4 (healthcheck), backend, nginx, mailhog (dev profile) |
| `docker-compose.dev.yml` | OK | Dev overrides |
| `nginx/nginx.conf` | OK | /api/ reverse proxy, SPA try_files |
| `.env.example` | OK | Tất cả vars: DB, JWT, ENCRYPTION, SMTP, FILE_UPLOAD |
| `.env` | EXISTS | Actual env file (gitignored) |
| `.gitignore` | OK | .env, target/, node_modules/, dist/, uploads/, IDE |

### Architecture Compliance (ARCH refs)

- **ARCH-1** (Backend scaffolding): Spring Boot 4.0.6, Java 21, Maven, tất cả dependencies → **PASS**
- **ARCH-2** (Frontend scaffolding): Angular 17.3, TypeScript strict, NG-ZORRO 17.4.1, dayjs, xlsx → **PASS**
- **ARCH-3** (Docker Compose): nginx:alpine, backend (Java 21), mysql:8.4, mailhog (dev) → **PASS**
- **ARCH-4** (Monorepo): backend/ + frontend/ + nginx/ + docker-compose.yml + .env.example → **PASS**
- **ARCH-5** (Spring profiles): dev (DEBUG, mailhog), prod (JSON INFO, SMTP), test (H2) → **PASS**
- **ARCH-6** (Secrets via .env): JWT_SECRET, ENCRYPTION_KEY, DB_ROOT_PASSWORD, SMTP creds → **PASS**
- **ARCH-7** (Logging): SLF4J + Logback, dev console DEBUG, prod JSON INFO → **PASS**

### Potential Issues to Watch

1. **Git status shows deleted files from old package structure**: Files were reorganized from `com.hrms.auth`, `com.hrms.employee`, `com.hrms.user` to `com.hrms.common.*`. These deletions are expected from a prior refactoring session. The dev agent should NOT try to recreate the old structure.

2. **`com.hrms.user` package**: Old user/ files (User.java, UserController.java, etc.) were deleted during refactoring. This package is empty — entities/services will be created in Story 1.3 (JWT Auth) and 1.5 (User Account Management). For Story 1.1, only the package directory needs to exist.

3. **Frontend runs outside Docker**: `ng serve` on port 4200 is the dev workflow — not managed by Docker. Nginx serves the production build from `frontend/dist/`. This is by design.

4. **application-dev.yml datasource**: Uses `localhost:3306` which works when running backend outside Docker. Inside Docker, the backend uses env var `DB_HOST=mysql` from docker-compose.yml.

5. **BaseEntity uses `@SQLRestriction("active = true")`** (Hibernate 6.4+/Spring Boot 4.x syntax, replacing deprecated `@Where`). Correct for Spring Boot 4.0.x.

### Enforcement Rules for This Story

1. Do NOT create entity classes — only verify package structure exists
2. Do NOT modify existing working code unless it fails verification
3. Do NOT add features beyond scaffolding verification
4. Focus on: verify → fix gaps → smoke test → done
5. Security configs (JwtAuthenticationFilter, SecurityConfig) are placeholder stubs for Story 1.3 — do NOT implement full JWT logic here

### Project Structure Notes

Project follows the architecture-defined monorepo layout exactly:
```
hrms/
├── backend/          # Spring Boot 4.0.x, Java 21, Maven
│   ├── pom.xml
│   ├── Dockerfile
│   └── src/main/java/com/hrms/
│       ├── HrmsApplication.java
│       ├── common/{entity,dto,security,config,exception,util}/
│       └── user/     # Empty — populated in Story 1.3+
├── frontend/         # Angular 17, TypeScript strict, NG-ZORRO
│   ├── package.json
│   ├── angular.json
│   └── src/app/{core,layouts,pages,shared}/
├── nginx/nginx.conf
├── docker-compose.yml
├── docker-compose.dev.yml
├── .env.example
└── .gitignore
```

### References

- [Source: epics.md — Story 1.1 acceptance criteria, lines 301-324]
- [Source: architecture.md — ARCH-1 through ARCH-7, Starter Template Evaluation]
- [Source: architecture.md — Project Structure & Boundaries, complete directory layout]
- [Source: prd.md — Section 1 Tầm nhìn, NFR-12 through NFR-16]

## Dev Agent Record

### Agent Model Used

claude-sonnet-4-6 (2026-06-21)

### Debug Log References

- Test failure: data.sql ran before Hibernate schema creation → fixed với `defer-datasource-initialization: true`
- Health DOWN: application-dev.yml hardcoded `localhost` cho DB và SMTP → không hoạt động trong Docker container
- Port 3306 conflict: MySQL local on host → dùng `DB_PORT=13306` cho host port mapping khi test

### Completion Notes List

- ✅ AC-1: Docker Compose khởi động thành công (mysql healthy, backend UP, nginx running, mailhog dev profile)
- ✅ AC-1: `/actuator/health` → `{"status":"UP"}` verified qua cả port 8080 và nginx proxy
- ✅ AC-1: `ng serve` → HTTP 200 tại localhost:4200
- ✅ AC-2: Monorepo structure đúng. .env.example có đủ vars. .gitignore đúng.
- ✅ AC-3: Spring profiles hoạt động (dev=MySQL+mailhog, prod=SMTP+validate, test=H2+create-drop)
- ✅ AC-4: Package structure đúng — SecurityConfig.java đã được move về `com.hrms.common.security` (trước đó ở common.config)

**Fixes applied:**
1. Moved SecurityConfig.java từ `com.hrms.common.config` → `com.hrms.common.security` (đúng AC-4)
2. `application.yml`: thêm `spring.jpa.defer-datasource-initialization: true` — data.sql cần chạy sau Hibernate schema
3. `application-dev.yml`: datasource URL dùng `${DB_HOST:localhost}:${DB_PORT:3306}` — hoạt động cả local lẫn Docker
4. `application-dev.yml`: mail host/port dùng `${SMTP_HOST:localhost}/${SMTP_PORT:1025}` — hoạt động cả local lẫn Docker
5. `application-test.yml`: thêm `spring.sql.init.mode: never` — test dùng H2 clean state, không seed data

### File List

- `backend/src/main/java/com/hrms/common/security/SecurityConfig.java` (moved from config/, package updated)
- `backend/src/main/java/com/hrms/common/config/SecurityConfig.java` (deleted)
- `backend/src/main/resources/application.yml` (added defer-datasource-initialization)
- `backend/src/main/resources/application-dev.yml` (env vars for DB_HOST, DB_PORT, DB_NAME, SMTP_HOST, SMTP_PORT; restored ${DB_NAME:hrms} in JDBC URL)
- `backend/src/main/resources/application-prod.yml` (fixed DB_PASSWORD → DB_ROOT_PASSWORD to match docker-compose.yml)
- `backend/src/main/resources/application-test.yml` (added sql.init.mode: never)

### Code Review Findings (2026-06-21)

**🔴 HIGH — Fixed:** `application-prod.yml` used `${DB_PASSWORD}` but docker-compose.yml only sets `DB_ROOT_PASSWORD` → prod Docker deploy would fail on DataSource init. Fixed to `${DB_ROOT_PASSWORD}`.

**🟡 MEDIUM — Deferred to Story 1.3:** `JwtUtil`/`JwtAuthenticationFilter` parses JWT 3× per request (validateToken + getUserId + getRole each call parseSignedClaims). Fix in Story 1.3 when filter is fully wired: return `Claims` from validation and pass through.

**🔵 LOW — Fixed:** `application-dev.yml` JDBC URL had `hrms` hardcoded; docker-compose passes `DB_NAME` env var but it was silently ignored. Restored `${DB_NAME:hrms}`.

### Change Log

- 2026-06-21: Story 1.1 implemented — verified scaffolding, fixed 5 config gaps, all smoke tests pass
- 2026-06-21: Code review — fixed prod DB_PASSWORD var mismatch (HIGH) + DB_NAME configurable (LOW); JWT triple-parse deferred to Story 1.3

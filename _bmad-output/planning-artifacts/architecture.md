---
stepsCompleted: [1, 2, 3, 4, 5, 6, 7, 8]
inputDocuments:
  - planning-artifacts/prds/prd-HRMS-2026-06-09/prd.md
  - planning-artifacts/briefs/brief-HRMS-2026-06-08/brief.md
  - planning-artifacts/prds/prd-HRMS-2026-06-09/reconcile-brief.md
workflowType: 'architecture'
lastStep: 8
status: 'complete'
completedAt: '2026-06-09'
project_name: 'HRMS'
user_name: 'Huylaiphu3'
date: '2026-06-09'
---

# Architecture Decision Document

_This document builds collaboratively through step-by-step discovery. Sections are appended as we work through each architectural decision together._

## Project Context Analysis

### Requirements Overview

**Functional Requirements:**
38 FRs across 13 features covering: Auth & RBAC (FR-1→3), Employee Management (FR-7→9), Labor Contracts (FR-10→11), Attendance (FR-12→15), Leave Management (FR-16→18), Payroll (FR-19→24), Notifications (FR-25→26), Audit Log (FR-27→29), Employee Documents (FR-30→31), Dashboard (FR-32→34), Dynamic Approval Workflow (FR-35→37), Data Import (FR-38), Reports (FR-39→41).

Architecturally significant modules:
- Dynamic Approval Workflow requires a state machine engine with configurable templates per-module
- Payroll engine needs configurable tax/insurance formulas with Vietnamese labor law compliance
- Audit log requires AOP/interceptor pattern for automatic immutable recording across all entities

**Non-Functional Requirements:**
- Security: bcrypt (cost ≥ 12), JWT (access 30min + refresh 7d), AES-256 at-rest encryption for sensitive data (CCCD), HTTPS mandatory, file upload whitelist validation
- Performance: API < 2s @ 500 concurrent users, payroll 500 employees < 30s, Excel export < 10s for 500 rows
- Deployment: Docker Compose (Spring Boot + MySQL + Angular + Nginx), auto DB migration, backup/restore scripts
- Compliance: BHXH/BHYT/BHTN rates + PIT 7-bracket progressive tax configurable, 3 contract types per Labor Code 2019, OT coefficients 1.5x/2.0x/3.0x

**Scale & Complexity:**

- Primary domain: Full-stack web application (SPA + REST API + RDBMS)
- Complexity level: Medium
- Estimated architectural components: 12–15
- Target scale: 500 nhân viên trên single instance

### Technical Constraints & Dependencies

- Stack updated: Java 21 / Spring Boot 4.0.x / MySQL 8.4 LTS / Angular 17.x (see Starter Template Evaluation)
- Infrastructure: VPS 4 vCPU / 8GB RAM minimum
- File storage: Local filesystem V1 (50GB minimum), S3 migration path V2
- Notification: SMTP V1
- Language: Vietnamese-only UI V1
- Auth: username/password + JWT only (no SSO/LDAP V1)
- One user = one userRole (single-userRole constraint)

### Cross-Cutting Concerns Identified

1. **RBAC Authorization** — 3-tier userRole hierarchy (Admin > Manager > Employee) with scope filtering (company-wide / department / self-only).
2. **Audit Logging** — Automatic immutable recording of all CUD operations with old/new values. Must be implemented as infrastructure (AOP/interceptor), not per-module code.
3. **Soft Delete** — Entities are never hard-deleted. All queries must respect active/inactive status.
4. **Event-Driven Notifications** — Business events (leave request, approval, payroll confirmation, contract expiry) trigger dual-channel notifications (email + in-app).
5. **Vietnamese Labor Law Compliance** — Tax and insurance calculation formulas must be configurable and correct to the decimal. This is a correctness requirement, not a performance one.
6. **Pagination & Filtering** — All list endpoints support search, filter, pagination.

## Starter Template Evaluation

### Primary Technology Domain

Full-stack web application: Java REST API backend + Angular SPA frontend + MySQL database.

### Version Research (June 2026)

| Technology | PRD Specified | Current Stable | Selected |
|-----------|--------------|---------------|----------|
| Spring Boot | 3.x | 4.0.6 | **4.0.x** — 3.5 EOL June 2026 |
| Java | 17+ | 21 LTS | **21** — recommended by Spring Boot 4 |
| MySQL | 8.x | 8.4 LTS | **8.4 LTS** — 8.0 EOL April 2026 |
| Angular | — | 17.x | **17.x** — Signals stable, standalone components available, new control flow syntax |

### Backend Starter: Spring Initializr

**Initialization:**
Generated via start.spring.io with Spring Boot 4.0.x, Java 21, Maven, Jar packaging.

**Core Dependencies:**
- spring-boot-starter-web (REST API + embedded Tomcat)
- spring-boot-starter-data-jpa (Hibernate ORM)
- spring-boot-starter-security (auth filter chain)
- spring-boot-starter-validation (bean validation)
- spring-boot-starter-mail (SMTP notifications)
- mysql-connector-j (MySQL 8.4 driver)
- spring-boot-starter-test (JUnit 5 + Mockito)
- lombok (boilerplate reduction)

**Additional Dependencies (added manually):**
- jjwt (io.jsonwebtoken) — JWT generation/validation
- springdoc-openapi — API documentation (Swagger UI)
- apache-poi — Excel export/import
- spring-boot-starter-aop — AOP for audit logging

**Database Migration:** Hibernate auto DDL (`spring.jpa.hibernate.ddl-auto=update` for dev, `validate` for prod) + SQL scripts for seed data and schema changes. No Flyway/Liquibase.

**Architectural Decisions Provided by Starter:**
- Maven build with Spring Boot parent POM
- Embedded Tomcat servlet container
- Hibernate as JPA provider with MySQL dialect
- Spring Security filter chain architecture
- Application profiles (dev, prod, test)
- Auto-configuration and component scanning
- src/main/java + src/main/resources structure

### Frontend Starter: Angular CLI + TypeScript

**Initialization Command:**
```bash
ng new hrms-frontend --style=scss --routing --strict --standalone
```

**Core Stack:**
- Angular 17.x with TypeScript strict mode
- Angular CLI dev server (HMR) + esbuild production builds
- ESLint for code quality

**Additional Dependencies (added manually):**
- ng-zorro-antd — UI component library (Ant Design cho Angular, Vietnamese locale support)
- dayjs — Date/time handling (Vietnamese locale)
- xlsx — Excel export on client side

**Built-in (không cần thêm):**
- Angular Router — Client-side routing (built-in)
- HttpClient + HttpInterceptor — HTTP client với JWT attach (built-in)
- Reactive Forms — Form handling và validation (built-in)
- Angular Signals — Reactive state management (built-in từ Angular 17+)
- RxJS — Async operations, HttpClient responses (built-in)

**Architectural Decisions Provided by Starter:**
- TypeScript strict mode enforced
- esbuild-based dev server + production builds
- Angular CLI build pipeline with tree-shaking
- Path aliases via tsconfig.json
- Environment files (`environment.ts`, `environment.prod.ts`)
- Standalone components with explicit `standalone: true` (not default in Angular 17)
- Dependency Injection container built-in

**Note:** Project initialization using these commands should be the first implementation story. Backend and frontend are separate projects within a monorepo structure, connected via Docker Compose.

## Core Architectural Decisions

### Decision Priority Analysis

**Critical Decisions (Block Implementation):**
- JWT auth with refresh token rotation
- Spring Security filter chain
- REST API conventions and error format

**Important Decisions (Shape Architecture):**
- Sensitive data encryption via JPA AttributeConverter
- Caffeine in-memory caching for dashboard stats
- Frontend feature-module structure
- Docker Compose service topology

**Deferred Decisions (Post-MVP):**
- Redis distributed cache (V2 — when scaling beyond single instance)
- WebSocket/SSE real-time notifications (V2)
- Cloud storage S3 integration (V2)

### Data Architecture

**Base Entity Strategy:**
Abstract `BaseEntity` class inherited by all business entities:
- `id` (Long, @GeneratedValue AUTO_INCREMENT)
- `active` (Boolean, default true — soft delete flag)
- `createdAt` (LocalDateTime, @CreatedDate)
- `updatedAt` (LocalDateTime, @LastModifiedDate)
- `createdBy` (Long, @CreatedBy — user_id from SecurityContext)
- `updatedBy` (Long, @LastModifiedBy)

Soft delete: `@SQLRestriction("active = true")` on BaseEntity. Hard delete never exposed via API.

**Sensitive Data Encryption:**
JPA `@Convert(converter = AesEncryptConverter.class)` on sensitive fields (CCCD number). AES-256-GCM encryption. Key loaded from environment variable `ENCRYPTION_KEY`. Converter handles encrypt on write, decrypt on read — transparent to business code. Encrypted fields stored as Base64 strings in DB (VARCHAR). Salary fields lưu plaintext `DECIMAL(15,0)` — không mã hoá.

**Caching Strategy:**
Spring Cache + Caffeine (in-memory, single-instance):
- Dashboard aggregates: cache 5 minutes, evict on relevant data changes
- Leave balances: cache per-request (avoid repeated queries within payroll calculation)
- No distributed cache in V1 — Caffeine is sufficient for single-instance deployment

**Database Migration:**
Hibernate `ddl-auto=update` for dev environment, `ddl-auto=validate` for prod. Schema changes in production managed via versioned SQL scripts (`src/main/resources/db/migration/V{N}__{description}.sql`) executed manually or via a startup ApplicationRunner. Seed data (default admin account, default tax brackets) in `data.sql`.

### Authentication & Security

**JWT Implementation:**
- Library: jjwt (io.jsonwebtoken)
- Access token: HMAC-SHA256 signed, 30-minute TTL. Payload: `{ sub: userId, userRole, iat, exp }`
- Refresh token: UUID stored in `refresh_tokens` table (columns: token, user_id, expires_at, revoked). TTL 7 days.
- Refresh flow: client sends refresh token → server validates in DB → issues new access + refresh token → old refresh token marked revoked (rotation).
- JWT secret: loaded from env variable `JWT_SECRET` (minimum 256-bit).

**Spring Security Filter Chain:**
1. `JwtAuthenticationFilter` (extends `OncePerRequestFilter`): extracts JWT from `Authorization: Bearer` header → validates signature + expiration → creates `CustomUserDetails` (userId, userRole, departmentId) → sets `SecurityContextHolder`.
2. Endpoint authorization: `@PreAuthorize("hasRole('ADMIN')")` and custom method-level annotations for department-scoped access.
3. Public endpoints: only `/api/v1/auth/login` and `/api/v1/auth/refresh`.

**CORS Policy:**
Configured in `WebSecurityConfig`:
- Dev: allow `http://localhost:4200` (Angular CLI dev server)
- Prod: allow configured domain(s) from `application.yml`
- Allowed methods: GET, POST, PUT, DELETE, PATCH
- Credentials: true (for cookie-based refresh tokens if needed later)

**Password Security:**
BCryptPasswordEncoder with strength 12 (cost factor). Default password for new accounts: randomly generated, sent via email, force-change on first login.

### API & Communication Patterns

**REST API Convention:**
- Base path: `/api/v1/`
- Resource naming: plural nouns, kebab-case (`/api/v1/users`, `/api/v1/leave-requests`)
- Nested resources: `/api/v1/users/{id}/contracts`, `/api/v1/users/{id}/documents`
- Actions: POST for commands (`/api/v1/payroll/generate`, `/api/v1/attendance/check-in`)

**Unified API Response Wrapper:**
```java
public class ApiResponse<T> {
    private int code;       // HTTP status code (200, 400, 404, 500...)
    private String message; // Human-readable message (Vietnamese)
    private T data;         // Response payload (object, list, page, or null on error)
}
```

Success example:
```json
{ "code": 200, "message": "Thành công", "data": { "id": 1, "name": "Nguyễn Văn A" } }
```

Error example:
```json
{ "code": 400, "message": "Ngày bắt đầu không được nhỏ hơn ngày hiện tại", "data": null }
```

Paginated list — `data` contains page info:
```json
{
  "code": 200,
  "message": "Thành công",
  "data": {
    "content": [...],
    "page": 0,
    "size": 20,
    "totalElements": 150,
    "totalPages": 8
  }
}
```
Handled by `@ControllerAdvice` global exception handler. All endpoints return `ApiResponse<T>`. Default page size: 20. Max page size: 100. Query params: `?page=0&size=20&sort=createdAt,desc`.

**API Documentation:**
springdoc-openapi with Swagger UI at `/swagger-ui.html`. Annotations on controllers. JWT auth configured in OpenAPI security scheme. Grouped by module (employee, leave, payroll, etc.).

### Frontend Architecture

**Project Structure:**
```
src/
  app/
    shared/              # Shared UI components (DataTable, PageHeader, StatusBadge)
      components/
      directives/
      pipes/
      models/            # Global TypeScript interfaces
      utils/             # Helpers (date formatting, currency, validators)
    core/                # Singleton services, guards, interceptors
      services/          # AuthService, NotificationService
      interceptors/      # JwtInterceptor, ErrorInterceptor
      guards/            # RoleGuard, AuthGuard
    layouts/             # AdminLayout, EmployeeLayout
    features/            # Feature modules (lazy-loaded)
      auth/              # Login, password reset
      user/              # User/Employee CRUD, search
      contract/          # Contract management
      attendance/        # Check-in/out, timesheet
      leave/             # Leave requests, approval
      payroll/           # Payroll generation, payslips
      notification/      # Notification list, badge
      audit-log/         # Audit log viewer
      document/          # Employee document upload/view
      dashboard/         # HR, payroll, leave dashboards
      workflow/          # Approval workflow config
      import/            # Excel import
      report/            # Reports + Excel export
      settings/          # Company settings, user management
    app.component.ts
    app.config.ts
    app.routes.ts
  environments/
    environment.ts
    environment.prod.ts
  main.ts
```

**Auth State & Route Guards:**
- `AuthService` (Angular Service + Signals): stores user and tokens via `signal()`. Persisted in localStorage.
- `JwtInterceptor` (HttpInterceptor): attaches `Authorization: Bearer` header on every request. On 401 → attempt token refresh → if fail → redirect to login.
- `RoleGuard` (CanActivate): checks userRole from AuthService. Unauthorized → redirect to dashboard or 403 page.
- Route structure: `/login`, `/dashboard`, `/employees`, `/leave-requests`, `/payroll`, `/settings`.
- Lazy loading: feature modules loaded on demand via `loadComponent`/`loadChildren` in route config.

**Form Handling:**
Angular Reactive Forms + NG-ZORRO Form components for all forms. Reactive Forms provides validation, dynamic fields, form arrays. NG-ZORRO provides Vietnamese locale, form layout, and error display. No additional form library needed.

**Server State:**
Angular `HttpClient` returns `Observable<T>`, consumed via `async` pipe or converted to Signals via `toSignal()`. Services cache data using `signal()` and `computed()`. Manual cache invalidation on successful mutations via service methods. No external state management library needed.

### Infrastructure & Deployment

**Docker Compose Topology:**
```yaml
services:
  nginx:
    image: nginx:alpine
    ports: ["80:80", "443:443"]
    # Serves Angular production build + reverse proxy to backend
  backend:
    build: ./backend
    # Spring Boot JAR, Java 21
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - DB_HOST=mysql
      - JWT_SECRET=${JWT_SECRET}
      - ENCRYPTION_KEY=${ENCRYPTION_KEY}
      - SMTP_HOST=${SMTP_HOST}
    depends_on: [mysql]
  mysql:
    image: mysql:8.4
    volumes: ["mysql_data:/var/lib/mysql"]
    environment:
      - MYSQL_ROOT_PASSWORD=${DB_ROOT_PASSWORD}
      - MYSQL_DATABASE=hrms
  # Dev only:
  mailhog:
    image: mailhog/mailhog
    ports: ["8025:8025"]
    profiles: ["dev"]
```

**Logging Strategy:**
- SLF4J + Logback (Spring Boot default)
- Dev: console output, DEBUG level
- Prod: JSON structured logging to stdout (Docker captures), INFO level
- Request/response logging: custom filter logs method + URL + status + duration. Excludes request/response bodies for sensitive endpoints (login, payroll).
- Audit log: separate from application log — stored in database (audit_logs table), not file-based.

**Environment Configuration:**
- Spring profiles: `dev` (H2 or local MySQL, debug logging, mailhog), `prod` (MySQL 8.4, JSON logging, real SMTP), `test` (H2 in-memory, mocked services)
- Secrets: environment variables via Docker Compose `.env` file (JWT_SECRET, ENCRYPTION_KEY, DB_ROOT_PASSWORD, SMTP credentials)
- Application config: `application.yml` with profile-specific overrides (`application-dev.yml`, `application-prod.yml`)

### Decision Impact Analysis

**Implementation Sequence:**
1. Project scaffolding (Spring Initializr + Angular CLI) + Docker Compose setup
2. BaseEntity + repository methods (foundation for all entities)
3. JWT auth + Spring Security filter chain + user/userRole entities
4. Core modules build on top: User (profile + auth) → Contract → Attendance → Leave → Payroll
5. Cross-cutting: Audit log AOP, notification service, caching
6. Frontend: auth flow → layouts → feature modules in parallel with backend
7. Dashboard + Reports (depend on data from core modules)
8. Import (depends on employee module)

**Cross-Component Dependencies:**
- AuditLogAspect ← Every entity CUD operation
- NotificationService ← Leave approval, payroll confirmation, contract expiry
- PayrollService ← AttendanceService + LeaveService + ConfigService
- ApprovalWorkflowEngine ← LeaveService (V1), extensible to OT/expense (V2)

## Implementation Patterns & Consistency Rules

### Naming Patterns

**Database Naming Conventions:**
- Tables: `snake_case`, plural — `users`, `leave_requests`, `audit_logs`, `approval_steps`
- Columns: `snake_case` — `created_at`, `user_id`, `base_salary`
- Foreign keys: `{referenced_table_singular}_id` — `user_id`, `department_id`
- Indexes: `idx_{table}_{columns}` — `idx_users_email`, `idx_leave_requests_status`
- Unique constraints: `uk_{table}_{columns}` — `uk_users_cccd`, `uk_users_email`
- Join tables: `{table1}_{table2}` alphabetically — `user_allowances`

**Java Code Naming Conventions:**
- Packages: `com.hrms.{module}` — `com.hrms.user`, `com.hrms.payroll`, `com.hrms.common`
- Entities: PascalCase singular — `User`, `LeaveRequest`, `ApprovalStep`
- Repositories: `{Entity}Repository` — `UserRepository`, `LeaveRequestRepository`
- Services: `{Entity}Service` (interface) + `{Entity}ServiceImpl` (implementation)
- Controllers: `{Entity}Controller` — `UserController`, `LeaveRequestController`
- DTOs: `{Entity}{Action}Request` / `{Entity}Response` — `UserCreateRequest`, `UserResponse`, `LeaveRequestResponse`
- Mappers: `{Entity}Mapper` — converts entity ↔ DTO
- Constants: `UPPER_SNAKE_CASE` — `MAX_LOGIN_ATTEMPTS`, `DEFAULT_PAGE_SIZE`
- Enums: PascalCase type, `UPPER_SNAKE_CASE` values — `LeaveType.ANNUAL_LEAVE`, `RequestStatus.PENDING`

**Angular/TypeScript Code Naming Conventions:**
- Component files: kebab-case — `user-list.component.ts`, `leave-request-form.component.ts`
- Component class: PascalCase + `Component` suffix — `UserListComponent`, `LeaveRequestFormComponent`
- Service files: kebab-case — `user.service.ts`, `auth.service.ts`
- Service class: PascalCase + `Service` suffix — `UserService`, `AuthService`
- Guard files: kebab-case — `userRole.guard.ts`, `auth.guard.ts`
- Interceptor files: kebab-case — `jwt.interceptor.ts`, `error.interceptor.ts`
- Model/Interface files: kebab-case — `user.model.ts`, `api-response.model.ts`
- Interfaces: PascalCase with `I` prefix — `IUser`, `ILeaveRequest`, `IApiResponse<T>`
- Enums (TS): match backend enum values — `LeaveType.ANNUAL_LEAVE`
- Pipe files: kebab-case — `currency-vnd.pipe.ts`, `date-vn.pipe.ts`
- Utility files: kebab-case — `date.utils.ts`, `currency.utils.ts`
- Module structure: each feature folder contains `*.component.ts`, `*.component.html`, `*.component.scss`

**API Naming Conventions:**
- Endpoints: kebab-case, plural nouns — `/api/v1/users`, `/api/v1/leave-requests`
- JSON fields: `camelCase` — `{ "userId": 1, "firstName": "Minh", "createdAt": "..." }`
- Query params: `camelCase` — `?departmentId=5&status=active&page=0&size=20`

### Structure Patterns

**Backend Package Structure:**
```
com.hrms/
  common/
    entity/          # BaseEntity
    dto/             # ApiResponse<T>, PageData<T>
    security/        # JwtFilter, SecurityConfig, CustomUserDetails
    audit/           # AuditLogAspect, AuditLog entity
    config/          # CacheConfig, CorsConfig, SwaggerConfig
    exception/       # GlobalExceptionHandler, BusinessException, ResourceNotFoundException
    util/            # DateUtils, ExcelUtils, EncryptionConverter
  user/
    entity/          # User (auth + profile), Role (enum), Department, Position, RefreshToken
    repository/      # UserRepository, RefreshTokenRepository
    service/         # UserService, UserServiceImpl, AuthService, AuthServiceImpl
    controller/      # UserController, AuthController
    dto/             # UserCreateRequest, UserResponse, LoginRequest, LoginResponse, UserMapper
  leave/             # Same structure per module
  payroll/
  attendance/
  contract/
  workflow/
  notification/
  document/
  report/
```

**Test Location:** `src/test/java/com/hrms/{module}/` mirrors main. Naming: `{Class}Test.java` (unit), `{Class}IntegrationTest.java` (integration).

**Frontend Feature Module Structure:**
```
features/{module}/
  components/        # Module-specific UI components (standalone Angular components)
  services/          # Module-specific API services (HttpClient calls)
  models/            # Module-specific TypeScript interfaces
  guards/            # Module-specific route guards (if any)
  {module}.routes.ts # Module route config (lazy-loaded)
```

### Format Patterns

**Unified API Response — All endpoints return `ApiResponse<T>`:**
```java
public class ApiResponse<T> {
    private int code;
    private String message;
    private T data;
}
```
- Success: `{ code: 200, message: "Thành công", data: {...} }`
- Created: `{ code: 201, message: "Tạo thành công", data: {...} }`
- Error: `{ code: 400, message: "Lỗi validation...", data: null }`
- Not found: `{ code: 404, message: "Không tìm thấy nhân viên", data: null }`
- Paginated list: `data` contains `PageData<T>` — `{ content: [...], page, size, totalElements, totalPages }`

**Date/Time Format:**
- API JSON: ISO 8601 — `"2026-06-09T10:00:00"` (Vietnam timezone assumed, no TZ suffix)
- Database: `DATETIME` column type
- Display UI: `DD/MM/YYYY` (date), `DD/MM/YYYY HH:mm` (datetime) — Vietnamese convention
- Payroll period: `YYYY-MM` — `"2026-06"`

**Money/Currency Format:**
- Database: `DECIMAL(15,0)` for VND amounts, `DECIMAL(5,4)` for rates (e.g., 0.0800 for 8%)
- API: Number type — `{ "baseSalary": 15000000 }`
- Display: `Intl.NumberFormat('vi-VN')` — `15.000.000 ₫`

**Enum Format:**
- Database: `VARCHAR` storing enum name — `"ANNUAL_LEAVE"`, `"PENDING"`, `"APPROVED"`
- Java: `@Enumerated(EnumType.STRING)`
- API JSON: string — `{ "status": "APPROVED", "leaveType": "ANNUAL_LEAVE" }`

**Null Handling:**
- Jackson: `@JsonInclude(Include.NON_NULL)` globally — omit null fields from response
- Frontend: null-safe access (`employee?.department?.name`)
- Database: explicit `NOT NULL` constraints where business requires

### Communication Patterns

**Application Events (Spring):**
- Event class naming: `{Entity}{Action}Event` — `LeaveRequestCreatedEvent`, `PayrollConfirmedEvent`
- Payload: `{ entityId, actorId, ...relevant data }`
- Published via `ApplicationEventPublisher.publishEvent()`
- Listeners: `@EventListener` in NotificationService, AuditLogService
- Email sending: `@Async` on listener methods

**Angular HTTP & State Conventions:**
- Service methods return `Observable<ApiResponse<T>>` via `HttpClient`
- Components consume via `async` pipe in templates or `toSignal()` in component class
- Cache invalidation: services expose `refresh()` methods that re-fetch and update internal signals
- Mutations: service method call → `subscribe` → on success: call `refresh()` + show `NzMessageService.success()`

### Process Patterns

**Error Handling (Backend):**
- Custom exceptions extend `RuntimeException`: `BusinessException(int code, String message)`, `ResourceNotFoundException`
- `@ControllerAdvice GlobalExceptionHandler` catches all → returns `ApiResponse<Void>` with appropriate code
- Never expose stack traces in API
- Log full exception server-side at ERROR level

**Error Handling (Frontend):**
- `ErrorInterceptor` (HttpInterceptor): unwrap `ApiResponse` — if `code >= 400`, throw with message
- 401 → refresh token → retry original request. Refresh fail → redirect `/login` via Router
- 403 → redirect to dashboard or show forbidden message
- NG-ZORRO `NzMessageService.error(response.message)` for user-facing errors
- Service-level error handling via RxJS `catchError` operator

**Loading & UI States:**
- Loading signals managed per-component or per-service via `signal<boolean>()`
- `nz-spin` (NG-ZORRO) for page-level loading
- `[nzLoading]` prop on buttons during form submission
- `nz-skeleton` components for initial data fetch

**Validation Pattern:**
- Backend: `@Valid` on request DTOs + custom validators for business rules. Always the source of truth.
- Frontend: Angular Reactive Forms `Validators` + NG-ZORRO form error display for immediate feedback. Never rely on frontend-only validation.
- Backend validation errors: set `code: 400`, `message` describes the issue

### Enforcement Guidelines

**All AI Agents MUST:**
1. Extend `BaseEntity` for every business entity (automatic soft delete, audit fields)
2. Follow naming conventions exactly: snake_case DB, camelCase Java/JSON, PascalCase components
3. Return `ApiResponse<T>` from every controller method — no raw objects
4. Throw custom exceptions from service layer, never return error codes
5. Use Angular `HttpClient` for all API calls — no `fetch/axios`. Consume via `async` pipe or `toSignal()`
6. Use NG-ZORRO components exclusively — no mixing UI libraries
7. Create DTOs for request/response — never expose entities directly in API
8. Write service interface + implementation — controllers call interfaces only
9. Place code in the correct package/module — no cross-module direct entity access (use service calls)

## Project Structure & Boundaries

### Complete Project Directory Structure

```
hrms/
├── docker-compose.yml
├── docker-compose.dev.yml
├── .env.example
├── .gitignore
├── README.md
│
├── backend/
│   ├── pom.xml
│   ├── Dockerfile
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/hrms/
│   │   │   │   ├── HrmsApplication.java
│   │   │   │   ├── common/
│   │   │   │   │   ├── entity/BaseEntity.java
│   │   │   │   │   ├── dto/ApiResponse.java, PageData.java
│   │   │   │   │   ├── security/SecurityConfig.java, JwtAuthenticationFilter.java,
│   │   │   │   │   │   JwtUtil.java, CustomUserDetails.java
│   │   │   │   │   ├── audit/AuditLog.java, AuditLogRepository.java, AuditLogAspect.java,
│   │   │   │   │   │   AuditLogService.java, AuditLogController.java
│   │   │   │   │   ├── scheduler/ScheduledTaskConfig.java, ContractExpiryJob.java,
│   │   │   │   │   │   LeaveBalanceResetJob.java
│   │   │   │   │   ├── config/CacheConfig.java, CorsConfig.java, SwaggerConfig.java, AsyncConfig.java
│   │   │   │   │   ├── exception/GlobalExceptionHandler.java, BusinessException.java,
│   │   │   │   │   │   ResourceNotFoundException.java
│   │   │   │   │   └── util/AesEncryptConverter.java, ExcelUtils.java, DateUtils.java
│   │   │   │   ├── user/
│   │   │   │   │   ├── entity/User.java, Role.java (enum), Department.java,
│   │   │   │   │   │   Position.java, RefreshToken.java
│   │   │   │   │   ├── repository/UserRepository.java, RefreshTokenRepository.java
│   │   │   │   │   ├── service/UserService.java, UserServiceImpl.java,
│   │   │   │   │   │   AuthService.java, AuthServiceImpl.java
│   │   │   │   │   ├── controller/UserController.java, AuthController.java
│   │   │   │   │   └── dto/UserCreateRequest.java, UserResponse.java, UserMapper.java,
│   │   │   │   │       LoginRequest.java, LoginResponse.java, RefreshTokenRequest.java
│   │   │   │   ├── contract/
│   │   │   │   │   ├── entity/LaborContract.java, ContractType.java (enum)
│   │   │   │   │   ├── repository/ service/ controller/ dto/
│   │   │   │   ├── attendance/
│   │   │   │   │   ├── entity/AttendanceRecord.java, IpWhitelist.java, MonthlyTimesheet.java
│   │   │   │   │   ├── repository/ service/ controller/ dto/
│   │   │   │   ├── leave/
│   │   │   │   │   ├── entity/LeaveRequest.java, LeaveType.java (enum), LeaveBalance.java,
│   │   │   │   │   │   RequestStatus.java (enum)
│   │   │   │   │   ├── repository/ service/ controller/ dto/
│   │   │   │   ├── payroll/
│   │   │   │   │   ├── entity/Payroll.java, Payslip.java, Allowance.java,
│   │   │   │   │   │   EmployeeAllowance.java, OvertimeRecord.java, InsuranceConfig.java
│   │   │   │   │   ├── repository/
│   │   │   │   │   ├── service/PayrollService.java, PayrollServiceImpl.java,
│   │   │   │   │   │   TaxCalculator.java, InsuranceCalculator.java
│   │   │   │   │   ├── controller/ dto/
│   │   │   │   ├── workflow/
│   │   │   │   │   ├── entity/ApprovalWorkflowTemplate.java, ApprovalWorkflowStep.java,
│   │   │   │   │   │   ApprovalPipeline.java, ApprovalPipelineStep.java
│   │   │   │   │   ├── repository/ service/ controller/ dto/
│   │   │   │   ├── notification/
│   │   │   │   │   ├── entity/Notification.java
│   │   │   │   │   ├── repository/
│   │   │   │   │   ├── service/NotificationService.java, NotificationServiceImpl.java, EmailService.java
│   │   │   │   │   ├── controller/ dto/
│   │   │   │   ├── document/
│   │   │   │   │   ├── entity/EmployeeDocument.java
│   │   │   │   │   ├── repository/
│   │   │   │   │   ├── service/DocumentService.java, FileStorageService.java
│   │   │   │   │   ├── controller/ dto/
│   │   │   │   ├── dashboard/
│   │   │   │   │   ├── service/DashboardService.java
│   │   │   │   │   ├── controller/DashboardController.java
│   │   │   │   │   └── dto/HrDashboardResponse.java, PayrollDashboardResponse.java, LeaveDashboardResponse.java
│   │   │   │   ├── dataimport/
│   │   │   │   │   ├── service/EmployeeImportService.java
│   │   │   │   │   ├── controller/ImportController.java
│   │   │   │   │   └── dto/ImportResultResponse.java
│   │   │   │   └── report/
│   │   │   │       ├── service/ReportService.java
│   │   │   │       ├── controller/ReportController.java
│   │   │   │       └── dto/
│   │   │   └── resources/
│   │   │       ├── application.yml, application-dev.yml, application-prod.yml, application-test.yml
│   │   │       ├── db/schema.sql, data.sql
│   │   │       └── templates/email/, excel/
│   │   └── test/java/com/hrms/
│   │       ├── user/ payroll/ leave/ workflow/ common/
│   └── uploads/                            # Local file storage (gitignored)
│
├── frontend/
│   ├── package.json, angular.json, tsconfig.json, Dockerfile, .env.example
│   ├── src/
│   │   ├── main.ts
│   │   ├── app/
│   │   │   ├── app.component.ts, app.config.ts, app.routes.ts
│   │   │   ├── shared/
│   │   │   │   ├── components/data-table/, page-header/, status-badge/, confirm-modal/
│   │   │   │   ├── directives/
│   │   │   │   ├── pipes/currency-vnd.pipe.ts, date-vn.pipe.ts
│   │   │   │   ├── models/api-response.model.ts, common.model.ts
│   │   │   │   └── utils/date.utils.ts, currency.utils.ts, validators.ts
│   │   │   ├── core/
│   │   │   │   ├── services/auth.service.ts, notification.service.ts
│   │   │   │   ├── interceptors/jwt.interceptor.ts, error.interceptor.ts
│   │   │   │   └── guards/userRole.guard.ts, auth.guard.ts
│   │   │   ├── layouts/admin-layout/, employee-layout/
│   │   │   ├── features/
│   │   │   │   ├── auth/          (components/ services/ models/ auth.routes.ts)
│   │   │   │   ├── user/           (components/ services/ models/ user.routes.ts)
│   │   │   │   ├── contract/      (components/ services/ models/ contract.routes.ts)
│   │   │   │   ├── attendance/    (components/ services/ models/ attendance.routes.ts)
│   │   │   │   ├── leave/         (components/ services/ models/ leave.routes.ts)
│   │   │   │   ├── payroll/       (components/ services/ models/ payroll.routes.ts)
│   │   │   │   ├── notification/  (components/ services/ models/ notification.routes.ts)
│   │   │   │   ├── audit-log/     (components/ services/ models/ audit-log.routes.ts)
│   │   │   │   ├── document/      (components/ services/ models/ document.routes.ts)
│   │   │   │   ├── dashboard/     (components/ services/ models/ dashboard.routes.ts)
│   │   │   │   ├── workflow/      (components/ services/ models/ workflow.routes.ts)
│   │   │   │   ├── import/        (components/ services/ models/ import.routes.ts)
│   │   │   │   ├── report/        (components/ services/ models/ report.routes.ts)
│   │   │   │   └── settings/      (components/ services/ models/ settings.routes.ts)
│   │   ├── environments/environment.ts, environment.prod.ts
│   │   ├── styles.scss
│   │   └── index.html
│   └── public/favicon.ico
│
└── nginx/
    └── nginx.conf

```

### FR → Structure Mapping

| FR Range | Module | Backend Package | Frontend Feature |
|----------|--------|----------------|-----------------|
| FR-1→3 | Auth & RBAC | `user/` (AuthService, AuthController) + `common/security/` | `features/auth/` |
| FR-7→9 | User/Employee | `user/` (UserService, UserController) | `features/user/` |
| FR-10→11 | Contract | `contract/` | `features/contract/` |
| FR-12→15 | Attendance | `attendance/` | `features/attendance/` |
| FR-16→18 | Leave | `leave/` | `features/leave/` |
| FR-19→24 | Payroll | `payroll/` | `features/payroll/` |
| FR-25→26 | Notifications | `notification/` | `features/notification/` |
| FR-27→29 | Audit Log | `common/audit/` | `features/audit-log/` |
| FR-30→31 | Documents | `document/` | `features/document/` |
| FR-32→34 | Dashboard | `dashboard/` | `features/dashboard/` |
| FR-35→37 | Workflow | `workflow/` | `features/workflow/` |
| FR-38 | Import | `dataimport/` | `features/import/` |
| FR-39→41 | Reports | `report/` | `features/report/` |

### Cross-Cutting Concerns Mapping

| Concern | Location |
|---------|----------|
| RBAC | `common/security/SecurityConfig.java` + `@PreAuthorize` on controllers |
| Audit logging | `common/audit/AuditLogAspect.java` — auto-intercepts all `@Service` CUD methods. `AuditLogController` for FR-28 viewing. |
| Scheduled tasks | `common/scheduler/` — `ContractExpiryJob` (FR-11 daily), `LeaveBalanceResetJob` (FR-18 annual) |
| Soft delete | `BaseEntity.active` + Hibernate `@SQLRestriction` clause |
| Notifications | `notification/service/` — listens to Spring `ApplicationEvent` from other modules |
| Encryption | `common/util/AesEncryptConverter.java` — `@Convert` on entity fields |

### Architectural Boundaries

**Allowed module dependencies:**
- `common` ← all modules (shared infrastructure)
- `leave` → `workflow` (leave request triggers approval pipeline)
- `payroll` → `attendance`, `leave` (data aggregation for payroll calculation)
- `dashboard` → `user`, `payroll`, `leave`, `contract` (read-only aggregation)
- `report` → `user`, `attendance`, `payroll` (read-only)
- `notification` ← event listeners only (no direct imports — event-driven decoupling)
- `dataimport` → `user` (bulk create users/employees)

**Forbidden dependencies:**
- No module imports entities/repositories from another module directly — use service interfaces
- `dashboard` and `report` are read-only consumers — they never write data to other modules
- `common/` never imports from any business module

### Data Flow

```
[Angular SPA] → HTTP/JSON → [Nginx] → reverse proxy → [Spring Boot API]
                                                          ↓
                                                   [JwtAuthFilter]
                                                          ↓
                                                   [Controller → Service → Repository]
                                                          ↓                    ↓
                                                   [AuditLogAspect]      [MySQL 8.4]
                                                          ↓
                                                   [ApplicationEvent]
                                                          ↓
                                                   [NotificationService]
                                                      ↓         ↓
                                               [Email/SMTP]  [In-app DB]
```

## Architecture Validation Results

### Coherence Validation ✅

**Decision Compatibility:** All technology choices verified compatible — Spring Boot 4.0.x runs on Java 21, Hibernate integrates with MySQL 8.4, Angular 17 communicates via REST/JSON with Spring Boot. No version conflicts.

**Pattern Consistency:** Naming conventions flow correctly across layers: snake_case (DB) → camelCase (Java fields, JSON) → kebab-case (Angular component files) → PascalCase (Angular component classes). Jackson ObjectMapper handles mapping automatically. ApiResponse<T> { code, message, data } used everywhere — no format inconsistency.

**Structure Alignment:** Package structure mirrors feature modules 1:1. Each module follows entity/repository/service/controller/dto pattern. Cross-cutting concerns isolated in common/. No circular dependencies detected.

### Requirements Coverage ✅

**All 38 FRs mapped to architectural components:**
- FR-1→3 (Auth): user/ (AuthService, AuthController) + common/security/
- FR-7→9 (User/Employee): user/ (UserService, UserController)
- FR-10→11 (Contract): contract/ + common/scheduler/ContractExpiryJob
- FR-12→15 (Attendance): attendance/
- FR-16→18 (Leave): leave/ + workflow/ + common/scheduler/LeaveBalanceResetJob
- FR-19→24 (Payroll): payroll/ (TaxCalculator, InsuranceCalculator)
- FR-25→26 (Notifications): notification/ (event-driven via ApplicationEvent)
- FR-27→29 (Audit Log): common/audit/ (AuditLogAspect + AuditLogController + AuditLogService)
- FR-30→31 (Documents): document/ + FileStorageService
- FR-32→34 (Dashboard): dashboard/
- FR-35→37 (Workflow): workflow/ (template + pipeline + steps)
- FR-38 (Import): dataimport/
- FR-39→41 (Reports): report/

**NFR Coverage:**
- Security: bcrypt(12) ✓, JWT ✓, AES-256 AttributeConverter ✓, HTTPS (nginx) ✓, file whitelist ✓
- Performance: Caffeine cache ✓, DB indexes ✓, payroll 30s budget ✓, Pageable pagination ✓
- Deployment: Docker Compose ✓, Hibernate DDL ✓, profiles (dev/prod/test) ✓
- Compliance: configurable rates ✓, 3 contract types ✓, OT coefficients ✓, immutable audit ✓

### Gaps Found & Resolved

| # | Gap | Severity | Resolution |
|---|-----|----------|------------|
| 1 | No scheduled task component for contract expiry alerts (FR-11) and leave balance reset (FR-18) | Important | Added `common/scheduler/` with `ContractExpiryJob`, `LeaveBalanceResetJob`, `ScheduledTaskConfig` |
| 2 | Audit log needs API controller for viewing/filtering (FR-28) | Minor | Added `AuditLogController` + `AuditLogService` in `common/audit/` |

### Architecture Completeness Checklist

**Requirements Analysis**
- [x] Project context thoroughly analyzed
- [x] Scale and complexity assessed (medium, 500 nhân viên)
- [x] Technical constraints identified (stack, infrastructure, single-instance)
- [x] Cross-cutting concerns mapped (6 concerns + scheduled tasks)

**Architectural Decisions**
- [x] Critical decisions documented with versions (Spring Boot 4.0.x, Java 21, MySQL 8.4, Angular 17)
- [x] Technology stack fully specified (13 backend + 3 frontend dependencies + Angular built-ins)
- [x] Integration patterns defined (event-driven notifications, REST API)
- [x] Performance considerations addressed (Caffeine cache, DB indexes, pagination)

**Implementation Patterns**
- [x] Naming conventions established (DB, Java, TS, API — all layers)
- [x] Structure patterns defined (module structure, test location, file organization)
- [x] Communication patterns specified (Spring events, Angular HttpClient + Signals, HttpInterceptor)
- [x] Process patterns documented (error handling, loading states, validation)

**Project Structure**
- [x] Complete directory structure defined (backend + frontend + docker + nginx)
- [x] Component boundaries established (module dependencies, forbidden imports)
- [x] Integration points mapped (data flow diagram, event listeners)
- [x] Requirements to structure mapping complete (38 FRs → packages/features)

### Architecture Readiness Assessment

**Overall Status:** READY FOR IMPLEMENTATION

**Confidence Level:** High

**Key Strengths:**
- Unified ApiResponse<T> { code, message, data } eliminates API format inconsistency
- 9 clear enforcement rules for AI agents
- Complete FR → Structure mapping ensures nothing is missed during implementation
- Event-driven notification decouples business modules from notification logic
- Scheduled tasks handle time-based business rules (contract expiry, leave reset)

**Areas for Future Enhancement (V2+):**
- Redis distributed cache when scaling beyond single instance
- WebSocket/SSE for real-time notifications
- Cloud storage (S3) for documents
- CI/CD pipeline configuration
- API rate limiting

### Implementation Handoff

**AI Agent Guidelines:**
- Follow all architectural decisions exactly as documented
- Use implementation patterns consistently across all components
- Respect project structure and module boundaries
- Return ApiResponse<T> from every controller — no exceptions
- Extend BaseEntity for every business entity

**First Implementation Priority:**
1. Generate backend via Spring Initializr (Spring Boot 4.0.x, Java 21, Maven)
2. Generate frontend via `ng new hrms-frontend --style=scss --routing --strict --standalone` + install ng-zorro-antd
3. Set up Docker Compose (mysql + backend + nginx)
4. Implement BaseEntity + repository methods
5. Implement JWT auth + Spring Security filter chain
6. Build core modules: User (profile + auth) → Contract → Attendance → Leave → Payroll
7. Cross-cutting: AuditLogAspect, NotificationService, Scheduler jobs
8. Frontend: auth flow → layouts → feature modules
9. Dashboard + Reports + Import

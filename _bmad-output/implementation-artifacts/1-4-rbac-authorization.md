# Story 1.4: RBAC Authorization

Status: ready-for-dev

## Story

As an **Admin**,
I want role-based access control with 3 roles,
So that users only access data within their permission scope.

## Acceptance Criteria

1. **AC-1: EMPLOYEE bị 403 trên ADMIN-only endpoint**
   - Given user có role EMPLOYEE
   - When họ gọi bất kỳ endpoint `@PreAuthorize("hasRole('ADMIN')")` (ví dụ PUT `/api/v1/employees/{id}`)
   - Then nhận `{ "code": 403, "message": "Không có quyền truy cập" }`

2. **AC-2: MANAGER chỉ thấy nhân viên trong phòng ban mình**
   - Given user có role MANAGER thuộc Department A
   - When họ query danh sách nhân viên
   - Then chỉ thấy nhân viên của Department A
   - **Note:** AC này sẽ được enforce ở tầng service trong Story 2.x — Story 1.4 chỉ cung cấp infrastructure (`SecurityContextUtils`) để lấy `departmentId` của current user

3. **AC-3: CORS config hoạt động trong dev profile**
   - Given frontend tại `http://localhost:4200`
   - When gửi request với methods GET, POST, PUT, DELETE, PATCH
   - Then request được allow với `credentials: true`
   - And cấu hình đến từ `app.cors.allowed-origins` (default `http://localhost:4200`)

4. **AC-4: @PreAuthorize enforce role checks**
   - Given Spring Security filter chain đã configured
   - When endpoint có annotation `@PreAuthorize("hasRole('ADMIN')")`
   - Then chỉ user với role ADMIN mới truy cập được
   - And public endpoints chỉ là: `/api/v1/auth/login`, `/api/v1/auth/refresh`

5. **AC-5: Tất cả tests pass**
   - `RbacAuthorizationTest`: ADMIN pass, EMPLOYEE → 403, MANAGER → 403, anonymous → 401
   - `CorsConfigTest`: allowed origins, methods, credentials
   - Toàn bộ tests từ Story 1.2 và 1.3 vẫn pass (no regression)

## Dependency: Story 1.3 phải hoàn thành trước

Story 1.4 phụ thuộc vào các thành phần được tạo trong Story 1.3:
- `User.java` entity (với `UserRole` enum: ADMIN, MANAGER, EMPLOYEE)
- `UserRepository.java` (để tạo test data)
- `SecurityConfig.java` đã được cập nhật với `AuthenticationEntryPoint` + `AccessDeniedHandler`

**Không implement Story 1.4 trước khi Story 1.3 hoàn thành.**

## Tasks / Subtasks

- [ ] Task 1: Tạo SecurityContextUtils
  - [ ] 1.1: Tạo `com.hrms.common.security.SecurityContextUtils` — static utility class
  - [ ] 1.2: Method `getCurrentUserId()` → Long (nullable nếu anonymous)
  - [ ] 1.3: Method `getCurrentUserRole()` → String (nullable)
  - [ ] 1.4: Method `getCurrentUserDepartmentId()` → Long (nullable)
  - [ ] 1.5: Method `isAdmin()` → boolean
  - [ ] 1.6: Method `isManager()` → boolean
  - [ ] 1.7: Method `isEmployee()` → boolean
  - [ ] 1.8: Method `getCurrentUserDetails()` → Optional<CustomUserDetails>

- [ ] Task 2: Verify AccessDeniedHandler trong SecurityConfig (từ Story 1.3)
  - [ ] 2.1: Confirm SecurityConfig đã có `exceptionHandling()` với `accessDeniedHandler` trả `{ "code": 403, "message": "Không có quyền truy cập" }`
  - [ ] 2.2: Confirm `authenticationEntryPoint` trả `{ "code": 401, "message": "Chưa xác thực" }`
  - [ ] 2.3: Nếu Story 1.3 chưa thêm → thêm vào SecurityConfig (xem Dev Notes)

- [ ] Task 3: Verify CorsConfig hoạt động đúng
  - [ ] 3.1: Kiểm tra `CorsConfig.java` đã configured methods GET/POST/PUT/DELETE/PATCH và `allowCredentials: true`
  - [ ] 3.2: Verify `application.yml` có `app.cors.allowed-origins: ${CORS_ORIGINS:http://localhost:4200}`
  - [ ] 3.3: Verify `application-dev.yml` KHÔNG override cors (dùng default từ `.env`)

- [ ] Task 4: Viết tests
  - [ ] 4.1: `RbacAuthorizationTest` — dùng inner `@RestController` để test @PreAuthorize
  - [ ] 4.2: `SecurityContextUtilsTest` — unit test các static methods
  - [ ] 4.3: `CorsConfigTest` — verify CORS configuration

- [ ] Task 5: Run toàn bộ test suite
  - [ ] 5.1: `mvn test` — tất cả tests pass (bao gồm 15 tests từ Story 1.2 + tests từ Story 1.3)

## Dev Notes

### TRẠNG THÁI HIỆN TẠI — CÁI GÌ ĐÃ TỒN TẠI

| File | Package | Trạng thái | Hành động |
|------|---------|-----------|-----------|
| `SecurityConfig.java` | `common/security` | ⚠️ Sau Story 1.3: có exceptionHandling() | Verify/thêm nếu thiếu |
| `JwtAuthenticationFilter.java` | `common/security` | ✅ HOÀN CHỈNH | Không thay đổi |
| `CustomUserDetails.java` | `common/security` | ✅ HOÀN CHỈNH | Không thay đổi |
| `CorsConfig.java` | `common/config` | ✅ ĐÃ TỒN TẠI | Chỉ verify, không sửa |
| `GlobalExceptionHandler.java` | `common/exception` | ✅ Đã có @ExceptionHandler(AccessDeniedException) | Không thay đổi |
| `SecurityContextUtils.java` | `common/security` | ❌ CHƯA TỒN TẠI | Tạo mới |
| `RbacAuthorizationTest.java` | `test/common/security` | ❌ CHƯA TỒN TẠI | Tạo mới |
| `SecurityContextUtilsTest.java` | `test/common/security` | ❌ CHƯA TỒN TẠI | Tạo mới |

### CorsConfig — ĐÃ TỒN TẠI, KHÔNG THAY ĐỔI

`CorsConfig.java` đã được implement đúng trong Story 1.1:
```java
// Đã có tại: com.hrms.common.config.CorsConfig
// Cấu hình: List.of(allowedOrigins.split(","))
// Methods: GET, POST, PUT, DELETE, PATCH
// AllowCredentials: true
// application.yml: app.cors.allowed-origins: ${CORS_ORIGINS:http://localhost:4200}
```

Không cần sửa. Chỉ cần viết test verify.

### SecurityConfig — Verify exceptionHandling() từ Story 1.3

Story 1.3 (Task 7.1 & 7.2) đã thêm `exceptionHandling()`. Verify SecurityConfig trông như sau sau Story 1.3:

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    return http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(401);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"code\":401,\"message\":\"Chưa xác thực\",\"data\":null}");
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setStatus(403);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"code\":403,\"message\":\"Không có quyền truy cập\",\"data\":null}");
                })
            )
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers(
                            "/api/v1/auth/login",
                            "/api/v1/auth/refresh",
                            "/actuator/health",
                            "/swagger-ui/**",
                            "/v3/api-docs/**"
                    ).permitAll()
                    .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
}
```

**Nếu Story 1.3 chưa thêm** → thêm vào SecurityConfig (thêm `.exceptionHandling(...)` block như trên).

### SecurityContextUtils — Tạo mới

```java
// File: backend/src/main/java/com/hrms/common/security/SecurityContextUtils.java
package com.hrms.common.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class SecurityContextUtils {

    private SecurityContextUtils() {}

    public static Optional<CustomUserDetails> getCurrentUserDetails() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails details) {
            return Optional.of(details);
        }
        return Optional.empty();
    }

    public static Long getCurrentUserId() {
        return getCurrentUserDetails().map(CustomUserDetails::getUserId).orElse(null);
    }

    public static String getCurrentUserRole() {
        return getCurrentUserDetails().map(CustomUserDetails::getRole).orElse(null);
    }

    public static Long getCurrentUserDepartmentId() {
        return getCurrentUserDetails().map(CustomUserDetails::getDepartmentId).orElse(null);
    }

    public static boolean isAdmin() {
        return "ADMIN".equals(getCurrentUserRole());
    }

    public static boolean isManager() {
        return "MANAGER".equals(getCurrentUserRole());
    }

    public static boolean isEmployee() {
        return "EMPLOYEE".equals(getCurrentUserRole());
    }
}
```

**Cách dùng trong Service tương lai (ví dụ Story 2.3):**
```java
// EmployeeServiceImpl.java (Story 2.3)
public Page<EmployeeResponse> getEmployees(Pageable pageable) {
    if (SecurityContextUtils.isManager()) {
        Long departmentId = SecurityContextUtils.getCurrentUserDepartmentId();
        return employeeRepository.findByDepartmentId(departmentId, pageable)
                .map(employeeMapper::toResponse);
    }
    // ADMIN xem tất cả
    return employeeRepository.findAll(pageable).map(employeeMapper::toResponse);
}
```

### @PreAuthorize — Đã được bật bởi @EnableMethodSecurity

`SecurityConfig.java` đã có `@EnableMethodSecurity` từ Story 1.1. Không cần thêm gì.

**Pattern dùng trong controllers tương lai:**
```java
// Chỉ ADMIN
@PreAuthorize("hasRole('ADMIN')")
@PutMapping("/{id}")
public ResponseEntity<ApiResponse<EmployeeResponse>> updateEmployee(...) { ... }

// ADMIN hoặc MANAGER
@PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
@GetMapping
public ResponseEntity<ApiResponse<PageData<EmployeeResponse>>> getEmployees(...) { ... }

// Bất kỳ user đã auth (không cần annotation vì .anyRequest().authenticated() đã cover)
@GetMapping("/profile")
public ResponseEntity<ApiResponse<UserResponse>> getProfile() { ... }
```

### JwtAuthenticationFilter — Đảm bảo departmentId được set

Hiện tại `JwtAuthenticationFilter.java` set `departmentId = null`:
```java
var userDetails = new CustomUserDetails(userId, role, null, ...);
```

**Vấn đề:** MANAGER cần `departmentId` để filter department scope. Hiện tại, departmentId không có trong JWT payload (theo ARCH-14: payload chỉ có `sub`, `role`, `iat`, `exp`).

**Giải pháp cho Story 1.4:** `departmentId` sẽ được load từ DB khi cần (trong service layer) dùng `SecurityContextUtils.getCurrentUserId()`, không phải từ JWT. Điều này đúng với architecture decision: JWT payload nhỏ gọn, data được query từ DB khi cần.

**Không thay đổi JwtAuthenticationFilter.** `departmentId = null` trong CustomUserDetails là đúng — Story 2.x sẽ query DB trực tiếp dựa trên `userId`.

### RbacAuthorizationTest — Dùng inner @RestController

Test này dùng pattern inner controller để test @PreAuthorize mà không cần controller thật:

```java
// File: backend/src/test/java/com/hrms/common/security/RbacAuthorizationTest.java
package com.hrms.common.security;

import com.hrms.common.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class RbacAuthorizationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JwtUtil jwtUtil;

    // --- Test: Anonymous → 401 ---
    @Test
    void anonymousRequest_returnsUnauthorized() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/v1/test/admin-only", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).contains("\"code\":401");
        assertThat(response.getBody()).contains("Chưa xác thực");
    }

    // --- Test: ADMIN → 200 on admin endpoint ---
    @Test
    void adminUser_canAccessAdminEndpoint() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtUtil.generateAccessToken(1L, "ADMIN"));

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/v1/test/admin-only",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    // --- Test: EMPLOYEE → 403 on admin endpoint ---
    @Test
    void employeeUser_cannotAccessAdminEndpoint() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtUtil.generateAccessToken(2L, "EMPLOYEE"));

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/v1/test/admin-only",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).contains("\"code\":403");
        assertThat(response.getBody()).contains("Không có quyền truy cập");
    }

    // --- Test: MANAGER → 403 on admin endpoint ---
    @Test
    void managerUser_cannotAccessAdminEndpoint() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtUtil.generateAccessToken(3L, "MANAGER"));

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/v1/test/admin-only",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).contains("\"code\":403");
    }

    // --- Test: MANAGER or ADMIN → 200 on manager-or-admin endpoint ---
    @Test
    void managerUser_canAccessManagerOrAdminEndpoint() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtUtil.generateAccessToken(3L, "MANAGER"));

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/v1/test/manager-or-admin",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    // --- Test: EMPLOYEE → 403 on manager-or-admin endpoint ---
    @Test
    void employeeUser_cannotAccessManagerOrAdminEndpoint() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtUtil.generateAccessToken(2L, "EMPLOYEE"));

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/v1/test/manager-or-admin",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    // --- Test: Authenticated user → 200 on authenticated-only endpoint ---
    @Test
    void authenticatedUser_canAccessAuthenticatedEndpoint() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtUtil.generateAccessToken(2L, "EMPLOYEE"));

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/v1/test/authenticated",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
```

**QUAN TRỌNG:** Test trên cần một `TestSecurityController` để register các endpoint `/api/v1/test/*`. Tạo controller này trong thư mục test:

```java
// File: backend/src/test/java/com/hrms/common/security/TestSecurityController.java
package com.hrms.common.security;

import com.hrms.common.dto.ApiResponse;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/test")
@Profile("test")
class TestSecurityController {

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin-only")
    public ResponseEntity<ApiResponse<String>> adminOnly() {
        return ResponseEntity.ok(ApiResponse.success("Admin access granted"));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @GetMapping("/manager-or-admin")
    public ResponseEntity<ApiResponse<String>> managerOrAdmin() {
        return ResponseEntity.ok(ApiResponse.success("Manager/Admin access granted"));
    }

    @GetMapping("/authenticated")
    public ResponseEntity<ApiResponse<String>> authenticated() {
        return ResponseEntity.ok(ApiResponse.success("Authenticated access granted"));
    }
}
```

**Lý do dùng `@Profile("test")`:** Controller này chỉ register trong test profile, không expose production.

### SecurityContextUtilsTest — Unit test

```java
// File: backend/src/test/java/com/hrms/common/security/SecurityContextUtilsTest.java
package com.hrms.common.security;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SecurityContextUtilsTest {

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    private void setAuthentication(Long userId, String role, Long departmentId) {
        var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));
        var userDetails = new CustomUserDetails(userId, role, departmentId,
                userId.toString(), "", authorities);
        var auth = new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void getCurrentUserId_returnsUserId() {
        setAuthentication(1L, "ADMIN", null);
        assertThat(SecurityContextUtils.getCurrentUserId()).isEqualTo(1L);
    }

    @Test
    void getCurrentUserRole_returnsRole() {
        setAuthentication(1L, "ADMIN", null);
        assertThat(SecurityContextUtils.getCurrentUserRole()).isEqualTo("ADMIN");
    }

    @Test
    void getCurrentUserDepartmentId_returnsNull_whenNotSet() {
        setAuthentication(1L, "MANAGER", null);
        assertThat(SecurityContextUtils.getCurrentUserDepartmentId()).isNull();
    }

    @Test
    void getCurrentUserDepartmentId_returnsDepartmentId_whenSet() {
        setAuthentication(1L, "MANAGER", 5L);
        assertThat(SecurityContextUtils.getCurrentUserDepartmentId()).isEqualTo(5L);
    }

    @Test
    void isAdmin_returnsTrue_forAdminRole() {
        setAuthentication(1L, "ADMIN", null);
        assertThat(SecurityContextUtils.isAdmin()).isTrue();
        assertThat(SecurityContextUtils.isManager()).isFalse();
        assertThat(SecurityContextUtils.isEmployee()).isFalse();
    }

    @Test
    void isManager_returnsTrue_forManagerRole() {
        setAuthentication(2L, "MANAGER", 1L);
        assertThat(SecurityContextUtils.isManager()).isTrue();
        assertThat(SecurityContextUtils.isAdmin()).isFalse();
    }

    @Test
    void getCurrentUserDetails_returnsEmpty_whenAnonymous() {
        SecurityContextHolder.clearContext();
        assertThat(SecurityContextUtils.getCurrentUserDetails()).isEmpty();
        assertThat(SecurityContextUtils.getCurrentUserId()).isNull();
        assertThat(SecurityContextUtils.isAdmin()).isFalse();
    }
}
```

### CorsConfigTest — Verify CORS configuration

```java
// File: backend/src/test/java/com/hrms/common/config/CorsConfigTest.java
package com.hrms.common.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class CorsConfigTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void corsHeaders_returnedForAllowedOrigin() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Origin", "http://localhost:4200");

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/v1/auth/login",
                HttpMethod.OPTIONS,
                new HttpEntity<>(headers),
                String.class);

        // CORS headers phải có
        assertThat(response.getHeaders().get("Access-Control-Allow-Origin"))
                .containsExactly("http://localhost:4200");
        assertThat(response.getHeaders().getFirst("Access-Control-Allow-Credentials"))
                .isEqualTo("true");
    }

    @Test
    void corsAllowedMethods_includeAllRequired() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Origin", "http://localhost:4200");
        headers.set("Access-Control-Request-Method", "PUT");

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/v1/auth/login",
                HttpMethod.OPTIONS,
                new HttpEntity<>(headers),
                String.class);

        String allowedMethods = response.getHeaders().getFirst("Access-Control-Allow-Methods");
        assertThat(allowedMethods).contains("GET", "POST", "PUT", "DELETE", "PATCH");
    }
}
```

### Package Structure — File Locations

```
backend/src/main/java/com/hrms/
└── common/
    └── security/
        ├── SecurityConfig.java          ← VERIFY/SỬA (thêm exceptionHandling nếu 1.3 chưa làm)
        ├── SecurityContextUtils.java    ← TẠO MỚI
        ├── JwtAuthenticationFilter.java ← KHÔNG THAY ĐỔI
        └── CustomUserDetails.java       ← KHÔNG THAY ĐỔI

backend/src/test/java/com/hrms/
└── common/
    ├── security/
    │   ├── TestSecurityController.java     ← TẠO MỚI (test profile only)
    │   ├── RbacAuthorizationTest.java      ← TẠO MỚI
    │   └── SecurityContextUtilsTest.java   ← TẠO MỚI
    └── config/
        └── CorsConfigTest.java             ← TẠO MỚI
```

### ApiResponse.java — Verify static factory methods

Tests dùng `ApiResponse.success(...)`. Kiểm tra `ApiResponse.java` đã có:
```java
// Cần có trong ApiResponse.java:
public static <T> ApiResponse<T> success(T data) {
    return new ApiResponse<>(200, "Thành công", data);
}
public static <T> ApiResponse<T> error(int code, String message) {
    return new ApiResponse<>(code, message, null);
}
```
Nếu chưa có static factory methods → thêm vào `ApiResponse.java`.

### Testing Strategy

**CRITICAL từ Story 1.2:** Spring Boot 4.0.6 — dùng `@SpringBootTest @ActiveProfiles("test")` cho TẤT CẢ integration tests.

- `RbacAuthorizationTest` — integration test với `RANDOM_PORT`, tạo JWT tokens thực từ `JwtUtil`
- `SecurityContextUtilsTest` — pure unit test, không cần Spring context, set SecurityContextHolder thủ công
- `CorsConfigTest` — integration test với `RANDOM_PORT`, gửi OPTIONS request

**application-test.yml** đã có `spring.sql.init.mode: never` → `data.sql` KHÔNG chạy. Tests tự generate JWT tokens mà không cần user trong DB (JWT validation chỉ cần secret key, không cần user tồn tại).

### Scope Boundary — Story này KHÔNG làm

- KHÔNG implement department-scope filtering trong service layer (Story 2.3)
- KHÔNG implement User management CRUD (Story 1.5)
- KHÔNG implement login frontend (Story 1.7)
- KHÔNG thêm role vào database (đã trong Story 1.3 qua `UserRole` enum)
- KHÔNG implement audit logging (Story 8.x)
- KHÔNG thêm `departmentId` vào JWT payload (ARCH-14 explicit: chỉ `sub`, `role`, `iat`, `exp`)

### Sử dụng SecurityContextUtils trong các Stories tương lai

**Pattern cho MANAGER scope filtering (Story 2.3):**
```java
// Trong EmployeeServiceImpl.getEmployees():
if (SecurityContextUtils.isManager()) {
    // Query UserRepository để lấy departmentId của manager
    Long managerId = SecurityContextUtils.getCurrentUserId();
    User manager = userRepository.findById(managerId)
        .orElseThrow(() -> new BusinessException(401, "User không tồn tại"));
    return employeeRepository.findByDepartmentId(manager.getDepartmentId(), pageable);
}
// ADMIN: return all
return employeeRepository.findAll(pageable);
```

**Note:** `departmentId` của MANAGER được lưu trong `User.departmentId` (hoặc `User.department.id`), cần thiết kế khi tạo `User` entity trong Story 1.3.

### Architecture Compliance Checklist

- ✅ ARCH-18: @PreAuthorize("hasRole('ADMIN')") + @EnableMethodSecurity
- ✅ ARCH-20: CORS dev allow localhost:4200, methods GET/POST/PUT/DELETE/PATCH, credentials:true
- ✅ NFR-3: Tất cả API yêu cầu authentication (trừ login và refresh endpoint)
- ✅ FR-2: RBAC — 3 vai trò (Admin, Manager, Employee) với infrastructure để enforce
- ✅ ARCH-22: Unified ApiResponse<T> {code, message, data} cho 403 responses
- ✅ ARCH-26: AccessDeniedException → GlobalExceptionHandler (fallback) + SecurityConfig accessDeniedHandler (primary)

### Learnings từ Story 1.2 & 1.3

1. **`@SpringBootTest @ActiveProfiles("test")`** cho TẤT CẢ integration tests — Spring Boot 4.0.6 không có `@WebMvcTest`.
2. **`org.springframework.transaction.annotation.Transactional`** (không phải `jakarta.transaction.Transactional`).
3. **Test profile**: `data.sql` không chạy, H2 in-memory. JWT tests không cần user trong DB.
4. **`@Profile("test")`** trên `TestSecurityController` quan trọng — không muốn expose test endpoints trong production.
5. **SecurityContextHolder**: test unit với `SecurityContextHolder.clearContext()` trong `@AfterEach` để tránh pollution giữa tests.

### References

- [Source: epics.md — Story 1.4 acceptance criteria]
- [Source: architecture.md — ARCH-18, ARCH-20, NFR-3, FR-2]
- [Source: Story 1.3 Dev Notes — SecurityConfig exceptionHandling, testing patterns]
- [Source: Story 1.2 Dev Notes — Spring Boot 4 test patterns]

## Dev Agent Record

### Agent Model Used

(để dev agent điền khi implement)

### Completion Notes List

(để dev agent điền khi implement)

### File List

(để dev agent điền khi implement)

### Change Log

- 2026-06-23: Story 1.4 tạo bởi create-story — phân tích SecurityConfig, CustomUserDetails, CorsConfig hiện tại. Thiết kế SecurityContextUtils, RbacAuthorizationTest với inner TestController strategy, CorsConfigTest.

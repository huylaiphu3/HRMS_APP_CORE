# Story 1.3: JWT Authentication — Login & Refresh Token

Status: ready-for-dev

## Story

As a **User**,
I want to log in with username/password and receive JWT tokens,
So that I can securely access the system with automatic session refresh.

## Acceptance Criteria

1. **AC-1: Login thành công trả tokens**
   - Given user hợp lệ POST `/api/v1/auth/login` với `{ username, password }`
   - Then nhận `ApiResponse<LoginResponse>` code 200: `accessToken` (JWT HMAC-SHA256, 30min), `refreshToken` (UUID, 7 ngày)
   - And JWT payload: `sub` (userId), `role`, `iat`, `exp`
   - And password verified bằng bcrypt (cost 12)
   - And response: `{ "code": 200, "message": "Thành công", "data": { "accessToken": "...", "refreshToken": "..." } }`

2. **AC-2: Khóa tài khoản sau 5 lần thất bại**
   - Given user thất bại 5 lần liên tiếp
   - When thử lần 6 (dù đúng credentials)
   - Then nhận `{ "code": 403, "message": "Tài khoản đã bị khoá. Thử lại sau 15 phút" }`
   - And tài khoản auto-unlock sau 15 phút

3. **AC-3: Refresh token rotation**
   - Given refresh token hợp lệ, chưa revoked
   - When POST `/api/v1/auth/refresh` với `{ refreshToken }`
   - Then nhận accessToken mới + refreshToken mới
   - And refresh token cũ bị mark `revoked = true` trong bảng `refresh_tokens`

4. **AC-4: Refresh token hết hạn/revoked**
   - Given refresh token hết hạn hoặc đã revoked
   - When POST `/api/v1/auth/refresh`
   - Then nhận `{ "code": 401, "message": "Phiên đăng nhập hết hạn" }`

5. **AC-5: Unauthorized cho endpoint bảo vệ**
   - Given request không có Bearer token hợp lệ đến endpoint bất kỳ (trừ login/refresh)
   - Then nhận `{ "code": 401, "message": "Chưa xác thực" }`

6. **AC-6: GlobalExceptionHandler không lộ stacktrace**
   - Given bất kỳ exception nào xảy ra trong controller
   - Then trả ApiResponse với code và message tiếng Việt
   - And không bao giờ lộ stack trace trong response

7. **AC-7: Admin seed hoạt động**
   - Given application khởi động dev profile
   - Then admin account `admin@hrms.vn` / `Admin@123` tồn tại trong DB
   - And có thể đăng nhập thành công bằng credentials này

8. **AC-8: Tất cả tests pass**
   - `AuthControllerIntegrationTest`: login thành công, sai password, account locked, refresh flow
   - `AuthServiceTest`: unit tests cho login attempt tracking, lockout logic
   - Toàn bộ 15 tests từ Story 1.2 vẫn pass (no regression)

## Tasks / Subtasks

- [ ] Task 1: Tạo User entity và repository
  - [ ] 1.1: Tạo `com.hrms.user.entity.User` extends BaseEntity — email, passwordHash, fullName, role (enum), status (enum), failedLoginAttempts, lockedUntil
  - [ ] 1.2: Tạo enum `UserRole` (ADMIN, MANAGER, EMPLOYEE) trong `com.hrms.user.entity`
  - [ ] 1.3: Tạo enum `UserStatus` (ACTIVE, INACTIVE, LOCKED) trong `com.hrms.user.entity`
  - [ ] 1.4: Tạo `com.hrms.user.repository.UserRepository` extends JpaRepository với `findByEmail(String email)`

- [ ] Task 2: Tạo RefreshToken entity và repository
  - [ ] 2.1: Tạo `com.hrms.auth.entity.RefreshToken` (KHÔNG extends BaseEntity — không cần audit fields) — id, token (UUID string), userId (Long), expiresAt (LocalDateTime), revoked (Boolean default false)
  - [ ] 2.2: Tạo `com.hrms.auth.repository.RefreshTokenRepository` với `findByTokenAndRevokedFalse(String token)` và `revokeByUserId(Long userId)` (for logout/disable later)

- [ ] Task 3: Tạo Auth DTOs
  - [ ] 3.1: `com.hrms.auth.dto.LoginRequest` — `username` (String, @NotBlank), `password` (String, @NotBlank)
  - [ ] 3.2: `com.hrms.auth.dto.LoginResponse` — `accessToken` (String), `refreshToken` (String)
  - [ ] 3.3: `com.hrms.auth.dto.RefreshTokenRequest` — `refreshToken` (String, @NotBlank)

- [ ] Task 4: Tạo AuthService và AuthServiceImpl
  - [ ] 4.1: Tạo `com.hrms.auth.service.AuthService` interface với `login(LoginRequest)` và `refresh(String refreshToken)`
  - [ ] 4.2: Implement `com.hrms.auth.service.impl.AuthServiceImpl` — đầy đủ lockout logic, UUID refresh token

- [ ] Task 5: Tạo AuthController
  - [ ] 5.1: `com.hrms.auth.controller.AuthController` — POST `/api/v1/auth/login` và POST `/api/v1/auth/refresh`

- [ ] Task 6: Sửa JwtUtil — xóa generateRefreshToken()
  - [ ] 6.1: XÓA method `generateRefreshToken(Long userId)` khỏi `JwtUtil.java` — method này tạo JWT refresh token, SAI theo ARCH-15 (refresh token phải là UUID trong DB)

- [ ] Task 7: Cập nhật SecurityConfig — thêm AuthenticationEntryPoint
  - [ ] 7.1: Thêm custom `AuthenticationEntryPoint` trả `{ "code": 401, "message": "Chưa xác thực" }` theo format ApiResponse
  - [ ] 7.2: Wire vào SecurityConfig qua `.exceptionHandling()`

- [ ] Task 8: Uncomment data.sql admin seed
  - [ ] 8.1: Uncomment INSERT trong `data.sql` để tạo admin account khi khởi động

- [ ] Task 9: Viết tests
  - [ ] 9.1: `AuthControllerIntegrationTest` — @SpringBootTest @ActiveProfiles("test") — login thành công, sai password, account locked, refresh thành công, refresh revoked
  - [ ] 9.2: `AuthServiceTest` — unit test lockout logic

- [ ] Task 10: Run toàn bộ test suite
  - [ ] 10.1: `mvn test` — tất cả tests pass (bao gồm 15 tests từ Story 1.2)

## Dev Notes

### TRẠNG THÁI HIỆN TẠI — CÁI GÌ ĐÃ TỒN TẠI

| File | Package | Trạng thái | Hành động |
|------|---------|-----------|-----------|
| `JwtUtil.java` | `common/util` | ✅ TỒN TẠI — nhưng có method PHẢI XÓA | XÓA `generateRefreshToken()` |
| `JwtAuthenticationFilter.java` | `common/security` | ✅ HOÀN CHỈNH | Không thay đổi |
| `SecurityConfig.java` | `common/security` | ⚠️ CẦN SỬA — thiếu AuthenticationEntryPoint | Thêm exceptionHandling() |
| `CustomUserDetails.java` | `common/security` | ✅ HOÀN CHỈNH | Không thay đổi |
| `GlobalExceptionHandler.java` | `common/exception` | ✅ HOÀN CHỈNH | Không thay đổi |
| `ApiResponse.java` | `common/dto` | ✅ HOÀN CHỈNH | Không thay đổi |
| `BusinessException.java` | `common/exception` | ✅ HOÀN CHỈNH | Không thay đổi |
| `data.sql` | `resources` | ⚠️ INSERT bị comment out | Uncomment |
| `User.java` | `user/entity` | ❌ CHƯA TỒN TẠI | Tạo mới |
| `RefreshToken.java` | `auth/entity` | ❌ CHƯA TỒN TẠI | Tạo mới |
| `AuthController.java` | `auth/controller` | ❌ CHƯA TỒN TẠI | Tạo mới |
| `AuthService(Impl).java` | `auth/service(/impl)` | ❌ CHƯA TỒN TẠI | Tạo mới |

### CẢNH BÁO QUAN TRỌNG: JwtUtil.generateRefreshToken() PHẢI BỊ XÓA

**Vấn đề:** `JwtUtil.java` hiện có method `generateRefreshToken(Long userId)` tạo ra JWT token.  
**Tại sao sai:** ARCH-15 yêu cầu refresh token là UUID lưu trong bảng DB `refresh_tokens` để có thể revoke. JWT stateless = không thể revoke trước khi hết hạn.  
**Fix:** XÓA method này. Thay bằng `UUID.randomUUID().toString()` trực tiếp trong AuthServiceImpl.

```java
// ĐÚNG — trong AuthServiceImpl:
String refreshTokenValue = UUID.randomUUID().toString();
RefreshToken refreshToken = new RefreshToken();
refreshToken.setToken(refreshTokenValue);
refreshToken.setUserId(user.getId());
refreshToken.setExpiresAt(LocalDateTime.now().plusDays(7));
refreshToken.setRevoked(false);
refreshTokenRepository.save(refreshToken);
```

### User Entity — Thiết kế chi tiết

```java
@Entity
@Table(name = "users")
public class User extends BaseEntity {
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private UserStatus status = UserStatus.ACTIVE;

    @Column(name = "failed_login_attempts")
    private int failedLoginAttempts = 0;

    @Column(name = "locked_until")
    private LocalDateTime lockedUntil;

    @Column(name = "force_password_change")
    private boolean forcePasswordChange = false;
}
```

**Lưu ý:** `User` extends `BaseEntity` → `active` field được kế thừa. `active = false` = soft-deleted/disabled user (Story 1.5). `status = LOCKED` = bị khóa do sai password.

### RefreshToken Entity — Thiết kế chi tiết

```java
@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {  // KHÔNG extends BaseEntity
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token", nullable = false, unique = true)
    private String token;  // UUID string

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "revoked", nullable = false)
    private boolean revoked = false;
}
```

**RefreshToken KHÔNG extends BaseEntity** — không cần audit fields (created_by, updated_by), không cần soft delete (`active`), không có SQLRestriction phức tạp.

### AuthServiceImpl — Login Flow Chi Tiết

```java
public LoginResponse login(LoginRequest request) {
    User user = userRepository.findByEmail(request.getUsername())
        .orElseThrow(() -> new BusinessException(401, "Email hoặc mật khẩu không đúng"));

    // 1. Check khóa tài khoản
    if (user.getLockedUntil() != null && LocalDateTime.now().isBefore(user.getLockedUntil())) {
        throw new BusinessException(403, "Tài khoản đã bị khoá. Thử lại sau 15 phút");
    }

    // 2. Auto-unlock nếu hết thời gian khóa
    if (user.getLockedUntil() != null && LocalDateTime.now().isAfter(user.getLockedUntil())) {
        user.setLockedUntil(null);
        user.setFailedLoginAttempts(0);
        user.setStatus(UserStatus.ACTIVE);
    }

    // 3. Validate active và status
    if (!user.getActive() || user.getStatus() == UserStatus.INACTIVE) {
        throw new BusinessException(403, "Tài khoản đã bị vô hiệu hóa");
    }

    // 4. Verify password
    if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
        user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
        if (user.getFailedLoginAttempts() >= 5) {
            user.setLockedUntil(LocalDateTime.now().plusMinutes(15));
            user.setStatus(UserStatus.LOCKED);
        }
        userRepository.save(user);
        throw new BusinessException(401, "Email hoặc mật khẩu không đúng");
    }

    // 5. Reset failed attempts on success
    user.setFailedLoginAttempts(0);
    user.setLockedUntil(null);
    if (user.getStatus() == UserStatus.LOCKED) user.setStatus(UserStatus.ACTIVE);
    userRepository.save(user);

    // 6. Generate tokens
    String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getRole().name());
    String refreshTokenValue = UUID.randomUUID().toString();

    RefreshToken refreshToken = new RefreshToken();
    refreshToken.setToken(refreshTokenValue);
    refreshToken.setUserId(user.getId());
    refreshToken.setExpiresAt(LocalDateTime.now().plusDays(7));
    refreshToken.setRevoked(false);
    refreshTokenRepository.save(refreshToken);

    return new LoginResponse(accessToken, refreshTokenValue);
}
```

### AuthServiceImpl — Refresh Flow Chi Tiết

```java
public LoginResponse refresh(String refreshTokenValue) {
    RefreshToken refreshToken = refreshTokenRepository.findByTokenAndRevokedFalse(refreshTokenValue)
        .orElseThrow(() -> new BusinessException(401, "Phiên đăng nhập hết hạn"));

    if (LocalDateTime.now().isAfter(refreshToken.getExpiresAt())) {
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);
        throw new BusinessException(401, "Phiên đăng nhập hết hạn");
    }

    // Revoke old token (rotation)
    refreshToken.setRevoked(true);
    refreshTokenRepository.save(refreshToken);

    User user = userRepository.findById(refreshToken.getUserId())
        .orElseThrow(() -> new BusinessException(401, "Phiên đăng nhập hết hạn"));

    // Issue new tokens
    String newAccessToken = jwtUtil.generateAccessToken(user.getId(), user.getRole().name());
    String newRefreshTokenValue = UUID.randomUUID().toString();

    RefreshToken newRefreshToken = new RefreshToken();
    newRefreshToken.setToken(newRefreshTokenValue);
    newRefreshToken.setUserId(user.getId());
    newRefreshToken.setExpiresAt(LocalDateTime.now().plusDays(7));
    newRefreshToken.setRevoked(false);
    refreshTokenRepository.save(newRefreshToken);

    return new LoginResponse(newAccessToken, newRefreshTokenValue);
}
```

### SecurityConfig — Thêm AuthenticationEntryPoint

**Vấn đề hiện tại:** SecurityConfig không có custom `AuthenticationEntryPoint`. Spring Security mặc định trả response HTML hoặc JSON không theo format `ApiResponse`.  

**Yêu cầu AC-5:** Request không có token → `{ "code": 401, "message": "Chưa xác thực" }`

```java
// Thêm vào SecurityConfig:
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

**Lưu ý:** Vẫn giữ `@ExceptionHandler(AccessDeniedException.class)` trong GlobalExceptionHandler làm fallback. Nhưng `accessDeniedHandler` trong SecurityConfig sẽ xử lý trước cho Spring Security access denied.

### data.sql — Uncomment Admin Seed

Uncomment hoàn toàn đoạn INSERT trong `data.sql`:
```sql
INSERT INTO users (id, email, password_hash, full_name, role, status, active, created_at, updated_at)
SELECT 1, 'admin@hrms.vn',
       '$2a$12$LJ3UlGf2ZOi0YBCBHxZGBOQUEXBzjXTrZMj5fRmKL2nNqFZ.QR2Iq',
       'Quản trị viên', 'ADMIN', 'ACTIVE', true, NOW(), NOW()
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'admin@hrms.vn');
```

**Mật khẩu:** `Admin@123` (BCrypt 12 rounds)  
**Lưu ý:** `data.sql` chạy SAU Hibernate schema creation do `spring.jpa.defer-datasource-initialization: true` đã có từ Story 1.1. Bảng `users` được tạo khi `User.java @Entity` tồn tại → INSERT sẽ hoạt động.

### Package Structure — File Locations

```
backend/src/main/java/com/hrms/
├── user/
│   ├── entity/
│   │   ├── User.java                    ← TẠO MỚI
│   │   ├── UserRole.java                ← TẠO MỚI (enum: ADMIN, MANAGER, EMPLOYEE)
│   │   └── UserStatus.java             ← TẠO MỚI (enum: ACTIVE, INACTIVE, LOCKED)
│   └── repository/
│       └── UserRepository.java          ← TẠO MỚI
├── auth/
│   ├── controller/
│   │   └── AuthController.java          ← TẠO MỚI
│   ├── dto/
│   │   ├── LoginRequest.java            ← TẠO MỚI
│   │   ├── LoginResponse.java           ← TẠO MỚI
│   │   └── RefreshTokenRequest.java     ← TẠO MỚI
│   ├── entity/
│   │   └── RefreshToken.java            ← TẠO MỚI
│   ├── repository/
│   │   └── RefreshTokenRepository.java  ← TẠO MỚI
│   └── service/
│       ├── AuthService.java             ← TẠO MỚI (interface)
│       └── impl/
│           └── AuthServiceImpl.java     ← TẠO MỚI
└── common/
    ├── security/
    │   ├── SecurityConfig.java          ← SỬA (thêm exceptionHandling)
    │   ├── JwtAuthenticationFilter.java ← KHÔNG THAY ĐỔI
    │   └── CustomUserDetails.java       ← KHÔNG THAY ĐỔI
    └── util/
        └── JwtUtil.java                 ← SỬA (xóa generateRefreshToken())

backend/src/main/resources/
└── data.sql                             ← SỬA (uncomment INSERT)

backend/src/test/java/com/hrms/
└── auth/
    ├── AuthControllerIntegrationTest.java  ← TẠO MỚI
    └── AuthServiceTest.java                ← TẠO MỚI
```

### Testing Strategy — Áp dụng Learning từ Story 1.2

**CRITICAL:** Spring Boot 4.0.6 đã bỏ `@DataJpaTest` và `@WebMvcTest`. Dùng `@SpringBootTest @ActiveProfiles("test")` cho tất cả integration tests.

**application-test.yml** đã có `spring.sql.init.mode: never` → `data.sql` KHÔNG chạy trong test profile. Test phải tự tạo test data.

```java
// AuthControllerIntegrationTest.java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Transactional  // Spring's annotation, not jakarta
class AuthControllerIntegrationTest {

    @Autowired TestRestTemplate restTemplate;
    @Autowired UserRepository userRepository;
    @Autowired PasswordEncoder passwordEncoder;

    @BeforeEach
    void setup() {
        // Tạo test user vì data.sql không chạy trong test profile
        User user = new User();
        user.setEmail("test@hrms.vn");
        user.setPasswordHash(passwordEncoder.encode("Password123"));
        user.setFullName("Test User");
        user.setRole(UserRole.ADMIN);
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);
    }

    @Test void loginSuccess() { ... }
    @Test void loginWrongPassword() { ... }
    @Test void loginAccountLocked() { ... }
    @Test void refreshSuccess() { ... }
    @Test void refreshRevokedToken() { ... }
}
```

```java
// AuthServiceTest.java — Unit test (không cần Spring context)
class AuthServiceTest {
    @Mock UserRepository userRepository;
    @Mock RefreshTokenRepository refreshTokenRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock JwtUtil jwtUtil;
    @InjectMocks AuthServiceImpl authService;

    @Test void lockAfter5FailedAttempts() { ... }
    @Test void autoUnlockAfter15Minutes() { ... }
    @Test void resetAttemptsOnSuccess() { ... }
}
```

### Naming Conventions (ARCH-38)

- Package: `com.hrms.auth`, `com.hrms.user`
- Entity: `User`, `RefreshToken` (PascalCase)
- Repository: `UserRepository`, `RefreshTokenRepository`
- Service: `AuthService` (interface) / `AuthServiceImpl` (impl)
- Controller: `AuthController`
- DTO suffix: `Request` / `Response` — `LoginRequest`, `LoginResponse`, `RefreshTokenRequest`
- DB table names: `users`, `refresh_tokens` (snake_case plural — ARCH-37)
- DB column names: `email`, `password_hash`, `full_name`, `role`, `status`, `failed_login_attempts`, `locked_until`, `force_password_change` (snake_case — ARCH-37)

### API Endpoints

```
POST /api/v1/auth/login
  Request: { "username": "admin@hrms.vn", "password": "Admin@123" }
  Response 200: { "code": 200, "message": "Thành công", "data": { "accessToken": "...", "refreshToken": "..." } }
  Response 401: { "code": 401, "message": "Email hoặc mật khẩu không đúng", "data": null }
  Response 403: { "code": 403, "message": "Tài khoản đã bị khoá. Thử lại sau 15 phút", "data": null }

POST /api/v1/auth/refresh
  Request: { "refreshToken": "uuid-string" }
  Response 200: { "code": 200, "message": "Thành công", "data": { "accessToken": "...", "refreshToken": "..." } }
  Response 401: { "code": 401, "message": "Phiên đăng nhập hết hạn", "data": null }
```

**Public endpoints** (đã config trong SecurityConfig.java): `/api/v1/auth/login`, `/api/v1/auth/refresh`

### JwtUtil — Phần CÒN DÙNG (không thay đổi)

```java
// Các method GIỮ NGUYÊN:
generateAccessToken(Long userId, String role)  // ← DÙNG trong AuthServiceImpl
validateToken(String token)                    // ← DÙNG trong JwtAuthenticationFilter
getUserId(String token)                        // ← DÙNG trong JwtAuthenticationFilter
getRole(String token)                          // ← DÙNG trong JwtAuthenticationFilter

// Method XÓA:
generateRefreshToken(Long userId)              // ← XÓA — SAI kiến trúc, không có ai gọi
```

### Các thư viện đã có — KHÔNG cần thêm

Tất cả dependencies đã có từ Story 1.1 (pom.xml):
- `jjwt` — JWT generation (dùng trong JwtUtil)
- `spring-boot-starter-security` — Spring Security
- `spring-boot-starter-data-jpa` — JPA/Hibernate
- `spring-boot-starter-validation` — @NotBlank, @Valid
- `lombok` — @Getter, @Setter, @RequiredArgsConstructor
- `mysql-connector-j` — MySQL driver
- H2 đã được thêm vào pom.xml với scope `test` (xác nhận từ Story 1.2)

### Learnings từ Story 1.2

1. **`@SQLRestriction("active = true")`** là đúng cho Hibernate 6.x (Spring Boot 4.x). Dùng nếu User entity cần soft delete via `active` field (kế thừa từ BaseEntity). KHÔNG dùng `@Where`.
2. **`@SpringBootTest @ActiveProfiles("test")`** cho TẤT CẢ integration tests — Spring Boot 4.0.6 không có @DataJpaTest.
3. **`org.springframework.transaction.annotation.Transactional`** (không phải `jakarta.transaction.Transactional`) cho Spring test context.
4. **`spring.sql.init.mode: never`** trong test profile → `data.sql` không chạy trong tests. Test phải tự tạo dữ liệu trong @BeforeEach.
5. **`DB_ROOT_PASSWORD`** (không phải `DB_PASSWORD`) là env var đúng cho datasource password.
6. **`SimpleCacheManager`** trong CacheConfig yêu cầu đăng ký tên cache mới trước khi dùng trong `@Cacheable`. Nếu story này thêm cache mới → phải update CacheConfig.

### Scope Boundary — Story này KHÔNG làm

- KHÔNG implement login history recording (FR-29 — Story 8.2)
- KHÔNG implement audit log cho User entity (Story 8.x)
- KHÔNG implement user creation/disable/reset (FR-3 — Story 1.5)
- KHÔNG implement RBAC @PreAuthorize logic (Story 1.4)
- KHÔNG implement Department scope filtering (Story 1.4)
- KHÔNG implement logout endpoint (acceptable to defer — token-based auth, frontend clears localStorage)

### Architecture Compliance Checklist

- ✅ ARCH-14: jjwt, HMAC-SHA256, 30min, payload: sub(userId), role, iat, exp
- ✅ ARCH-15: Refresh token = UUID trong `refresh_tokens` table (token, user_id, expires_at, revoked), 7 ngày, rotation
- ✅ ARCH-16: JwtAuthenticationFilter → CustomUserDetails(userId, role, departmentId) — KHÔNG thay đổi filter
- ✅ ARCH-19: BCryptPasswordEncoder strength 12 (đã có trong SecurityConfig)
- ✅ ARCH-21: Base path /api/v1/, plural nouns
- ✅ ARCH-22: Unified ApiResponse<T> {code, message, data}
- ✅ ARCH-26: BusinessException → GlobalExceptionHandler → ApiResponse
- ✅ ARCH-37: snake_case tables/columns (users, refresh_tokens, failed_login_attempts...)
- ✅ ARCH-38: PascalCase entities, {Entity}Repository, {Entity}Service/{Entity}ServiceImpl, {Entity}Controller
- ✅ NFR-1: BCrypt cost factor 12
- ✅ NFR-2: Access token 30min, refresh token 7 ngày
- ✅ NFR-3: Tất cả API yêu cầu authentication (trừ /login và /refresh — đã config)

### Dependency với AuditConfig (QUAN TRỌNG)

`AuditConfig.java` trong Story 1.2 có:
```java
if (!(auth.getPrincipal() instanceof CustomUserDetails details)) {
    return Optional.empty();
}
return Optional.of(details.getUserId());
```

Khi `User.save()` được gọi trong `AuthServiceImpl`, Spring JPA Auditing sẽ cố lấy `createdBy`/`updatedBy` từ SecurityContext. Trong quá trình login (trước khi set token), SecurityContext chưa có user → `AuditorAware` trả `Optional.empty()` → `createdBy = null`. **Đây là behavior đúng** — không cần fix gì. Hibernate chấp nhận null cho Long field.

### References

- [Source: epics.md — Story 1.3 acceptance criteria, lines 357-393]
- [Source: architecture.md — ARCH-14, ARCH-15, ARCH-16, ARCH-19, ARCH-37, ARCH-38]
- [Source: Story 1.2 Dev Agent Record — Spring Boot 4 test patterns, @SQLRestriction]
- [Source: sprint-change-proposal-2026-06-20.md — JWT payload chỉ có sub(userId)+role, KHÔNG có tenantId]

## Dev Agent Record

### Agent Model Used

(để dev agent điền khi implement)

### Completion Notes List

(để dev agent điền khi implement)

### File List

(để dev agent điền khi implement)

### Change Log

- 2026-06-21: Story 1.3 tạo bởi create-story — phân tích hiện trạng, xác định JwtUtil bug, SecurityConfig gap, data.sql cần uncomment

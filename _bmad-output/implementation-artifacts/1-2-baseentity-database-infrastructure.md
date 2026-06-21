---
baseline_commit: 93b5ea5abee1b64be27becf544fc76b131c71405
---

# Story 1.2: BaseEntity & Database Infrastructure

Status: done

## Story

As a **Developer**,
I want the foundational data layer with BaseEntity, soft delete, and AES encryption verified and tested,
So that all future entities automatically inherit audit fields and soft delete, with encryption working correctly.

## Acceptance Criteria

1. **AC-1: BaseEntity audit fields auto-populate**
   - Given a new entity extending BaseEntity
   - When it is persisted
   - Then it has: `id` (Long AUTO_INCREMENT), `active` (Boolean, default true), `createdAt`/`updatedAt` (LocalDateTime, auto-set by JPA Auditing), `createdBy`/`updatedBy` (Long, from SecurityContext via AuditorAware)

2. **AC-2: AES-256-GCM encryption transparent**
   - Given an entity field annotated with `@Convert(converter = AesEncryptConverter.class)`
   - When the entity is saved
   - Then the field is encrypted with AES-256-GCM using ENCRYPTION_KEY from environment
   - And stored as Base64 VARCHAR in the database
   - When read back, the field is decrypted transparently to the original value

3. **AC-3: Soft delete via @SQLRestriction works**
   - Given a record with `active = false`
   - When any standard Spring Data query executes
   - Then the record is excluded from results
   - And the record still exists in the database (verifiable via native query)

4. **AC-4: data.sql seed deferred correctly**
   - Given application startup in dev profile
   - When Hibernate ddl-auto=update runs first
   - Then `spring.jpa.defer-datasource-initialization: true` ensures data.sql runs AFTER schema creation
   - And data.sql uses idempotent INSERTs (WHERE NOT EXISTS pattern)
   - NOTE: `users` table is created by User entity in Story 1.3 — data.sql INSERT for admin is kept but tested end-to-end in Story 1.3

5. **AC-5: Caffeine cache configured with correct TTLs**
   - `systemConfig`: 1 hour TTL (for future system-level config queries)
   - `dashboardStats`: 5 minutes TTL (evict on data changes)
   - `leaveBalances`: 5 minutes TTL (short-lived for payroll calc)

6. **AC-6: All tests pass**
   - `AesEncryptConverterTest`: round-trip encrypt/decrypt, null handling, IV randomness
   - `BaseEntityAuditTest` (Spring Boot H2 test): createdAt/updatedAt auto-set, createdBy from SecurityContext
   - `SoftDeleteTest` (Spring Boot H2 test): soft-deleted entity excluded from standard query, still exists via native query
   - `HrmsApplicationTests.contextLoads()` continues to pass

## Tasks / Subtasks

- [x] Task 1: Fix CacheConfig — separate TTLs per cache (AC: #5)
  - [x] 1.1: Đổi `dashboardStats` TTL từ 1 giờ xuống 5 phút
  - [x] 1.2: Thêm `systemConfig` cache với TTL 1 giờ
  - [x] 1.3: Giữ `leaveBalances` với TTL 5 phút (sẽ được dùng trong Story 5/6)
  - [x] 1.4: Dùng `SimpleCacheManager` + `buildCache()` helper — mỗi cache có TTL riêng biệt

- [x] Task 2: Verify and note data.sql dependency (AC: #4)
  - [x] 2.1: Xác nhận `spring.jpa.defer-datasource-initialization: true` đã có trong application.yml (đã có từ Story 1.1)
  - [x] 2.2: Xác nhận data.sql dùng pattern idempotent (WHERE NOT EXISTS) — users INSERT giữ nguyên nhưng sẽ chỉ hoạt động sau khi Story 1.3 tạo bảng users
  - [x] 2.3: Comment out toàn bộ INSERT trong data.sql, ghi rõ dependency với Story 1.3

- [x] Task 3: Write AesEncryptConverterTest (AC: #2, #6)
  - [x] 3.1: Test round-trip: encrypt then decrypt → original value (+ Vietnamese text)
  - [x] 3.2: Test null input → null output (cả hai chiều)
  - [x] 3.3: Test IV randomness: encrypt same plaintext twice → different ciphertext
  - [x] 3.4: Test wrong key → RuntimeException thrown on decrypt
  - [x] Bonus: Test Base64 format validation

- [x] Task 4: Write BaseEntity & Soft Delete integration tests with H2 (AC: #1, #3, #6)
  - [x] 4.1: Tạo `TestItem` entity trong test package — extends BaseEntity, chỉ dùng cho test
  - [x] 4.2: BaseEntityAuditTest: persist TestItem, verify `id != null`, `active == true`, `createdAt != null`, `updatedAt != null`
  - [x] 4.3: SoftDeleteTest: persist TestItem, set `active = false`, save → verify standard `findAll()` không trả entity này
  - [x] 4.4: Verify entity still exists via native query — active = false vẫn có trong DB
  - [x] 4.5: Verify `createdBy` được set khi có authenticated user trong SecurityContext (mock Authentication)

- [x] Task 5: Run full test suite validation (AC: #6)
  - [x] 5.1: Chạy `mvn test` — 15/15 tests pass (BUILD SUCCESS)
  - [x] 5.2: Xác nhận HrmsApplicationTests.contextLoads() vẫn pass

## Dev Notes

### Current State — GÌ ĐÃ TỒN TẠI

Story 1.1 đã verify scaffolding. Phần lớn Story 1.2 đã được implement rồi:

| File | Đường dẫn | Trạng thái |
|------|-----------|-----------|
| `BaseEntity.java` | `common/entity/BaseEntity.java` | ✅ DONE — Không cần thay đổi |
| `AesEncryptConverter.java` | `common/util/AesEncryptConverter.java` | ✅ DONE — Không cần thay đổi |
| `AuditConfig.java` | `common/config/AuditConfig.java` | ✅ DONE — Không cần thay đổi |
| `CacheConfig.java` | `common/config/CacheConfig.java` | ⚠️ CẦN SỬA — TTL sai |
| `data.sql` | `src/main/resources/data.sql` | ⚠️ CẦN GHI CHÚ — dependency với Story 1.3 |
| Tests | `src/test/java/com/hrms/` | 📝 CẦN TẠO |

**Dev agent CHỈ cần: fix CacheConfig TTL + write tests. KHÔNG được thay đổi BaseEntity, AesEncryptConverter, AuditConfig.**

### BaseEntity — Đã đúng, KHÔNG thay đổi

```java
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@SQLRestriction("active = true")  // Spring Boot 4.x / Hibernate 6.x — ĐÚNG (không dùng @Where đã deprecated)
public abstract class BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Boolean active = true;

    @CreatedDate @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @CreatedBy @Column(name = "created_by", updatable = false)
    private Long createdBy;

    @LastModifiedBy @Column(name = "updated_by")
    private Long updatedBy;
}
```

**Lưu ý quan trọng:**
- `@SQLRestriction("active = true")` là syntax đúng cho Hibernate 6.x (Spring Boot 4.x). Không dùng `@Where` (deprecated).
- `@EnableJpaAuditing` đã được đặt tại `HrmsApplication.java` — KHÔNG thêm lại.
- `createdBy`/`updatedBy` sẽ là `null` nếu không có authenticated user trong SecurityContext (ví dụ khi seeding data.sql). Đây là behavior đúng — nullable Long.

### AesEncryptConverter — Đã đúng, KHÔNG thay đổi

Cách hoạt động:
1. **Encrypt**: Tạo random 12-byte IV → AES-GCM encrypt plaintext → `[IV(12 bytes) + ciphertext+tag]` → Base64 encode → lưu vào DB
2. **Decrypt**: Base64 decode → split `[IV(12) | ciphertext+tag]` → AES-GCM decrypt → plaintext

Key handling:
```java
byte[] keyBytes = encryptionKey.getBytes();
byte[] key = new byte[32];  // 256-bit AES key
System.arraycopy(keyBytes, 0, key, 0, Math.min(keyBytes.length, 32));
```
- Key từ `ENCRYPTION_KEY` env var, lấy tối đa 32 bytes, pad zeros nếu ngắn hơn
- `.env.example` yêu cầu `ENCRYPTION_KEY` dài đủ 32 ký tự

### AuditConfig — Đã đúng, KHÔNG thay đổi

```java
@Bean
public AuditorAware<Long> auditorProvider() {
    return () -> {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof CustomUserDetails details)) {
            return Optional.empty();  // null khi không có user (e.g., data.sql seeding)
        }
        return Optional.of(details.getUserId());
    };
}
```

### CacheConfig — CẦN SỬA

**Vấn đề hiện tại:**
```java
// Hiện tại: tất cả cache dùng chung TTL 1 giờ — SAI
var manager = new CaffeineCacheManager("dashboardStats", "leaveBalances");
manager.setCaffeine(Caffeine.newBuilder().maximumSize(500).expireAfterWrite(1, TimeUnit.HOURS));
```

**Yêu cầu architecture (ARCH-12):**
- `systemConfig`: 1 giờ (system-level config)
- `dashboardStats`: 5 phút (aggregate stats, hay thay đổi)
- `leaveBalances`: 5 phút (tránh query lặp trong 1 payroll calculation run)

**Fix pattern — dùng `registerCustomCache` hoặc `CaffeineCacheManager` per-cache:**
```java
@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager manager = new SimpleCacheManager();
        manager.setCaches(List.of(
            buildCache("systemConfig", 60, TimeUnit.MINUTES, 100),
            buildCache("dashboardStats", 5, TimeUnit.MINUTES, 100),
            buildCache("leaveBalances", 5, TimeUnit.MINUTES, 1000)
        ));
        return manager;
    }

    private CaffeineCache buildCache(String name, long ttl, TimeUnit unit, int maxSize) {
        return new CaffeineCache(name,
            Caffeine.newBuilder()
                .expireAfterWrite(ttl, unit)
                .maximumSize(maxSize)
                .build());
    }
}
```
Cần import: `org.springframework.cache.caffeine.CaffeineCache`, `org.springframework.cache.support.SimpleCacheManager`.

### data.sql — Dependency với Story 1.3

**Vấn đề:** data.sql INSERT vào bảng `users` nhưng bảng này chưa tồn tại trong Story 1.2 (User entity tạo ở Story 1.3).

**Behavior hiện tại:**
- `spring.jpa.defer-datasource-initialization: true` → data.sql chạy sau Hibernate schema creation
- Hibernate tạo schema dựa trên các `@Entity` classes hiện có
- Story 1.2 không có User entity → Hibernate không tạo bảng `users`
- data.sql chạy → `INSERT INTO users ... WHERE NOT EXISTS (SELECT 1 FROM users ...)` → ERROR: Table 'users' doesn't exist

**Fix cho Story 1.2:** Thêm guard comment, giữ nguyên SQL nhưng wrap trong MySQL conditional:
```sql
-- NOTE: Uncomment below AFTER Story 1.3 creates User entity
-- (users table is created by Hibernate when User.java @Entity exists)
-- Default admin: password = Admin@123 (BCrypt 12 rounds)
-- INSERT INTO users (id, email, password_hash, full_name, role, status, active, created_at, updated_at)
-- SELECT 1, 'admin@hrms.vn', '$2a$12$...', 'Quản trị viên', 'ADMIN', 'ACTIVE', true, NOW(), NOW()
-- FROM DUAL
-- WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'admin@hrms.vn');
```

**Lưu ý cho dev agent:** Comment out toàn bộ INSERT, ghi rõ sẽ được bật lại trong Story 1.3.

### Testing Strategy

**Dùng profile `test` (H2 in-memory):**
- `@SpringBootTest @ActiveProfiles("test")` — H2, ddl-auto=create-drop, data.sql KHÔNG chạy (sql.init.mode=never)

**Test entity helper:**
Để test BaseEntity và soft delete, cần 1 concrete entity. Tạo `TestItem` **trong test package** (không phải main):
```java
// src/test/java/com/hrms/common/entity/TestItem.java
@Entity
@Table(name = "test_items")
public class TestItem extends BaseEntity {
    @Column
    private String name;
    // constructor + getter
}
```

**Test repository:**
```java
// src/test/java/com/hrms/common/entity/TestItemRepository.java
public interface TestItemRepository extends JpaRepository<TestItem, Long> {
    @Query(value = "SELECT * FROM test_items WHERE id = :id", nativeQuery = true)
    Optional<TestItem> findByIdNative(@Param("id") Long id);
}
```

**AesEncryptConverterTest — unit test (không cần Spring):**
```java
class AesEncryptConverterTest {
    private AesEncryptConverter converter;

    @BeforeEach
    void setUp() {
        // 32-char key
        converter = new AesEncryptConverter("test-encryption-key-32-chars-ok!");
    }

    @Test
    void roundTrip() {
        String original = "123456789012345678";
        String encrypted = converter.convertToDatabaseColumn(original);
        String decrypted = converter.convertToEntityAttribute(encrypted);
        assertThat(decrypted).isEqualTo(original);
    }

    @Test
    void nullInNullOut() {
        assertThat(converter.convertToDatabaseColumn(null)).isNull();
        assertThat(converter.convertToEntityAttribute(null)).isNull();
    }

    @Test
    void ivRandomness() {
        String cipher1 = converter.convertToDatabaseColumn("same text");
        String cipher2 = converter.convertToDatabaseColumn("same text");
        assertThat(cipher1).isNotEqualTo(cipher2); // different IV each time
    }
}
```

**BaseEntityAuditTest:**
- Mock `Authentication` với `CustomUserDetails(userId=99L, ...)` vào `SecurityContextHolder` trước khi persist
- Persist TestItem, flush, clear, findById → verify `createdAt != null`, `createdBy == 99L`

**SoftDeleteTest:**
- Persist TestItem (`active = true`), save
- `item.setActive(false)`, save
- `findAll()` → không có item này
- `findByIdNative(item.getId())` → vẫn có, `active = false`

### Architecture Compliance

- **ARCH-8**: `BaseEntity` — id (Long), active (Boolean), createdAt/updatedAt, createdBy/updatedBy → ✅ đã có
- **ARCH-10**: Soft delete `@SQLRestriction("active = true")` → ✅ đã có
- **ARCH-11**: AES-256-GCM JPA converter, key từ env `ENCRYPTION_KEY` → ✅ đã có
- **ARCH-12**: Caffeine cache — systemConfig 1hr, dashboardStats 5min, leaveBalances per-request → ⚠️ CẦN FIX
- **ARCH-13**: ddl-auto=update (dev), seed data.sql → ✅ config đúng, data.sql cần comment out dependency

### Enforcement Rules cho Story này

1. **KHÔNG** thay đổi `BaseEntity.java` — đã đúng
2. **KHÔNG** thay đổi `AesEncryptConverter.java` — đã đúng
3. **KHÔNG** thay đổi `AuditConfig.java` — đã đúng
4. **KHÔNG** tạo entity thực — chỉ tạo `TestItem` trong test package
5. **PHẢI** fix CacheConfig theo pattern đã chỉ định ở trên
6. **PHẢI** comment out data.sql INSERT và ghi rõ dependency
7. Tests phải chạy với `@ActiveProfiles("test")` — dùng H2, không phụ thuộc MySQL
8. Tên test class: `AesEncryptConverterTest` (unit), `BaseEntityAuditTest` (integration), `SoftDeleteTest` (integration)

### Packages & File Locations

```
backend/src/main/java/com/hrms/
└── common/
    ├── config/
    │   └── CacheConfig.java              ← SỬA TTL
    └── (các file khác: KHÔNG THAY ĐỔI)

backend/src/main/resources/
└── data.sql                              ← COMMENT OUT INSERT

backend/src/test/java/com/hrms/
├── HrmsApplicationTests.java             ← ĐÃ CÓ, không thay đổi
└── common/
    └── entity/
        ├── TestItem.java                 ← TẠO MỚI (test entity)
        ├── TestItemRepository.java       ← TẠO MỚI (test repo)
        ├── AesEncryptConverterTest.java  ← TẠO MỚI
        ├── BaseEntityAuditTest.java      ← TẠO MỚI
        └── SoftDeleteTest.java          ← TẠO MỚI
```

### Lưu ý về `createdAt` không null trong test

`@CreatedDate` được set bởi JPA Auditing khi `persist()` — cần `flush()` và `clear()` (hoặc `saveAndFlush()`) rồi `findById()` để đọc lại từ DB. Nếu chỉ dùng object trả về từ `save()` thì `createdAt` có thể đã được set in-memory, không cần reload. Nhưng để test thực sự đúng (verify DB round-trip):
```java
TestItem saved = repo.saveAndFlush(item);
em.clear(); // evict from 1st-level cache
TestItem reloaded = repo.findById(saved.getId()).orElseThrow();
assertThat(reloaded.getCreatedAt()).isNotNull();
```

### Story 1.1 Learnings (từ code review)

- `@SQLRestriction` là đúng cho Spring Boot 4.x (Hibernate 6.x). Không dùng `@Where`.
- `defer-datasource-initialization: true` đã được add ở Story 1.1 — data.sql chạy sau Hibernate. Không thêm lại.
- `spring.sql.init.mode: never` trong application-test.yml — tests không chạy data.sql. Đây là thiết kế đúng để tests độc lập.
- `DB_ROOT_PASSWORD` (không phải `DB_PASSWORD`) là tên env var đúng cho datasource password.

### References

- [Source: epics.md — Story 1.2 acceptance criteria, lines 325-356]
- [Source: architecture.md — ARCH-8 (BaseEntity), ARCH-10 (soft delete), ARCH-11 (AES), ARCH-12 (Caffeine cache), ARCH-13 (ddl strategy)]
- [Source: Story 1.1 Dev Agent Record — confirmed all scaffolding files exist and verified]

## Dev Agent Record

### Agent Model Used

claude-sonnet-4-6 (2026-06-21)

### Debug Log References

- `@DataJpaTest` không tồn tại trong Spring Boot 4.0.6 — SB4 đã bỏ sliced test annotations (DataJpaTest, WebMvcTest, etc.). Fix: dùng `@SpringBootTest @ActiveProfiles("test") @Transactional` thay thế. Existing `HrmsApplicationTests` đã dùng pattern này nên biết nó hoạt động.

### Completion Notes List

- ✅ AC-1: BaseEntity audit fields verified qua `BaseEntityAuditTest` (4 tests) — id, active, createdAt, updatedAt, createdBy đều auto-populate đúng
- ✅ AC-2: AesEncryptConverter verified qua `AesEncryptConverterTest` (7 tests) — round-trip, null, IV randomness, wrong key error, Base64 format
- ✅ AC-3: Soft delete via `@SQLRestriction("active = true")` verified qua `SoftDeleteTest` (3 tests) — bị ẩn khỏi standard query, vẫn tồn tại qua native query
- ✅ AC-4: `defer-datasource-initialization: true` đã có; data.sql đã comment out INSERT để tránh lỗi "Table 'users' doesn't exist" (sẽ uncomment ở Story 1.3)
- ✅ AC-5: CacheConfig fixed — `systemConfig` (1hr), `dashboardStats` (5min), `leaveBalances` (5min) với TTL riêng biệt qua `SimpleCacheManager`
- ✅ AC-6: 15/15 tests pass — BUILD SUCCESS (4.2s)

**Learning cho Story 1.3+:** Spring Boot 4.0.6 đã bỏ `@DataJpaTest` và các sliced test annotations. Dùng `@SpringBootTest @ActiveProfiles("test")` cho tất cả integration tests.

### File List

- `backend/src/main/java/com/hrms/common/config/CacheConfig.java` (updated — SimpleCacheManager + per-cache TTL)
- `backend/src/main/resources/data.sql` (updated — INSERT commented out, dependency Story 1.3 documented)
- `backend/src/test/java/com/hrms/common/entity/TestItem.java` (created — test entity extends BaseEntity)
- `backend/src/test/java/com/hrms/common/entity/TestItemRepository.java` (created — JpaRepository + native query)
- `backend/src/test/java/com/hrms/common/entity/AesEncryptConverterTest.java` (created — 7 unit tests)
- `backend/src/test/java/com/hrms/common/entity/BaseEntityAuditTest.java` (created — 4 integration tests)
- `backend/src/test/java/com/hrms/common/entity/SoftDeleteTest.java` (created — 3 integration tests)

### Code Review Findings (2026-06-21)

**Outcome: 3 findings — 2 FIXED, 1 DEFERRED**

```json
[
  {
    "file": "backend/src/main/java/com/hrms/common/util/AesEncryptConverter.java",
    "line": 26,
    "severity": "HIGH",
    "summary": "getBytes()/new String() without explicit UTF-8 charset causes cross-JVM data corruption",
    "failure_scenario": "On a JVM where default charset is not UTF-8 (some Linux containers default to ISO-8859-1), key derivation on line 26, attribute encoding on line 41, and plaintext decoding on line 66 all produce different bytes. Data encrypted on dev (macOS, UTF-8) becomes permanently undecryptable in prod.",
    "status": "FIXED — added StandardCharsets.UTF_8 to all three calls"
  },
  {
    "file": "backend/src/test/java/com/hrms/common/entity/BaseEntityAuditTest.java",
    "line": 5,
    "severity": "LOW",
    "summary": "jakarta.transaction.Transactional should be org.springframework.transaction.annotation.Transactional in Spring test context",
    "failure_scenario": "Spring 6 handles both but Spring's own annotation is idiomatic for Spring Boot tests and avoids ambiguity if a JTA provider is ever added.",
    "status": "FIXED — switched to org.springframework.transaction.annotation.Transactional in BaseEntityAuditTest and SoftDeleteTest"
  },
  {
    "file": "backend/src/main/java/com/hrms/common/config/CacheConfig.java",
    "line": 20,
    "severity": "LOW",
    "summary": "SimpleCacheManager requires all cache names pre-declared; undeclared @Cacheable names throw IllegalArgumentException at runtime",
    "failure_scenario": "Future story adds @Cacheable(\"newName\") without updating CacheConfig → runtime failure. CaffeineCacheManager creates on-demand; SimpleCacheManager does not. By design (enables per-cache TTL) but requires discipline.",
    "status": "DEFERRED — add comment in CacheConfig noting this constraint; each new cache must be registered here"
  }
]
```

All 15 tests pass after fixes (4+7+3+1=15).

### Change Log

- 2026-06-21: Story 1.2 tạo bởi create-story — phân tích hiện trạng, xác định 2 fix + test suite cần tạo
- 2026-06-21: Implemented — CacheConfig fixed (TTL riêng biệt), data.sql commented out, 14 new tests (7+4+3), 15/15 pass
- 2026-06-21: Code review complete — 3 findings: AesEncryptConverter charset bug FIXED (HIGH), jakarta→Spring @Transactional FIXED (LOW), SimpleCacheManager rigidity DEFERRED (LOW)

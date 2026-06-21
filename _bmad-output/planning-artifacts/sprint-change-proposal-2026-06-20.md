# Sprint Change Proposal — Multi-tenant → Single-company

**Ngày:** 2026-06-20  
**Project:** HRMS  
**Đề xuất bởi:** Huylaiphu3  
**Scope:** Major — ảnh hưởng PRD, Epics, sprint-status  

---

## 1. Tóm tắt vấn đề

**Trigger:** Yêu cầu kiến trúc sai — hệ thống được thiết kế multi-tenant SaaS nhưng thực tế là **hệ thống nội bộ cho 1 công ty duy nhất**.

**Loại:** Hiểu sai yêu cầu ban đầu. Product Brief gốc scope "self-hosted đơn tenant", nhưng PRD tự mở rộng thành multi-tenant SaaS mà không có yêu cầu thực tế.

**Bằng chứng:**
- User xác nhận rõ ràng: "đây là hệ thống nội bộ - chỉ 1 company"
- Code backend/frontend đã được refactor bỏ tenant_id
- Architecture doc đã clean
- Không có yêu cầu kinh doanh nào cần multi-tenant

---

## 2. Phân tích ảnh hưởng

### 2.1 Epic Impact

| Epic | Ảnh hưởng | Chi tiết |
|---|---|---|
| Epic 1 | **Nặng** | Đổi title, xóa Story 1.5, sửa Stories 1.2/1.3/1.4/1.6 |
| Epic 2 | Nhẹ | Bỏ "trong tenant" ở vài chỗ |
| Epic 3 | Nhẹ | "per-tenant IP whitelist" → "IP whitelist hệ thống" |
| Epic 4 | Nhẹ | "per-tenant, per-module" → "per-module" |
| Epic 5 | Nhẹ | Bỏ "in tenant" |
| Epic 6 | Nhẹ | "per-tenant config" → "system config" |
| Epic 7 | Nhẹ | Bỏ "tenantId" trong notification record |
| Epic 8 | Nhẹ | Bỏ tenant_id trong audit log, bỏ System Admin cross-tenant query |
| Epic 9 | Nhẹ | Bỏ "toàn tenant" → "toàn hệ thống" |

### 2.2 Artifact Impact

| Artifact | Trạng thái | Cần làm |
|---|---|---|
| **PRD** | Dính nặng | Xóa section 4.2, FR-4/5/6, sửa 50+ dòng |
| **Epics** | Dính nặng | Sửa Epic 1 title + stories, bỏ Story 1.5, sửa scattered refs |
| **Architecture** | ✅ Đã clean | Không cần thay đổi |
| **UX Design** | Nhẹ | Bỏ System Admin role khỏi sidebar spec |
| **Code** | ✅ Đã clean | Không cần thay đổi |
| **sprint-status.yaml** | Cần cập nhật | Xóa entry 1-5 |

### 2.3 Role Changes

| Cũ (4 roles) | Mới (3 roles) |
|---|---|
| System Admin | **XÓA** |
| Admin (HR Admin) | Admin (HR Admin) — highest role |
| Manager | Manager |
| Employee | Employee |

### 2.4 Data Architecture Changes

| Thành phần | Cũ | Mới |
|---|---|---|
| BaseEntity | tenantId (String, immutable) | **Xóa field tenantId** |
| JWT payload | sub, tenantId, role | sub, role |
| TenantContext | ThreadLocal tenant filter | **Xóa hoàn toàn** |
| Repository methods | findByTenantId(...) | findAll / findBy... (no tenant filter) |
| Config | per-tenant (giờ làm, IP, BH...) | system-wide config table |
| ARCH-8 | BaseEntity có tenantId | BaseEntity không có tenantId |
| ARCH-9 | Tenant isolation via tenantId | **Xóa** |
| ARCH-14 | JWT chứa tenantId | JWT chỉ chứa sub, role |
| ARCH-15 | refresh_tokens có tenant_id | refresh_tokens không có tenant_id |
| ARCH-16 | TenantUserDetails (userId, tenantId, role, deptId) | UserDetails (userId, role, deptId) |
| ARCH-17 | TenantContext ThreadLocal | **Xóa** |
| ARCH-26 | TenantAccessDeniedException | **Xóa** |

---

## 3. Đề xuất hướng giải quyết

**Phương án chọn: Direct Adjustment (Option 1)**

**Lý do:**
- Sprint chưa bắt đầu implement story nào (tất cả backlog)
- Code đã được refactor đúng hướng
- Architecture đã clean
- Chỉ cần cập nhật docs (PRD + Epics) cho consistent
- Không ảnh hưởng timeline — thực tế đơn giản hơn (ít code hơn)
- Risk: Low

**Effort:** Medium (thay đổi nhiều text, nhưng pattern lặp lại)  
**Timeline Impact:** Không ảnh hưởng — bỏ multi-tenant = giảm scope = nhanh hơn  

---

## 4. Chi tiết thay đổi

### 4.1 PRD Changes

#### Change P1: Section 0 — Mục đích tài liệu (Line 14)

**OLD:**
```
**Thay đổi quan trọng so với brief:** V1 được thiết kế SaaS-ready — multi-tenant ở tầng DB/API ngay từ đầu, bổ sung Dynamic Approval Workflow, Audit Log, Employee Documents, Dashboard, và Import dữ liệu. Brief ban đầu scope self-hosted đơn tenant; PRD mở rộng nền tảng để không phải retrofit sau.
```

**NEW:**
```
**Thay đổi quan trọng so với brief:** V1 bổ sung Dynamic Approval Workflow, Audit Log, Employee Documents, Dashboard, và Import dữ liệu so với Product Brief ban đầu.
```

#### Change P2: Section 1 — Tầm nhìn (Lines 18-22)

**OLD:**
```
HRMS là nền tảng quản lý nhân sự **mã nguồn mở, self-hosted, SaaS-ready**, thiết kế cho doanh nghiệp vừa và nhỏ tại Việt Nam (50–500 nhân viên/tenant). Hệ thống thay thế quy trình HR thủ công bằng Excel/giấy tờ — tập trung hóa hồ sơ nhân viên, hợp đồng lao động, chấm công, nghỉ phép, và tính lương vào một nền tảng web duy nhất.

Khác với các giải pháp SaaS đóng gói (Base.vn ~120–200K VND/user/tháng, AMIS HRM ~80–150K, 1Office ~80–120K), HRMS là **mã nguồn mở, không phí thuê bao** — doanh nghiệp sở hữu hoàn toàn dữ liệu và hệ thống, tự triển khai trên hạ tầng riêng bằng Docker. Kiến trúc multi-tenant sẵn sàng từ V1 — mỗi công ty là một Tenant cách ly dữ liệu hoàn toàn trên cùng một instance, mở đường cho mô hình SaaS hosted khi cần. Stack kỹ thuật Spring Boot + MySQL + Angular phổ biến tại Việt Nam — dễ tuyển developer, dễ bảo trì. Tầm nhìn dài hạn: xây dựng cộng đồng contributor và ecosystem plugin, trở thành giải pháp HRM mã nguồn mở hàng đầu cho doanh nghiệp Việt Nam.

V1 bao gồm: hồ sơ nhân viên, hợp đồng lao động, chấm công, nghỉ phép, tính lương, dynamic approval workflow, audit log, quản lý hồ sơ tài liệu, import dữ liệu, và dashboard — đủ để vận hành HR nghiệp vụ hàng ngày với nền tảng SaaS vững chắc.
```

**NEW:**
```
HRMS là hệ thống quản lý nhân sự **nội bộ, self-hosted**, thiết kế cho doanh nghiệp vừa và nhỏ tại Việt Nam (50–500 nhân viên). Hệ thống thay thế quy trình HR thủ công bằng Excel/giấy tờ — tập trung hóa hồ sơ nhân viên, hợp đồng lao động, chấm công, nghỉ phép, và tính lương vào một nền tảng web duy nhất.

Đây là hệ thống nội bộ cho **một công ty duy nhất** — không phải SaaS, không multi-tenant. Doanh nghiệp sở hữu hoàn toàn dữ liệu và hệ thống, tự triển khai trên hạ tầng riêng bằng Docker. Stack kỹ thuật Spring Boot + MySQL + Angular phổ biến tại Việt Nam — dễ tuyển developer, dễ bảo trì.

V1 bao gồm: hồ sơ nhân viên, hợp đồng lao động, chấm công, nghỉ phép, tính lương, dynamic approval workflow, audit log, quản lý hồ sơ tài liệu, import dữ liệu, và dashboard — đủ để vận hành HR nghiệp vụ hàng ngày.
```

#### Change P3: Section 2.1 — Jobs To Be Done (Line 28)

**OLD:**
```
- **System Admin (Super Admin)** cần quản lý các Tenant, tạo công ty mới, theo dõi hệ thống tổng thể.
```

**NEW:** XÓA dòng này.

#### Change P4: Section 2.3 — User Journeys

- UJ-1: Bỏ "(tenant abc-corp)" — chỉ nói "công ty"
- UJ-2: Bỏ "(IP nằm trong whitelist của tenant)" → "(IP nằm trong whitelist)"
- UJ-3: Bỏ "Approval Workflow Template của tenant" → "Approval Workflow Template"
- UJ-8: Bỏ "Công ty ABC vẫn giữ chuỗi cũ — mỗi tenant cấu hình riêng"

#### Change P5: Section 3 — Thuật ngữ (Lines 67-71)

**XÓA:**
```
- **Tenant** — Một công ty/tổ chức sử dụng hệ thống. Mỗi Tenant có dữ liệu cách ly hoàn toàn. Được định danh bằng tenant_id trên mọi bảng dữ liệu.
- **System Admin (Super Admin)** — Vai trò quản trị toàn hệ thống, quản lý Tenant. Không thuộc Tenant cụ thể.
```

**SỬA:**
```
- **Admin (HR Admin)** — Vai trò quản trị cao nhất trong hệ thống. Quản lý nhân viên, chấm công, lương, cấu hình hệ thống.
- **Nhân viên (Employee)** — Người lao động có hồ sơ trong hệ thống. Thuộc một Phòng ban, giữ một Chức vụ, có một hoặc nhiều Hợp đồng.
- **Phòng ban (Department)** — Đơn vị tổ chức trong công ty. Có một Manager phụ trách.
```

Cũng sửa:
- "per-Tenant" → "hệ thống" (BHXH, IP Whitelist, Approval Workflow)

#### Change P6: Section 4.1 — Xác thực & Phân quyền (Lines 94-118)

**SỬA mô tả:**
```
**Mô tả:** Hệ thống xác thực người dùng bằng username/password, cấp JWT token cho các request tiếp theo. Phân quyền theo 3 vai trò: Admin, Manager, Employee.
```

**SỬA FR-1:**
- "JWT payload chứa: user_id, tenant_id, role" → "JWT payload chứa: user_id, role"

**SỬA FR-2:**
- "4 vai trò (System Admin, Admin, Manager, Employee). Tenant_id filter mọi request." → "3 vai trò (Admin, Manager, Employee)."
- Xóa: "Admin tenant A truy cập dữ liệu tenant B → HTTP 403"
- Xóa: "Mọi API request tự động filter theo tenant_id từ JWT..."

**SỬA FR-3:**
- "Admin tạo/vô hiệu hóa/reset password trong tenant mình" → "Admin tạo/vô hiệu hóa/reset password tài khoản"

#### Change P7: Section 4.2 — Multi-tenant (Lines 130-161)

**XÓA TOÀN BỘ SECTION 4.2** (FR-4, FR-5, FR-6)

#### Change P8: Sections 4.3-4.14 — Scattered tenant references

Thay thế pattern lặp:
- "trong tenant" → xóa hoặc "trong hệ thống"
- "per-tenant" → "hệ thống" hoặc xóa
- "Mỗi tenant có whitelist riêng" → xóa
- "Scoped tenant" → xóa
- "chỉ tenant hiện tại" → xóa
- "tenant_id/employee_id/" → "employee_id/"
- "kiểm tra quyền (tenant + role)" → "kiểm tra quyền (role)"

#### Change P9: Section 5 — Non-Goals (Lines 596-597)

**XÓA:**
```
- **Không self-service tenant registration** — System Admin tạo tenant thủ công. UI đăng ký + subscription/billing deferred to V2.
- **Không per-tenant SMTP** — dùng SMTP chung. Per-tenant email config deferred to V2.
```

#### Change P10: Section 6 — MVP Scope (Lines 605-606)

**XÓA:**
```
- **Multi-tenant architecture:** tenant_id trên mọi bảng, data isolation, tenant config per-company.
```

**SỬA:**
```
- **Xác thực:** username/password + JWT, phân quyền 3 vai trò (Admin, Manager, Employee).
```

Trong "Ngoài phạm vi MVP":
**XÓA:**
- "Self-service tenant registration + billing → V2"
- "Per-tenant SMTP config → V2"
- "SSO/LDAP per-tenant → V2"
- "Database-per-tenant isolation → V3+"

#### Change P11: Section 7 — Tiêu chí thành công

**XÓA SM-4:**
```
- **SM-4**: Dữ liệu giữa các tenant cách ly 100% — không xảy ra data leak cross-tenant. Validates FR-5.
```

#### Change P12: Section 8 — Cross-Cutting NFRs

**XÓA:**
- "JWT chứa tenant_id để enforce isolation" (trong Bảo mật)
- "**Tenant isolation:** Application layer enforce tenant_id filter..." (toàn bộ bullet)
- "across all tenants" (trong Hiệu năng)
- "Database index trên tenant_id cho mọi bảng nghiệp vụ" → XÓA

**SỬA:**
- "per-tenant" → "hệ thống" (trong Tuân thủ pháp luật)

#### Change P13: Section 9 — Ràng buộc

**XÓA:**
```
- **Multi-tenant model:** Shared database, tenant_id column. `[ASSUMPTION: V1 hỗ trợ tối đa 100 tenants, mỗi tenant tối đa 500 nhân viên trên cùng một instance.]`
```

**SỬA:**
```
- **Hạ tầng tối thiểu:** VPS 2 vCPU, 4GB RAM.
- **File storage:** Local filesystem (10GB minimum cho 500 nhân viên).
```

#### Change P14: Section 10 — Open Questions

**XÓA Q5 và Q6:**
```
5. Tenant onboarding flow — System Admin tạo tenant cần những thông tin gì tối thiểu?
6. File storage limit per-tenant — có cần quota không?
```

#### Change P15: Section 11 — Assumptions Index

**XÓA tất cả tenant-related assumptions:**
- §4.1 "Một user = một vai trò = một tenant" → "Một user = một vai trò"
- §4.2 FR-5 — Shared database approach → XÓA
- §4.5 FR-14 — "per-tenant" → "hệ thống"
- §4.7 FR-21 — "per-tenant" → "hệ thống"  
- §9 — "VPS 4 vCPU / 8GB RAM cho multi-tenant (100 tenants × 500 employees)" → "VPS 2 vCPU / 4GB RAM (500 employees)"
- §9 — "50GB disk tối thiểu cho file storage" → "10GB disk"

---

### 4.2 Epics Changes

#### Change E1: Requirements Inventory (Lines 20-25)

**SỬA FR-1:**
```
FR-1: Đăng nhập bằng username/password — trả JWT access token (30 phút) + refresh token (7 ngày). JWT payload: user_id, role. Sai 5 lần → khóa 15 phút.
```

**SỬA FR-2:**
```
FR-2: Phân quyền RBAC — 3 vai trò (Admin, Manager, Employee). Employee → 403 khi truy cập ngoài scope. Manager chỉ thấy phòng ban mình.
```

**SỬA FR-3:**
```
FR-3: Quản lý tài khoản — Admin tạo/vô hiệu hóa/reset password. Tạo nhân viên → auto tạo tài khoản + gửi email.
```

**XÓA FR-4, FR-5, FR-6**

**SỬA scattered "trong tenant" → xóa** cho FR-9, FR-13, FR-14, FR-17, FR-19, FR-25, FR-27, FR-28, FR-30, FR-31, FR-35

**SỬA NFRs:**
- NFR-2: Bỏ "JWT chứa tenant_id để enforce isolation"
- NFR-6: XÓA hoàn toàn (tenant isolation)
- NFR-8: Bỏ "(across all tenants)"
- NFR-9: Bỏ "/tenant"
- NFR-11: XÓA (index trên tenant_id)
- NFR-17: "per-tenant" → "cấu hình được"

**SỬA ARCH:**
- ARCH-8: Bỏ "tenantId (String, immutable after creation)"
- ARCH-9: XÓA hoàn toàn
- ARCH-12: "tenant config 1hr" → "system config 1hr"
- ARCH-14: "Payload: sub(userId), tenantId, role" → "Payload: sub(userId), role"
- ARCH-15: Bỏ "tenant_id" khỏi refresh_tokens
- ARCH-16: "TenantUserDetails (userId, tenantId, role, departmentId)" → "CustomUserDetails (userId, role, departmentId)"
- ARCH-17: XÓA hoàn toàn (TenantContext)
- ARCH-21: Bỏ "Tenant implicit from JWT"
- ARCH-26: Bỏ "TenantAccessDeniedException"

#### Change E2: FR Coverage Map (Lines 187-189)

**XÓA:**
```
FR-4: Epic 1 — Tenant Management (System Admin)
FR-5: Epic 1 — Tenant Data Isolation (tenant_id)
FR-6: Epic 1 — Tenant Configuration per-company
```

#### Change E3: Epic 1 Title & Description (Lines 228-235)

**OLD:**
```
### Epic 1: Foundation — Authentication, Multi-tenant & Core Infrastructure

System Admin tạo được tenant mới. User đăng nhập bằng username/password, nhận JWT, phân quyền đúng vai trò. Data cách ly hoàn toàn giữa các tenant. Frontend có design system premium (sidebar, layout, page header, theme tokens) và auth flow hoàn chỉnh.

**FRs covered:** FR-1, FR-2, FR-3, FR-4, FR-5, FR-6
```

**NEW:**
```
### Epic 1: Foundation — Authentication & Core Infrastructure

User đăng nhập bằng username/password, nhận JWT, phân quyền đúng vai trò (Admin, Manager, Employee). Frontend có design system premium (sidebar, layout, page header, theme tokens) và auth flow hoàn chỉnh.

**FRs covered:** FR-1, FR-2, FR-3
```

#### Change E4: Story 1.2 — BaseEntity (Lines 335-371)

**SỬA title:** "BaseEntity & Database Infrastructure" (bỏ TenantContext)

**SỬA acceptance criteria:**
- Bỏ "tenantId (String, immutable after creation)" khỏi BaseEntity fields
- XÓA toàn bộ acceptance criteria về TenantContext
- Giữ: id, active, createdAt/updatedAt, createdBy/updatedBy, AES encryption, soft delete, Caffeine cache

#### Change E5: Story 1.3 — JWT Authentication (Lines 373-409)

**SỬA:**
- "JWT payload contains: sub (userId), tenantId, role" → "JWT payload contains: sub (userId), role"

#### Change E6: Story 1.4 — RBAC (Lines 411-444)

**SỬA title:** "RBAC Authorization" (bỏ "& Tenant Data Isolation")

**XÓA:**
- Toàn bộ acceptance criteria về SYSTEM_ADMIN role
- "all repository methods include `AND tenant_id = ?`"
- "no result from Tenant B is ever returned"

**GIỮ:**
- Employee không truy cập ADMIN endpoint
- Manager chỉ thấy phòng ban mình
- CORS config
- @PreAuthorize

#### Change E7: Story 1.5 — Tenant Management (Lines 446-475)

**XÓA TOÀN BỘ STORY 1.5**

Renumber: Story 1.6 → 1.5, Story 1.7 → 1.6, Story 1.8 → 1.7

#### Change E8: Story 1.6 (new 1.5) — User Account Management (Lines 476-504)

**SỬA:**
- "an Admin in Tenant A" → "an Admin"
- XÓA: "Admin from Tenant B tries to manage user from Tenant A → 403"

#### Change E9: Epic 3 scattered tenant refs

- Story 3.1: "their tenant" → xóa, "each tenant has its own separate whitelist" → xóa
- Story 3.2: "tenant's whitelist" → "whitelist"
- Story 3.3: "Tenant A with hours 8:00... Tenant B with hours 9:00..." → XÓA acceptance criteria này (không có tenant)

#### Change E10: Epic 4 scattered tenant refs

- "per-tenant, per-module" → "per-module" throughout
- Story 4.1: "per-tenant, per-module" → "per-module", "one workflow active per module per tenant" → "one workflow active per module"
- Story 4.2: "newly created tenant" → "hệ thống", "default workflow applies" giữ nguyên

#### Change E11: Epic 5-9 scattered refs

- "in tenant" → xóa
- "for tenant" → xóa  
- "tenant-configured" → "system-configured" hoặc xóa
- "across all tenants" → xóa
- "scoped to current user's tenant" → xóa
- "tenant Admin(s)" → "Admin(s)"

#### Change E12: Story 2.7 — Document storage path

**SỬA:**
- "uploads/{tenant_id}/{employee_id}/{filename}" → "uploads/{employee_id}/{filename}"

#### Change E13: Story 8.3 — Audit Log Viewer

**XÓA:**
- "System Admin can view across all tenants"

---

### 4.3 Sprint Status Changes

**XÓA entry:** `1-5-tenant-management-configuration: backlog`

**Renumber:**
- `1-5-user-account-management` → giữ ID (rename logic, không cần đổi ID)
- `1-6-frontend-design-system-application-shell` → giữ
- `1-7-frontend-auth-flow-route-protection` → giữ

Thực ra giữ nguyên numbering trong sprint-status cũng OK vì Story 1.5 bị xóa, stories 1.6/1.7 giữ ID cũ.

---

## 5. Handoff & Implementation

### Scope Classification: **Moderate**

Thay đổi chỉ ở docs, không ảnh hưởng code (code đã clean). Nhưng khối lượng text cần sửa lớn.

### Handoff Plan:

| Vai trò | Trách nhiệm |
|---|---|
| **Developer (current)** | Apply tất cả changes trực tiếp vào PRD + Epics + sprint-status |
| **Product Manager** | Không cần — changes chỉ loại bỏ scope, không thêm |

### Tiêu chí thành công:

1. ✅ PRD không còn từ "tenant", "multi-tenant", "System Admin"
2. ✅ Epics không còn Story 1.5, không còn tenant references
3. ✅ sprint-status.yaml không có entry 1-5-tenant
4. ✅ Roles chỉ còn 3: Admin, Manager, Employee
5. ✅ Docs consistent với code hiện tại (đã không có tenant)

### Bước tiếp theo sau khi approve:

1. Apply changes vào PRD (`prd.md`)
2. Apply changes vào Epics (`epics.md`)
3. Update `sprint-status.yaml`
4. Chạy `bmad-sprint-planning` nếu cần refresh
5. Bắt đầu `bmad-create-story` cho Story 1.1

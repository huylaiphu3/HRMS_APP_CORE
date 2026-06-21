---
stepsCompleted: [1, 2, 3]
inputDocuments:
  - planning-artifacts/prds/prd-HRMS-2026-06-09/prd.md
  - planning-artifacts/architecture.md
  - planning-artifacts/ux-designs/ux-HRMS-2026-06-09/DESIGN.md
  - planning-artifacts/ux-designs/ux-HRMS-2026-06-09/EXPERIENCE.md
---

# HRMS - Epic Breakdown

## Overview

This document provides the complete epic and story breakdown for HRMS, decomposing the requirements from the PRD, UX Design, and Architecture requirements into implementable stories.

## Requirements Inventory

### Functional Requirements

FR-1: Đăng nhập bằng username/password — trả JWT access token (30 phút) + refresh token (7 ngày). JWT payload: user_id, role. Sai 5 lần → khóa 15 phút.
FR-2: Phân quyền RBAC — 3 vai trò (Admin, Manager, Employee). Employee → 403 khi truy cập ngoài scope. Manager chỉ thấy phòng ban mình.
FR-3: Quản lý tài khoản — Admin tạo/vô hiệu hóa/reset password. Tạo nhân viên → auto tạo tài khoản + gửi email.
FR-7: CRUD Nhân viên — tạo/xem/sửa/vô hiệu hóa. Mã nhân viên tự sinh. Soft delete only. Bắt buộc: họ tên, CCCD, ngày sinh, giới tính, phòng ban, chức vụ, ngày vào làm.
FR-8: Quản lý Phòng ban & Chức vụ — CRUD phòng ban/chức vụ. Xóa phòng ban có NV → từ chối. Mỗi phòng ban có 1 Manager.
FR-9: Tìm kiếm & Lọc nhân viên — partial match, không phân biệt hoa thường. Filter phòng ban + trạng thái.
FR-10: CRUD Hợp đồng — 3 loại (Thử việc, Xác định thời hạn ≤36 tháng, Không xác định thời hạn). Bắt buộc: loại, ngày BĐ/KT, lương. Tạo mới → cũ chuyển "Đã kết thúc".
FR-11: Cảnh báo hết hạn hợp đồng — thông báo Admin 30 ngày trước. Email + in-app. Dashboard hiển thị danh sách.
FR-12: Check-in / Check-out — IP whitelist validate. Chỉ 1 lần check-in/ngày. Check-out phải sau check-in. Admin chỉnh sửa bổ sung cho NV.
FR-13: Quản lý IP Whitelist — Admin CRUD IP per-module. Có hiệu lực ngay. Whitelist rỗng → fail-safe.
FR-14: Quy tắc đi muộn / về sớm — trễ >15 phút = đi muộn, sớm >15 phút = về sớm. Giờ chuẩn config per-module (mặc định 8:00-17:00).
FR-15: Bảng tổng hợp công tháng — tổng ngày công, đi muộn, về sớm, nghỉ phép, vắng. Employee/Manager/Admin scope khác nhau.
FR-16: Gửi đơn nghỉ phép — chọn loại (5 loại), ngày BĐ/KT, lý do. Hết phép năm → cảnh báo chuyển không lương. Ngày BĐ < hiện tại → từ chối.
FR-17: Duyệt / Từ chối đơn nghỉ phép — theo Approval Workflow Template per-module. Tuần tự. Reject → dừng pipeline + thông báo. Default: Employee → Manager → HR.
FR-18: Quản lý số dư phép — auto cấp phép năm đầu năm (12 ngày mặc định). Pro-rata cho NV mới. Tăng theo thâm niên (+1/5năm). Admin chỉnh tay. Reset đầu năm (carry-over optional).
FR-19: Cấu hình thông số lương — per-module: BHXH/BHYT/BHTN rates, biểu thuế TNCN 7 bậc, giảm trừ gia cảnh, lương tối thiểu vùng.
FR-20: Cấu hình phụ cấp — tạo loại phụ cấp (cố định hoặc % lương). Gán theo NV/phòng ban/chức vụ.
FR-21: Ghi nhận OT — Admin nhập giờ OT. Hệ số: 1.5x (thường), 2.0x (cuối tuần), 3.0x (lễ). Lương giờ = cơ bản / 22 / 8.
FR-22: Tạo bảng lương tháng — auto tính: lương cơ bản × (công TT/công chuẩn) + phụ cấp + OT - BH - thuế = net. Draft → review → xác nhận (lock).
FR-23: Tính thuế TNCN lũy tiến — 7 bậc trên thu nhập chịu thuế. Thu nhập CT = tính thuế - giảm trừ GC - BH. CT ≤ 0 → thuế = 0.
FR-24: Phiếu lương & Xuất Excel — NV xem chi tiết. Xác nhận → gửi email + in-app. Admin xuất Excel tổng hợp.
FR-25: Thông báo in-app — polling 30s. Badge đỏ. Click → đánh dấu đã đọc + navigate. Phân trang, sắp xếp thời gian.
FR-26: Thông báo email — sự kiện: nghỉ phép (gửi/duyệt/từ chối), hợp đồng hết hạn, phiếu lương, tài khoản mới. SMTP chung V1.
FR-27: Ghi nhận audit log tự động — mọi CREATE/UPDATE/DELETE. Ghi: user_id, user_id, timestamp, entity, action, old_value, new_value. Immutable.
FR-28: Xem & Tra cứu audit log — filter: module, user, NV liên quan, thời gian, action. Phân trang.
FR-29: Login History — ghi đăng nhập: user, thời gian, IP, user agent, success/fail + lý do.
FR-30: Upload tài liệu NV — PDF/JPG/PNG/DOCX. 10MB/file, 20 file/NV. Lưu user_id/employee_id/. Trùng tên → đổi tên auto.
FR-31: Quản lý & Xem tài liệu — Admin xem/download/xóa. Employee xem/download cá nhân. Soft delete. Kiểm tra quyền (role-based).
FR-32: HR Dashboard — tổng NV active, mới tháng, nghỉ việc tháng, biến động. Danh sách HĐ hết hạn. Đơn chờ duyệt.
FR-33: Payroll Dashboard — tổng chi phí lương tháng, so sánh tháng trước (%). Phân bổ theo phòng ban.
FR-34: Leave Dashboard — Admin: tổng nghỉ phép tháng, theo loại, phòng ban nghỉ nhiều nhất. Manager: phòng ban mình. Lịch nghỉ team.
FR-35: Cấu hình Approval Workflow Template — Admin tạo/sửa per-module, per-module. Chuỗi bước tuần tự. Thay đổi chỉ áp dụng đơn mới. Mỗi module 1 workflow active.
FR-36: Thực thi Approval Workflow — gửi request → auto tạo pipeline theo template. Tuần tự approve. Reject → dừng + thông báo. Employee xem pipeline status.
FR-37: Mặc định và Fallback — chưa config → default Employee → Manager → HR. Xóa custom → revert. Approver unavailable → escalate Admin.
FR-38: Import nhân viên từ Excel — template download. Upload → tạo hàng loạt. Dòng lỗi → skip + báo chi tiết. 500 dòng/lần.
FR-39: Báo cáo danh sách nhân viên — filter phòng ban, trạng thái, loại HĐ. Xuất Excel.
FR-40: Báo cáo tổng hợp công tháng — mỗi NV 1 dòng: ngày công, đi muộn, về sớm, nghỉ phép, vắng. Xuất Excel.
FR-41: Báo cáo bảng lương tháng — chỉ tháng đã xác nhận. Mỗi NV 1 dòng: gross → net. Tổng cột. Xuất Excel.

### NonFunctional Requirements

NFR-1: Password hash bcrypt (cost factor ≥ 12).
NFR-2: JWT access token TTL 30 phút, refresh token TTL 7 ngày.
NFR-3: Tất cả API yêu cầu authentication (trừ login và refresh endpoint).
NFR-4: Dữ liệu nhạy cảm (CCCD) mã hóa AES-256-GCM at-rest. Salary lưu plaintext DECIMAL(15,0).
NFR-5: HTTPS bắt buộc cho production.
NFR-7: File upload validate type (whitelist PDF/JPG/PNG/DOCX), size limit (10MB), lưu ngoài webroot.
NFR-8: API response < 2 giây cho list views và form submissions với 500 concurrent users.
NFR-9: Tính bảng lương 500 nhân viên < 30 giây.
NFR-10: Xuất Excel < 10 giây cho 500 dòng.
NFR-12: Docker Compose cho toàn bộ stack (Spring Boot + MySQL + Angular + Nginx).
NFR-13: Tài liệu triển khai step-by-step cho người có kiến thức Docker cơ bản.
NFR-14: One-command deploy: docker compose up -d.
NFR-15: Database migration tự động khi nâng cấp version (Hibernate ddl-auto + SQL scripts).
NFR-16: Script backup/restore đi kèm (DB + file storage).
NFR-17: BHXH/BHYT/BHTN và thuế TNCN cấu hình được — không hard-code công thức.
NFR-18: Hợp đồng lao động tuân thủ 3 loại theo Bộ luật Lao động 2019.
NFR-19: OT tính theo đúng hệ số: 1.5x (ngày thường), 2.0x (cuối tuần), 3.0x (ngày lễ).
NFR-20: Audit log immutable — không có API update/delete cho audit records.

### Additional Requirements

**Starter Template (impacts Epic 1 Story 1):**
- ARCH-1: Backend scaffolding via Spring Initializr — Spring Boot 4.0.x, Java 21, Maven, Jar packaging. Dependencies: spring-boot-starter-web, spring-boot-starter-data-jpa, spring-boot-starter-security, spring-boot-starter-validation, spring-boot-starter-mail, mysql-connector-j, jjwt, springdoc-openapi, apache-poi, spring-boot-starter-aop, lombok.
- ARCH-2: Frontend scaffolding via `ng new hrms-frontend --style=scss --routing --strict --standalone`. Angular 17.x + TypeScript strict mode + Angular CLI (esbuild). Dependencies: ng-zorro-antd, dayjs, xlsx. Built-in: Angular Router, HttpClient, Reactive Forms, Signals, RxJS.

**Infrastructure & Deployment:**
- ARCH-3: Docker Compose topology — nginx (alpine, ports 80/443, reverse proxy + Angular build), backend (Spring Boot JAR, Java 21), mysql (8.4, volume mount), mailhog (dev profile only).
- ARCH-4: Monorepo structure: backend/ + frontend/ + nginx/ + docker-compose.yml + .env.example.
- ARCH-5: Spring profiles: dev (debug logging, mailhog), prod (JSON structured logging, real SMTP), test (H2 in-memory).
- ARCH-6: Secrets via Docker .env file: JWT_SECRET, ENCRYPTION_KEY, DB_ROOT_PASSWORD, SMTP credentials.
- ARCH-7: SLF4J + Logback logging. Dev: console DEBUG. Prod: JSON stdout INFO. Request/response filter logs method+URL+status+duration.

**Data Architecture:**
- ARCH-8: Abstract BaseEntity class — id (Long, AUTO_INCREMENT), active (Boolean, default true), createdAt, updatedAt (LocalDateTime, @CreatedDate/@LastModifiedDate), createdBy, updatedBy (Long, @CreatedBy/@LastModifiedBy).
- ARCH-10: Soft delete via Hibernate @Where(clause = "active = true") on BaseEntity. No hard delete exposed via API.
- ARCH-11: AES-256-GCM JPA @Convert(converter = AesEncryptConverter.class) on CCCD field. Key from env ENCRYPTION_KEY. Encrypted → Base64 VARCHAR.
- ARCH-12: Caffeine in-memory cache — system config 1hr, dashboard aggregates 5min, leave balances per-request. No Redis V1.
- ARCH-13: Hibernate ddl-auto=update (dev), validate (prod). Versioned SQL scripts in db/migration/. Seed data in data.sql.

**Authentication & Security:**
- ARCH-14: jjwt library — access token HMAC-SHA256, 30min. Payload: sub(userId), role, iat, exp.
- ARCH-15: Refresh token: UUID in refresh_tokens table (token, user_id, expires_at, revoked). 7-day TTL. Rotation on refresh.
- ARCH-16: JwtAuthenticationFilter (OncePerRequestFilter) → validates JWT → creates CustomUserDetails (userId, role, departmentId) → SecurityContextHolder.
- ARCH-18: @PreAuthorize("hasRole('ADMIN')") + custom annotations for department-scoped access.
- ARCH-19: BCryptPasswordEncoder strength 12. Default password random, sent via email, force-change on first login.
- ARCH-20: CORS: dev allow localhost:4200, prod allow configured domain(s). Methods: GET/POST/PUT/DELETE/PATCH.

**API & Communication:**
- ARCH-21: REST API base path /api/v1/. Plural nouns, kebab-case.
- ARCH-22: Unified ApiResponse<T> { int code, String message, T data }. Handled by @ControllerAdvice GlobalExceptionHandler.
- ARCH-23: Paginated: data contains PageData<T> { content, page, size, totalElements, totalPages }. Default 20, max 100.
- ARCH-24: springdoc-openapi + Swagger UI at /swagger-ui.html. JWT auth in OpenAPI security scheme.
- ARCH-25: Application events: {Entity}{Action}Event via ApplicationEventPublisher. @EventListener in NotificationService, AuditLogService. @Async for email.
- ARCH-26: Custom exceptions: BusinessException(code, message), ResourceNotFoundException → GlobalExceptionHandler → ApiResponse.

**Frontend Architecture:**
- ARCH-27: Feature module structure: features/{module}/components/ services/ models/ {module}.routes.ts. Standalone components (explicit `standalone: true`), lazy-loaded.
- ARCH-28: AuthService (Angular Service + Signals) — stores user, tokens via signal(). Persisted localStorage.
- ARCH-29: JwtInterceptor (HttpInterceptor) — attaches Bearer header. On 401 → refresh → if fail → redirect /login via Router.
- ARCH-30: RoleGuard (CanActivate) checks role from AuthService. Unauthorized → redirect dashboard/403.
- ARCH-31: Angular HttpClient for all API calls. Services return Observable<ApiResponse<T>>. Components consume via async pipe or toSignal(). Manual cache invalidation via service refresh() methods.
- ARCH-32: NG-ZORRO Form + Angular Reactive Forms exclusively for all forms. Reactive Forms provides validation, dynamic fields. NG-ZORRO provides Vietnamese locale and form layout.

**Module Boundaries:**
- ARCH-33: Allowed: common ← all, leave → workflow, payroll → attendance+leave+config, dashboard → read-only, report → read-only, notification ← event-only, dataimport → user.
- ARCH-34: Forbidden: no cross-module entity/repository imports (use service interfaces). dashboard/report never write. common never imports business modules.

**Scheduled Tasks:**
- ARCH-35: ContractExpiryJob — daily scan for contracts expiring within 30 days, trigger notification (FR-11).
- ARCH-36: LeaveBalanceResetJob — annual reset of leave balances (FR-18).

**Naming Conventions (Enforcement):**
- ARCH-37: Database: snake_case tables (plural), snake_case columns, idx_{table}_{cols} indexes, uk_{table}_{cols} unique constraints.
- ARCH-38: Java: com.hrms.{module} packages, PascalCase entities, {Entity}Repository, {Entity}Service/{Entity}ServiceImpl, {Entity}Controller, {Entity}{Action}Request/{Entity}Response DTOs.
- ARCH-39: Angular/TS: kebab-case files (user-list.component.ts), PascalCase classes + suffix (UserListComponent, UserService), IUser interfaces, match backend enum values.
- ARCH-40: API: kebab-case endpoints, camelCase JSON fields, camelCase query params.

### UX Design Requirements

UX-DR1: NG-ZORRO Theme Override — implement custom theme via NG-ZORRO global config + SCSS variables overriding: colors (primary #1677FF, success #059669, warning #D97706, danger #DC2626), border-radius (card 20px, input 12px, tag 9999px), typography (Inter font family, weight 800 for headings/badges), spacing (sidebar 260px, content padding 28px 36px, card padding 22px), shadows (dual-layer card shadow).
UX-DR2: Premium Sidebar Component — dark navy #0F172A background + radial gradient accent (rgba(22,119,255,0.20)). Width 260px. Brand mark gradient square + "People Operations" subtitle. Nav items: SVG icons 19x19 stroke-width 2, 42px height, 13px radius, hover translateX(2px) + color shift. Active: primary-soft bg + 3px left indicator #1677FF. Groups: uppercase 11px/700/0.12em. Bottom: avatar + name + role. Collapsible (icon-only ~72px).
UX-DR3: Content Topbar (integrated, not sticky separate bar) — search pill (320px, 44px, 999px radius, icon inline, shadow subtle), notification bell (40px circle, Badge count), message icon, avatar. No border on icons — hover background + shadow only.
UX-DR4: Page Header Pattern — every page: Breadcrumb (13px/600/faint) → Title (32px/800/-0.04em) → Subtitle (15px/400/muted). Utilities right-aligned same row as title. 28px spacing to content below.
UX-DR5: Premium Card Component — background white, radius 20px, padding 22px, shadow dual-layer (0 18px 50px rgba(15,23,42,0.06), 0 3px 10px rgba(15,23,42,0.04)), NO border. Hover: translateY(-3px) + shadow-hover (0 24px 70px rgba(15,23,42,0.10), 0 8px 18px rgba(15,23,42,0.06)).
UX-DR6: Stat Card (Dashboard KPI) — 4 color variants (blue/green/orange/red). Structure: Icon (48px, 16px radius, tinted bg) left + Sparkline SVG (74x28px, stroke 4px, opacity 0.65) right. Below: Label (13px/600/muted) + Value (36px/800/-0.05em) + Trend text (13px, colored arrow). Decorative gradient circle (120px, opacity, top-right). Hover lift. Click → navigate to module.
UX-DR7: Quick Action Cards (Dashboard) — 4-column grid below topbar. Glass-morphism: rgba(255,255,255,0.76) + backdrop-filter blur(10px), border subtle, radius 18px. Icon (38px, 13px radius, primary-soft bg) + Title (14px/700) + Description (12px/faint). Hover: translateY(-2px) + shadow.
UX-DR8: Data Table Component — wrapper: white bg, radius 20px, premium shadow. Header: bg #F8FAFF, text 12px/800/0.07em uppercase, color #94A3B8. Row: hover #FBFDFF, height 56px, cell padding 16px 24px, divider 1px {colors.line}. Employee column: gradient avatar 38px + Name (700) + Email (12px faint) stacked.
UX-DR9: Data Table Toolbar — search input (280px, 44px, 12px radius, icon inline) + filter buttons (44px, 12px radius, border line, hover primary) + spacer + action buttons (Import/Export outline, Primary CTA filled).
UX-DR10: Hover Row Actions — Edit/Delete icons appear opacity 0→1 on row hover. 32px circle, transparent bg → primary-soft (edit) / danger-soft (delete) on icon hover.
UX-DR11: Pagination Component — below table, 16px padding. Left: "Tổng: {N} nhân viên". Right: page buttons (36px, 10px radius), active page = primary bg white text.
UX-DR12: Bulk Action Bar — primary-soft bg, weight 700, slide up animation when checkbox rows selected. "Đã chọn {N}" + action buttons.
UX-DR13: Employee Avatar Component — gradient circle 38px, white text 14px/800 initials. 6 gradient variants: blue (#3b82f6→#60a5fa), purple (#8b5cf6→#a78bfa), amber (#f59e0b→#fbbf24), green (#10b981→#34d399), pink (#ec4899→#f472b6), indigo (#6366f1→#818cf8).
UX-DR14: Status Badge/Tag System — pill shape (9999px radius), padding 5px 12px, font-size 12px, weight 800, no border, soft semantic backgrounds. 8 status variants: Active (#ECFDF5/#047857), Probation (#FFF7ED/#C2410C), Pending (#FFFBEB/#B45309), Annual Leave (#EFF6FF/#1D4ED8), Sick Leave (#FFFBEB/#B45309), Terminated (#FEF2F2/#B91C1C), Draft (#F1F5F9/#475569), Department purple (#F5F3FF/#6D28D9).
UX-DR15: Contract Deadline Widget — progress bar 5px height, 999px radius, semantic color fill. Ranges: 0-7 days (danger #DC2626, ~80% fill), 8-15 days (warning #D97706, ~50% fill), 16+ days (success #059669, ~30% fill). Text: "X ngày" weight 800 semantic color.
UX-DR16: Activity Timeline (Dashboard) — vertical dot timeline. Dot: 10px circle, semantic color + 5px ring shadow. Text: 14px/700 + time 12px faint. Colors: blue (default), amber (warning), green (success).
UX-DR17: Payroll Card (Dashboard) — currency value 32px/850/-0.05em + trend text. SVG area chart (gradient fill + stroke line). Status badge green pill "Đã xác nhận". Link "Xem chi tiết".
UX-DR18: Step Builder Component — 3 separate section cards (radius 20px): (1) Thông tin workflow: 2-column form — Tên quy trình (input 44px) + Module (select 44px). (2) Điều kiện áp dụng: dynamic condition rows (field select + operator select + value input + delete X) + "Thêm điều kiện" dashed button. (3) Chuỗi phê duyệt: vertical timeline with gradient line + step cards.
UX-DR19: Step Timeline (within Step Builder) — vertical line 2px gradient (primary-soft → line color). Nodes: 44px, 16px radius, numbered. Step 1 fixed "Người gửi đơn" muted + badge "Cố định". Step 2+: card bg #F8FAFF, 2-column form (Loại người duyệt select + Vai trò/Người duyệt dynamic select), delete button. "Thêm bước duyệt" dashed full-width button.
UX-DR20: Preview Pipeline (Step Builder) — strip at bottom of approval section. Gradient bg (#F8FAFF → #F0F7FF). Chip pills: Employee (muted gray bg) → steps (primary-soft bg). Condition text small below conditional step chip.
UX-DR21: Approval Inbox Tabs — pill tabs: 999px radius, 40px height, 20px padding. Active = primary-soft bg. Weight 700. Tabs: "Chờ duyệt" (default), "Đã duyệt", "Từ chối", "Tất cả".
UX-DR22: Approval Pipeline Dots — inline dots: Done (success-soft bg + checkmark), Current (primary-soft bg + circle + 4px ring shadow), Pending (gray bg). Labels 12px/600, current step = primary color text.
UX-DR23: Approve/Reject Buttons — "Duyệt" (primary filled, 34px height, 10px radius, blue glow shadow), "Từ chối" (outline, border line color, danger text). In-row quick actions.
UX-DR24: Input/Button System — all inputs height 44px, radius 12px, border 1px {colors.line}, focus shadow 0 0 0 4px rgba(22,119,255,0.08). Button primary: height 44px, radius 12px, bg primary, shadow 0 14px 30px rgba(22,119,255,0.18).
UX-DR25: Brand Mark — "H" character white on gradient square (linear-gradient 135deg #1677ff → #69b1ff), border-radius 14px, shadow 0 14px 30px rgba(22,119,255,0.30). Below: "People Operations" subtitle.
UX-DR26: Notification Dropdown — 360px width. Items: icon + content (1-2 lines) + time (relative). Unread: bg primary-bg. Footer: "Xem tất cả thông báo" link. aria-live="polite" for badge count updates.
UX-DR27: Form Patterns — Simple CRUD ≤5 fields: NZ Modal. Medium CRUD 6-15 fields: NZ Drawer 560px width. Complex operations: full page. Cancel with dirty state → Popconfirm "Huỷ thay đổi?". Validation inline on blur. Success: NzMessageService.success 3s. Error: NzMessageService.error with server message.
UX-DR28: Responsive Layout — xl(≥1200px): full sidebar + all columns + 4-col dashboard. lg(992-1199): collapsible sidebar + 2-col dashboard + hide secondary table columns. md(768-991): collapsed sidebar default + 2-col dash. sm(<768): hidden sidebar + hamburger drawer + table horizontal scroll + 1-col dash.
UX-DR29: Vietnamese Microcopy Rules — ngắn gọn, verb trước ("Thêm nhân viên", "Xuất Excel"). Không emoji, không "!" cho thông báo thường. Số liệu cụ thể ("3 đơn chờ duyệt" not "Có đơn chờ duyệt"). Error: nói rõ vấn đề + gợi ý hành động.
UX-DR30: State Patterns Implementation — Loading: NZ Skeleton rows (5) matching layout. Empty no data: NZ Empty + text + primary action button. Empty filter: Empty (no image) + "Xoá bộ lọc". Error API: NzMessageService.error toast, full page fail → NZ Result error + "Thử lại". 403/404: NZ Result + "Về Dashboard". Session expired: redirect /login + NzMessageService.warning. Unsaved changes: NzModalService.confirm.
UX-DR31: Interaction Primitives — mouse-first. Click row → navigate detail. Row hover → highlight + show action icons. Enter submit, Esc close modal/drawer, Tab navigate fields. No custom keyboard shortcuts V1. No drag-drop V1. Sidebar collapse toggle saves to localStorage.
UX-DR32: Elevation System — 7 shadow levels: card default (18px+3px dual-layer), card hover (24px+8px), button primary (14px blue glow), brand mark (14px blue glow strong), search bar (10px subtle), dropdown/popover (4px+16px), modal/drawer (8px+30px).
UX-DR33: Navigation Patterns — sidebar collapse toggle (localStorage). Breadcrumb click → navigate up. List → Detail: row click. Detail → Edit: button → Drawer. Detail → Sub-entity: tabs within detail page. Back: breadcrumb or browser back, no custom Back button.
UX-DR34: Accessibility Floor — WCAG 2.1 AA baseline (NG-ZORRO handles). Tab order = reading order. Esc closes modal/drawer/popover. aria-required, aria-invalid on forms (NG-ZORRO default). role="table" on tables. Status tags: color + text label (never color alone). Decorative icons: aria-hidden="true". Avatar: alt = employee name. Notification badge: aria-live="polite".

### FR Coverage Map

FR-1: Epic 1 — Đăng nhập username/password, JWT
FR-2: Epic 1 — Phân quyền RBAC 4 vai trò
FR-3: Epic 1 — Quản lý tài khoản (tạo/vô hiệu hóa/reset)
FR-7: Epic 2 — CRUD Nhân viên
FR-8: Epic 2 — Quản lý Phòng ban & Chức vụ
FR-9: Epic 2 — Tìm kiếm & Lọc nhân viên
FR-10: Epic 2 — CRUD Hợp đồng lao động
FR-11: Epic 2 — Cảnh báo hết hạn hợp đồng
FR-12: Epic 3 — Check-in / Check-out
FR-13: Epic 3 — Quản lý IP Whitelist
FR-14: Epic 3 — Quy tắc đi muộn / về sớm
FR-15: Epic 3 — Bảng tổng hợp công tháng
FR-16: Epic 5 — Gửi đơn nghỉ phép
FR-17: Epic 5 — Duyệt / Từ chối đơn theo Approval Workflow
FR-18: Epic 5 — Quản lý số dư phép
FR-19: Epic 6 — Cấu hình thông số lương
FR-20: Epic 6 — Cấu hình phụ cấp
FR-21: Epic 6 — Ghi nhận OT
FR-22: Epic 6 — Tạo bảng lương tháng
FR-23: Epic 6 — Tính thuế TNCN lũy tiến
FR-24: Epic 6 — Phiếu lương & Xuất Excel
FR-25: Epic 7 — Thông báo in-app
FR-26: Epic 7 — Thông báo email
FR-27: Epic 8 — Ghi nhận audit log tự động
FR-28: Epic 8 — Xem & Tra cứu audit log
FR-29: Epic 8 — Login History
FR-30: Epic 2 — Upload tài liệu nhân viên
FR-31: Epic 2 — Quản lý & Xem tài liệu
FR-32: Epic 9 — HR Dashboard
FR-33: Epic 9 — Payroll Dashboard
FR-34: Epic 9 — Leave Dashboard
FR-35: Epic 4 — Cấu hình Approval Workflow Template
FR-36: Epic 4 — Thực thi Approval Workflow
FR-37: Epic 4 — Mặc định và Fallback
FR-38: Epic 2 — Import nhân viên từ Excel
FR-39: Epic 9 — Báo cáo danh sách nhân viên
FR-40: Epic 9 — Báo cáo tổng hợp công tháng
FR-41: Epic 9 — Báo cáo bảng lương tháng

## Epic List

### Epic 1: Foundation — Authentication & Core Infrastructure

User đăng nhập bằng username/password, nhận JWT, phân quyền đúng vai trò (Admin, Manager, Employee). Frontend có design system premium (sidebar, layout, page header, theme tokens) và auth flow hoàn chỉnh.

**FRs covered:** FR-1, FR-2, FR-3
**NFRs addressed:** NFR-1→5 (security), NFR-12→16 (deployment)
**ARCH covered:** ARCH-1→40 (foundational architecture, scaffolding, BaseEntity, JWT, Spring Security, Docker Compose, monorepo, profiles, naming conventions, module boundaries)
**UX-DRs covered:** UX-DR1 (ConfigProvider theme), UX-DR2 (sidebar), UX-DR3 (topbar), UX-DR4 (page header), UX-DR5 (card component), UX-DR24 (input/button system), UX-DR25 (brand mark), UX-DR28→34 (responsive, microcopy, states, interactions, elevation, navigation, accessibility)

### Epic 2: Employee Lifecycle Management

HR Admin quản lý trọn vẹn hồ sơ nhân viên: thêm/sửa/tìm kiếm nhân viên, phòng ban, chức vụ, upload tài liệu, tạo hợp đồng lao động (3 loại BLLĐ 2019), nhận cảnh báo hết hạn HĐ, import hàng loạt từ Excel.

**FRs covered:** FR-7, FR-8, FR-9, FR-10, FR-11, FR-30, FR-31, FR-38
**NFRs addressed:** NFR-7 (file upload validate), NFR-18 (3 loại HĐ BLLĐ 2019)
**ARCH covered:** ARCH-35 (ContractExpiryJob scheduled task)
**UX-DRs covered:** UX-DR8 (data table), UX-DR9 (toolbar), UX-DR10 (hover actions), UX-DR11 (pagination), UX-DR12 (bulk bar), UX-DR13 (avatar gradient), UX-DR14 (status badges), UX-DR15 (contract deadline widget), UX-DR27 (form patterns — drawer 560px)
**Depends on:** Epic 1

### Epic 3: Attendance Management

Employee check-in/out hàng ngày từ mạng công ty (IP whitelist). Hệ thống tự động đánh dấu đi muộn/về sớm. Admin cấu hình IP whitelist, xem bảng tổng hợp công tháng.

**FRs covered:** FR-12, FR-13, FR-14, FR-15
**Depends on:** Epic 1, Epic 2

### Epic 4: Dynamic Approval Workflow Engine

Admin cấu hình chuỗi duyệt per-module, per-module qua Step Builder UI. Hệ thống có workflow mặc định (Employee → Manager → HR Admin) hoạt động ngay. Engine generic, V1 cho Leave, mở rộng V2.

**FRs covered:** FR-35, FR-36, FR-37
**UX-DRs covered:** UX-DR18 (step builder 3-section), UX-DR19 (step timeline), UX-DR20 (preview pipeline)
**Depends on:** Epic 1

### Epic 5: Leave Management

Employee gửi đơn nghỉ phép (5 loại), xem pipeline duyệt real-time. Manager/Admin duyệt/từ chối qua Approval Inbox. Hệ thống quản lý số dư phép tự động (cấp đầu năm, pro-rata, trừ khi duyệt).

**FRs covered:** FR-16, FR-17, FR-18
**ARCH covered:** ARCH-36 (LeaveBalanceResetJob scheduled task)
**UX-DRs covered:** UX-DR21 (approval inbox tabs), UX-DR22 (pipeline dots), UX-DR23 (approve/reject buttons)
**Depends on:** Epic 2 (employees), Epic 4 (workflow engine)

### Epic 6: Payroll

Admin cấu hình BHXH/BHYT/BHTN rates, biểu thuế TNCN 7 bậc, phụ cấp linh hoạt, OT. Tạo bảng lương tháng auto tính gross→net cho toàn bộ. NV xem phiếu lương chi tiết. Xuất Excel.

**FRs covered:** FR-19, FR-20, FR-21, FR-22, FR-23, FR-24
**NFRs addressed:** NFR-9 (payroll <30s), NFR-10 (Excel <10s), NFR-17 (rates configurable), NFR-19 (OT coefficients)
**UX-DRs covered:** UX-DR17 (payroll card)
**Depends on:** Epic 2 (employee/salary), Epic 3 (attendance), Epic 5 (leave)

### Epic 7: Notifications

User nhận thông báo in-app (bell badge, dropdown, phân trang, mark read) và email SMTP cho sự kiện nghiệp vụ: nghỉ phép gửi/duyệt/từ chối, HĐ hết hạn, phiếu lương, tài khoản mới.

**FRs covered:** FR-25, FR-26
**ARCH covered:** ARCH-25 (ApplicationEvent listeners, @Async email)
**UX-DRs covered:** UX-DR26 (notification dropdown 360px, aria-live)
**Depends on:** Epic 1 (standalone delivery mechanism, events from other modules)

### Epic 8: Audit Log

Mọi thay đổi dữ liệu tự động ghi nhận qua AOP interceptor (retroactive cho tất cả modules). Admin tra cứu audit log với filter (module, user, thời gian, action). Login history truy vết đầy đủ.

**FRs covered:** FR-27, FR-28, FR-29
**NFRs addressed:** NFR-20 (audit immutable)
**Depends on:** Epic 1 (standalone, AOP retroactive)

### Epic 9: Dashboard & Reports

Admin/Manager mở Dashboard thấy ngay KPI: tổng NV, HĐ hết hạn, đơn chờ duyệt, chi phí lương, nghỉ phép. Quick actions. Sparkline charts. 3 báo cáo nghiệp vụ (NV, công, lương) + xuất Excel.

**FRs covered:** FR-32, FR-33, FR-34, FR-39, FR-40, FR-41
**UX-DRs covered:** UX-DR6 (stat cards + sparkline), UX-DR7 (quick actions glass-morphism), UX-DR16 (activity timeline), UX-DR17 (payroll card)
**Depends on:** Data từ Epics 2–6

---

## Epic 1: Foundation — Authentication & Core Infrastructure

User đăng nhập bằng username/password, nhận JWT, phân quyền đúng vai trò (Admin, Manager, Employee). Frontend có design system premium và auth flow hoàn chỉnh.

### Story 1.1: Project Scaffolding & Docker Compose Setup

As a **Developer**,
I want a fully configured monorepo with backend (Spring Boot 4.0.x, Java 21), frontend (Angular 17, TypeScript, NG-ZORRO), and Docker Compose,
So that the team can start developing features on a consistent, reproducible environment.

**Acceptance Criteria:**

**Given** a fresh clone of the repository
**When** I run `docker compose up -d`
**Then** MySQL starts on port 3306, backend on 8080, nginx on 80, frontend dev server on 4200
**And** backend returns health check at `/actuator/health`
**And** frontend renders a blank NG-ZORRO page at `http://localhost:4200`

**Given** the project structure
**When** I inspect the repository
**Then** it follows monorepo layout: `backend/` (Maven, pom.xml with all ARCH-1 dependencies), `frontend/` (package.json with all ARCH-2 dependencies), `nginx/nginx.conf`, `docker-compose.yml`, `docker-compose.dev.yml`, `.env.example`, `.gitignore`
**And** Spring profiles are configured: dev (console DEBUG, mailhog), prod (JSON INFO, real SMTP), test (H2 in-memory)
**And** `.env.example` lists: JWT_SECRET, ENCRYPTION_KEY, DB_ROOT_PASSWORD, SMTP_HOST, SMTP_PORT, SMTP_USER, SMTP_PASS

**Given** the backend package structure
**When** I inspect `src/main/java/com/hrms/`
**Then** packages exist: `common/entity/`, `common/dto/`, `common/security/`, `common/config/`, `common/exception/`, `common/util/`, `user/`

### Story 1.2: BaseEntity & Database Infrastructure

As a **Developer**,
I want the foundational data layer with BaseEntity, soft delete, and encryption,
So that all future entities automatically inherit audit fields and soft delete.

**Acceptance Criteria:**

**Given** a new entity extending BaseEntity
**When** it is persisted
**Then** it has: id (Long AUTO_INCREMENT), active (Boolean, default true), createdAt/updatedAt (LocalDateTime, auto-set), createdBy/updatedBy (Long, from SecurityContext)

**Given** an entity field annotated with `@Convert(converter = AesEncryptConverter.class)`
**When** the entity is saved
**Then** the field is encrypted with AES-256-GCM using ENCRYPTION_KEY from environment
**And** stored as Base64 VARCHAR in the database
**And** when read back, the field is decrypted transparently

**Given** a soft-deleted record (active = false)
**When** any standard query executes
**Then** the record is excluded by Hibernate `@Where(clause = "active = true")`
**And** the record still exists in the database for audit/history purposes

**Given** application startup in dev profile
**When** Hibernate ddl-auto=update runs
**Then** tables are created/updated automatically
**And** seed data (default roles, admin account) loads from `data.sql`

**Given** the Caffeine cache configuration
**When** system config is queried
**Then** results are cached for 1 hour and evicted on config update

### Story 1.3: JWT Authentication — Login & Refresh Token

As a **User**,
I want to log in with username/password and receive JWT tokens,
So that I can securely access the system with automatic session refresh.

**Acceptance Criteria:**

**Given** a registered user with valid credentials
**When** I POST `/api/v1/auth/login` with `{ username, password }`
**Then** I receive `ApiResponse<LoginResponse>` with code 200 containing: accessToken (JWT HMAC-SHA256, 30min TTL), refreshToken (UUID, 7 days)
**And** JWT payload contains: sub (userId), role, iat, exp
**And** password is verified against bcrypt hash (cost factor 12)
**And** response format is `{ "code": 200, "message": "Thành công", "data": { "accessToken": "...", "refreshToken": "..." } }`

**Given** a user who failed login 5 consecutive times
**When** they attempt a 6th login (even with correct credentials)
**Then** they receive `{ "code": 403, "message": "Tài khoản đã bị khoá. Thử lại sau 15 phút" }`
**And** the account auto-unlocks after 15 minutes

**Given** a valid, non-revoked refresh token
**When** I POST `/api/v1/auth/refresh` with `{ refreshToken }`
**Then** I receive new accessToken + new refreshToken
**And** the old refreshToken is marked revoked in `refresh_tokens` table (rotation)

**Given** an expired or revoked refresh token
**When** I POST `/api/v1/auth/refresh`
**Then** I receive `{ "code": 401, "message": "Phiên đăng nhập hết hạn" }`

**Given** any API endpoint except `/api/v1/auth/login` and `/api/v1/auth/refresh`
**When** a request is sent without valid Bearer token
**Then** it receives `{ "code": 401, "message": "Chưa xác thực" }`

**Given** the GlobalExceptionHandler
**When** any unhandled exception occurs in a controller
**Then** it returns ApiResponse with appropriate code and Vietnamese message
**And** stack traces are never exposed in the response

### Story 1.4: RBAC Authorization

As an **Admin**,
I want role-based access control with 3 roles,
So that users only access data within their permission scope.

**Acceptance Criteria:**

**Given** a user with role EMPLOYEE
**When** they access an ADMIN-only endpoint (e.g., PUT `/api/v1/employees/{id}`)
**Then** they receive `{ "code": 403, "message": "Không có quyền truy cập" }`

**Given** a user with role MANAGER in Department A
**When** they query employee list
**Then** they only see employees in Department A in the system

**Given** the CORS configuration in dev profile
**When** the frontend at `http://localhost:4200` sends a request
**Then** the request is allowed with methods GET, POST, PUT, DELETE, PATCH and credentials: true

**Given** the Spring Security filter chain
**When** endpoint authorization is configured
**Then** `@PreAuthorize` annotations enforce role checks on controller methods
**And** public endpoints are only: `/api/v1/auth/login`, `/api/v1/auth/refresh`

### Story 1.5: User Account Management

As an **HR Admin**,
I want to create, disable, and reset passwords for user accounts,
So that I can manage system access for employees in my company.

**Acceptance Criteria:**

**Given** an Admin
**When** they create a new user account with email and role
**Then** the account is created with a random password
**And** email is sent with login credentials
**And** the user must change password on first login (force-change flag)

**Given** an Admin
**When** they disable a user account
**Then** the user's active refresh tokens are revoked
**And** the user cannot log in
**And** the user record remains in the system (soft disable)

**Given** an Admin
**When** they reset a user's password
**Then** a new random password is generated and emailed
**And** the user must change password on next login

### Story 1.6: Frontend Design System & Application Shell

As a **User**,
I want a premium, professional-looking interface with consistent design,
So that the application feels like a high-quality commercial SaaS product.

**Acceptance Criteria:**

**Given** the NG-ZORRO global theme configuration
**When** the application loads
**Then** theme tokens are overridden via SCSS variables + NZ global config: primary #1677FF, card borderRadius 20px, input borderRadius 12px, tag borderRadius 9999px, fontFamily Inter, shadows (dual-layer)
**And** all NG-ZORRO components inherit these overrides globally

**Given** the application shell
**When** an authenticated user loads any page
**Then** they see a sidebar (260px, dark navy #0F172A + radial gradient, SVG icons 19x19, nav items 42px height with hover translateX(2px), active item = primary-soft bg + 3px left indicator #1677FF)
**And** a brand mark ("H" gradient square + "People Operations" subtitle) at sidebar top
**And** a content area with integrated topbar: search pill (320px, 44px, 999px radius), notification bell (40px, badge placeholder), avatar dropdown
**And** page header pattern: breadcrumb (13px/600) → title (32px/800/-0.04em) → subtitle (15px/400)

**Given** a user with role EMPLOYEE
**When** the sidebar renders
**Then** they see only: Dashboard, Chấm công, Nghỉ phép, Phiếu lương (items outside scope are hidden, not disabled)

**Given** a user with role ADMIN
**When** the sidebar renders
**Then** they see all items except "Hệ thống"

**Given** the browser window
**When** resized below 768px
**Then** sidebar hides, hamburger menu appears, content takes full width
**And** at lg (992-1199px), sidebar is collapsible
**And** sidebar collapse state is persisted in localStorage

**Given** any loading state
**When** data is being fetched
**Then** NZ Skeleton rows (5) are shown matching expected layout
**And** empty states show NZ Empty + action CTA button
**And** error states show NzMessageService.error toast or NZ Result page with "Thử lại"
**And** 403 shows "Bạn không có quyền truy cập" + "Về Dashboard"
**And** 404 shows "Trang không tồn tại" + "Về Dashboard"

### Story 1.7: Frontend Auth Flow & Route Protection

As a **User**,
I want to log in, have my session managed automatically, and be redirected based on my role,
So that I have a seamless and secure authentication experience.

**Acceptance Criteria:**

**Given** an unauthenticated user
**When** they navigate to any protected route
**Then** they are redirected to `/login`

**Given** the login page
**When** a user enters valid credentials and submits
**Then** JWT tokens are stored in AuthService (Angular Service + Signals, persisted to localStorage)
**And** JwtInterceptor is configured with `Authorization: Bearer` header
**And** the user is redirected to `/dashboard`

**Given** an authenticated user
**When** an API request returns 401
**Then** JwtInterceptor automatically attempts token refresh
**And** if refresh succeeds, the original request retries with new token
**And** if refresh fails, user is redirected to `/login` with `NzMessageService.warning("Phiên đăng nhập hết hạn")`

**Given** an authenticated user
**When** they navigate to a route outside their role scope
**Then** RoleGuard redirects to 403 page

**Given** an authenticated user
**When** they click "Đăng xuất" from avatar dropdown
**Then** tokens are cleared from localStorage and AuthService
**And** they are redirected to `/login`

**Given** the route structure
**When** configured
**Then** routes match: `/login`, `/dashboard`, `/employees`, `/contracts`, `/attendance`, `/leave`, `/payroll`, `/approvals`, `/reports`, `/audit-log`, `/config/*`, `/system/*`, `/profile`
**And** each route is protected with RoleGuard (CanActivate) checking role from AuthService

---

## Epic 2: Employee Lifecycle Management

HR Admin quản lý trọn vẹn hồ sơ nhân viên: thêm/sửa/tìm kiếm nhân viên, phòng ban, chức vụ, upload tài liệu, tạo hợp đồng lao động (3 loại BLLĐ 2019), nhận cảnh báo hết hạn HĐ, import hàng loạt từ Excel.

### Story 2.1: Department & Position Management

As an **HR Admin**,
I want to create and manage departments and positions,
So that I can organize the company structure before adding employees.

**Acceptance Criteria:**

**Given** an Admin
**When** they POST to create a Department with name
**Then** the Department is created in the system
**And** they can assign a Manager to the department

**Given** an Admin
**When** they try to delete a Department that has active employees
**Then** the request is rejected with message "Không thể xoá phòng ban đang có nhân viên. Chuyển nhân viên trước."

**Given** an Admin
**When** they CRUD Positions (Chức vụ)
**Then** positions are created/updated/soft-deleted in the system
**And** positions can be assigned to employees

**Given** the frontend
**When** Admin navigates to Phòng ban or Chức vụ
**Then** they see a data table (radius 20px, shadow, header #F8FAFF uppercase) with search, pagination
**And** CRUD via NZ Modal (≤5 fields)

### Story 2.2: Employee CRUD — Create & Edit

As an **HR Admin**,
I want to create and edit employee profiles,
So that I can maintain accurate personnel records for my company.

**Acceptance Criteria:**

**Given** an Admin
**When** they click "Thêm nhân viên" on the employee list toolbar
**Then** a Drawer (560px) opens with form fields: Họ tên*, CCCD*, Ngày sinh*, Giới tính*, Email, SĐT, Phòng ban* (Select), Chức vụ* (Select), Ngày vào làm*
**And** required fields show red `*` (NG-ZORRO default)
**And** inline validation on blur

**Given** valid employee data is submitted
**When** the employee is created
**Then** mã nhân viên is auto-generated (unique in the system)
**And** CCCD field is encrypted with AES-256-GCM in database
**And** a user account is auto-created and credentials sent via email (FR-3)
**And** toast: "Thêm nhân viên thành công"

**Given** an Admin
**When** they edit an existing employee via Drawer
**Then** all fields are pre-populated and editable (except mã NV)
**And** userId cannot be changed

**Given** an Admin
**When** they deactivate an employee
**Then** the employee's user account is disabled
**And** the employee disappears from active list but data is preserved
**And** historical records (attendance, payroll, contracts) remain intact

### Story 2.3: Employee List — Search, Filter & Table Display

As an **HR Admin or Manager**,
I want to search and filter employees in a premium data table,
So that I can quickly find and review personnel information.

**Acceptance Criteria:**

**Given** the Employee list page
**When** it loads
**Then** it displays a data table with: gradient avatar (38px, initials, 6 color variants) + Name (700) + Email (12px faint) stacked, Mã NV, Phòng ban (purple badge), Chức vụ, Trạng thái (pill badge: Active green / Probation orange / Terminated red), Ngày vào làm
**And** table matches UX-DR8: radius 20px, header #F8FAFF/12px/800 uppercase, row height 56px, hover #FBFDFF

**Given** the toolbar
**When** Admin interacts
**Then** search input (280px, 44px) filters by name/employee code (debounce 300ms, partial match, case-insensitive)
**And** filter buttons for Department (Select) and Status (Select)
**And** active filters show as Tag row below, removable individually or "Xoá tất cả"
**And** action buttons: "Import Excel", "Xuất Excel" (outline), "Thêm nhân viên" (primary, shadow)

**Given** row hover
**When** mouse enters a row
**Then** Edit and Delete action icons appear (opacity 0→1), 32px circle, transparent→tinted bg

**Given** pagination
**When** results exceed page size
**Then** pagination shows below table: "Tổng: {N} nhân viên" left, page buttons (36px, 10px radius) right, default 20 rows/page

**Given** a Manager
**When** they view the employee list
**Then** they only see employees in their department (read-only, no CRUD actions)

**Given** bulk selection
**When** Admin checks multiple rows
**Then** bulk action bar slides up: "Đã chọn {N}" + "Xuất Excel" + "Xoá" buttons

### Story 2.4: Employee Detail Page with Tabs

As an **HR Admin**,
I want to view complete employee information organized in tabs,
So that I can access all aspects of an employee's profile in one place.

**Acceptance Criteria:**

**Given** an Admin clicks on an employee name/row
**When** the detail page loads
**Then** it shows: page header (employee name, mã NV, department badge, status badge) + NZ Tabs: Thông tin, Hợp đồng, Chấm công, Nghỉ phép, Lương, Tài liệu

**Given** the "Thông tin" tab
**When** displayed
**Then** it shows employee personal info in a structured layout: họ tên, CCCD (masked except last 4 digits for non-admin viewers), ngày sinh, giới tính, email, SĐT, phòng ban, chức vụ, ngày vào làm
**And** "Chỉnh sửa" button opens Drawer with edit form

**Given** an Employee viewing their own profile at `/profile`
**When** the page loads
**Then** they see their personal info (read-only) and can change password
**And** they cannot see other employees' profiles

### Story 2.5: Labor Contract Management

As an **HR Admin**,
I want to create and manage labor contracts for employees,
So that contract records comply with Vietnamese Labor Code 2019.

**Acceptance Criteria:**

**Given** an Admin on Employee Detail → tab "Hợp đồng"
**When** they click "Thêm hợp đồng"
**Then** a Drawer opens with: Loại HĐ* (Select: Thử việc, Xác định thời hạn, Không xác định thời hạn), Ngày bắt đầu*, Ngày kết thúc* (disabled for "Không xác định"), Mức lương cơ bản* (DECIMAL, format VND)

**Given** contract type "Xác định thời hạn"
**When** duration exceeds 36 months
**Then** validation error: "Hợp đồng xác định thời hạn tối đa 36 tháng"

**Given** an employee with an active contract
**When** a new contract is created
**Then** the previous contract status changes to "Đã kết thúc"
**And** only one contract is "Hiện hành" at a time

**Given** the contract list in employee detail tab
**When** displayed
**Then** it shows: loại HĐ (badge), ngày BĐ/KT, mức lương (formatted VND), trạng thái (Hiện hành/Đã kết thúc)

### Story 2.6: Contract Expiry Alerts

As an **HR Admin**,
I want automatic alerts when contracts are about to expire,
So that I never miss a contract renewal deadline.

**Acceptance Criteria:**

**Given** a contract with end date within 30 days
**When** ContractExpiryJob runs daily (scheduled)
**Then** Admin receives notification (in-app + email placeholder event published)
**And** the contract appears in "Hợp đồng sắp hết hạn" widget

**Given** a contract of type "Không xác định thời hạn"
**When** ContractExpiryJob runs
**Then** no expiry alert is generated

**Given** the contract deadline widget (on dashboard or contract list)
**When** displayed
**Then** each expiring contract shows: employee name, contract type, "X ngày" text (weight 800, semantic color) + progress bar (5px, 999px radius)
**And** 0-7 days: danger red ~80% fill, 8-15 days: warning amber ~50% fill, 16-30 days: success green ~30% fill (UX-DR15)

### Story 2.7: Employee Document Management

As an **HR Admin**,
I want to upload and manage documents attached to employee profiles,
So that all personnel documents (CCCD, certificates, CV) are centralized.

**Acceptance Criteria:**

**Given** an Admin on Employee Detail → tab "Tài liệu"
**When** they click "Upload"
**Then** they can select files: PDF, JPG, PNG, DOCX (whitelist validated)
**And** max 10MB per file, max 20 files per employee
**And** file > 10MB → `message.error("File vượt quá 10MB")`
**And** wrong type → `message.error("Chỉ chấp nhận PDF, JPG, PNG, DOCX")`

**Given** a file is uploaded
**When** saved
**Then** it is stored at `uploads/{user_id}/{employee_id}/{filename}` (outside webroot)
**And** duplicate filename → auto-renamed (append timestamp)
**And** document record created: file name, type, size, upload date, uploader

**Given** the document list
**When** displayed
**Then** it shows: file name, type icon, size, upload date, uploader
**And** actions: Download, Delete (soft delete — file remains on storage)

**Given** an Employee
**When** they view their own documents tab
**Then** they can view and download their documents but cannot delete

**Given** a user without permission
**When** they try to download another employee's document
**Then** access is denied (role-based check on download endpoint)

### Story 2.8: Employee Import from Excel

As an **HR Admin**,
I want to bulk import employees from an Excel file,
So that I can migrate existing data quickly when onboarding the company.

**Acceptance Criteria:**

**Given** the employee list toolbar
**When** Admin clicks "Import Excel"
**Then** a Modal opens with: download template link + file upload area

**Given** the Excel template
**When** downloaded
**Then** it contains columns: Họ tên*, CCCD*, Ngày sinh*, Giới tính*, Email, SĐT, Phòng ban*, Chức vụ*, Ngày vào làm*

**Given** a valid Excel file (≤500 rows)
**When** uploaded
**Then** system shows preview of data before import
**And** after confirmation: creates employees in batch
**And** result: "Thành công: {N}, Lỗi: {M}"

**Given** rows with errors (missing required field, duplicate CCCD, non-existent department)
**When** import processes
**Then** error rows are skipped without affecting valid rows
**And** detailed error list: row number + field + error message

**Given** an Excel file > 500 rows
**When** uploaded
**Then** rejected with message "Tối đa 500 dòng/lần import"

---

## Epic 3: Attendance Management

Employee check-in/out hàng ngày từ mạng công ty (IP whitelist). Hệ thống tự động đánh dấu đi muộn/về sớm. Admin cấu hình IP whitelist, xem bảng tổng hợp công tháng.

### Story 3.1: IP Whitelist Configuration

As an **HR Admin**,
I want to configure allowed IP addresses for attendance check-in,
So that only employees within the company network can record attendance.

**Acceptance Criteria:**

**Given** an Admin
**When** they navigate to Cấu hình → IP Whitelist
**Then** they see a list of whitelisted IPs for the system with add/delete actions

**Given** an Admin
**When** they add or remove an IP address
**Then** the change takes effect immediately

**Given** an empty whitelist
**When** any employee attempts to check-in
**Then** check-in is rejected (fail-safe: no whitelist = no check-in allowed)

**Given** the IP Whitelist management UI
**When** displayed
**Then** it uses NZ Modal for add (≤5 fields: IP address, description)
**And** data table with search and delete action

### Story 3.2: Employee Check-in & Check-out

As an **Employee**,
I want to check-in when I arrive and check-out when I leave,
So that my attendance is recorded automatically.

**Acceptance Criteria:**

**Given** an Employee on their Dashboard
**When** they click "Check-in"
**Then** the system validates their current IP against the system's whitelist
**And** if IP is valid: records check-in timestamp, shows `message.success("Check-in 8:05 — Đúng giờ")`
**And** the button changes to "Check-out"

**Given** an Employee whose IP is NOT in the whitelist
**When** they click "Check-in"
**Then** they see `message.error("Không thể check-in. Vui lòng kết nối mạng công ty.")`

**Given** an Employee who already checked in today
**When** they click "Check-in" again
**Then** the button is disabled with tooltip "Đã check-in lúc 8:05"

**Given** an Employee who hasn't checked in
**When** they click "Check-out"
**Then** it is rejected: "Chưa check-in hôm nay"

**Given** an Employee clicks "Check-out" after check-in
**When** the check-out is recorded
**Then** timestamp is saved, attendance record for today is complete
**And** the personal attendance table below updates with today's row: "8:05 | 17:30"

**Given** an Admin
**When** they need to correct an employee's attendance (forgot check-in/out)
**Then** Admin can manually add/edit attendance records for any employee in the system
**And** the correction is recorded (auditable)

### Story 3.3: Late Arrival & Early Departure Rules

As an **HR Admin**,
I want the system to automatically flag late arrivals and early departures,
So that attendance compliance is tracked without manual review.

**Acceptance Criteria:**

**Given** system working hours configured as 8:00-17:00 with 15-minute threshold
**When** an Employee checks in at 8:16
**Then** the record is flagged as "Đi muộn"

**Given** the same system config
**When** an Employee checks in at 8:14
**Then** the record is normal (within 15-minute grace period)

**Given** the same system config
**When** an Employee checks out at 16:44
**Then** the record is flagged as "Về sớm"

**Given** the same system config
**When** an Employee checks out at 16:46
**Then** the record is normal

### Story 3.4: Monthly Timesheet Summary

As an **HR Admin**,
I want to view a monthly attendance summary for all employees,
So that I can review work days, absences, and patterns before payroll.

**Acceptance Criteria:**

**Given** an Admin navigates to Chấm công
**When** they select a month
**Then** they see a summary table: each employee row with columns: Tổng ngày công, Đi muộn (count), Về sớm (count), Nghỉ phép (count), Vắng (count)
**And** data is scoped to the Admin's access

**Given** a Manager
**When** they view the attendance page
**Then** they only see employees in their department

**Given** an Employee
**When** they view their attendance page
**Then** they see only their personal attendance calendar/list for the month
**And** each day shows: check-in time, check-out time, status (normal/đi muộn/về sớm/nghỉ phép/vắng)

**Given** the timesheet data
**When** Admin clicks a specific employee row
**Then** they see daily attendance details for that month

**Given** the summary table
**When** displayed
**Then** it matches the premium data table pattern (radius 20px, header uppercase, row hover)
**And** status flags (đi muộn, về sớm) use semantic color badges

---

## Epic 4: Dynamic Approval Workflow Engine

Admin cấu hình chuỗi duyệt per-module, per-module qua Step Builder UI. Hệ thống có workflow mặc định (Employee → Manager → HR Admin) hoạt động ngay. Engine generic, V1 cho Leave, mở rộng V2.

### Story 4.1: Workflow Template CRUD — Backend

As an **HR Admin**,
I want to create and edit approval workflow templates for each module,
So that my company has customized approval chains that match our organizational structure.

**Acceptance Criteria:**

**Given** an Admin
**When** they POST to create a workflow template with: name, module (e.g., "LEAVE"), list of steps (each with order, approver type, approver role/user)
**Then** the template is saved per-module, per-module
**And** each step has: stepOrder, approverType (DIRECT_MANAGER, DEPARTMENT_MANAGER, ROLE, SPECIFIC_USER), approverRole or approverUserId

**Given** an Admin
**When** they edit an existing workflow template (add/remove/reorder steps)
**Then** changes only apply to NEW requests created after the save
**And** requests already in pipeline keep the old workflow version

**Given** a module
**When** checked
**Then** only one workflow template is active at a time per module

**Given** an Admin
**When** they delete a custom workflow
**Then** the system reverts to the default workflow for that module

### Story 4.2: Default Workflow & Fallback

As a **System**,
I want default approval workflows that work without configuration,
So that the system works immediately without custom configuration.

**Acceptance Criteria:**

**Given** a fresh system installation
**When** no custom workflow is configured for module "LEAVE"
**Then** the default workflow applies: Step 1 = Employee (fixed) → Step 2 = Manager → Step 3 = HR Admin

**Given** an Admin deletes their custom workflow for "LEAVE"
**When** a leave request is submitted
**Then** the default workflow (Employee → Manager → HR Admin) is used

**Given** an approver in a step whose account is disabled
**When** a request reaches that step
**Then** the request escalates to Admin with notification: "Người duyệt [Tên] không khả dụng. Đơn đã chuyển lên Admin."

### Story 4.3: Approval Pipeline Execution Engine

As the **System**,
I want to automatically create and execute approval pipelines when requests are submitted,
So that requests flow through the configured approval chain without manual routing.

**Acceptance Criteria:**

**Given** an Employee submits a request (e.g., leave request) with a configured workflow
**When** the request is created
**Then** the system creates an ApprovalPipeline with N ApprovalPipelineSteps matching the active template
**And** Step 1 (Employee/submitter) is auto-approved
**And** Step 2's approver receives a notification (event published)

**Given** an approver at Step N approves
**When** approval is recorded
**Then** Step N status → APPROVED, Step N+1 approver receives notification
**And** pipeline progresses sequentially

**Given** the last step approver approves
**When** the pipeline completes
**Then** pipeline status → APPROVED
**And** business logic triggers (e.g., deduct leave balance)
**And** the requester receives notification "Đơn đã được duyệt"

**Given** any approver rejects at Step N
**When** rejection is recorded with reason (required)
**Then** pipeline status → REJECTED, all subsequent steps cancelled
**And** requester receives notification: "Đơn bị từ chối bởi [Tên] tại bước [N]. Lý do: [...]"
**And** pipeline stops immediately — no further steps processed

**Given** an Employee views their request
**When** they check pipeline status
**Then** they see each step: which are APPROVED (✓), which is CURRENT (●), which are PENDING (○)

### Story 4.4: Step Builder UI — Workflow Configuration

As an **HR Admin**,
I want a visual Step Builder to configure approval workflows,
So that I can set up approval chains without technical knowledge.

**Acceptance Criteria:**

**Given** Admin navigates to Cấu hình → Quy trình duyệt
**When** the page loads
**Then** they see a list of workflow templates (per module) with edit action

**Given** Admin clicks edit on a workflow
**When** the Step Builder page loads
**Then** it shows 3 section cards (radius 20px):
- Section 1 — Thông tin workflow: 2-column form: Tên quy trình (input 44px) + Module (select 44px)
- Section 2 — Điều kiện áp dụng: Dynamic condition rows (field select + operator + value + delete X) + "Thêm điều kiện" dashed button. Empty = applies to all.
- Section 3 — Chuỗi phê duyệt: Vertical timeline with gradient line

**Given** Section 3 (Chuỗi phê duyệt)
**When** displayed
**Then** Step 1 "Người gửi đơn" is fixed (muted style, badge "Cố định", cannot delete/edit)
**And** Step 2+ cards have: 2-column form (Loại người duyệt select: Direct Manager / Department Manager / Role / Specific User + dynamic Vai trò/Người duyệt select), delete button
**And** timeline nodes are 44px, 16px radius, numbered
**And** "Thêm bước duyệt" dashed full-width button at bottom

**Given** the workflow has steps configured
**When** preview section renders
**Then** it shows chip pills on gradient bg: Employee (muted) → Step names (primary-soft)
**And** conditional steps show condition text below their chip

**Given** Admin clicks "Lưu quy trình"
**When** validation passes (at least 1 step after Employee)
**Then** workflow saves, toast: "Cập nhật quy trình thành công"

**Given** Admin tries to save with no approval steps (only Employee)
**When** they click save
**Then** `message.error("Cần ít nhất một bước duyệt")`

**Given** the footer
**When** displayed
**Then** it shows "Huỷ" (default button) + "Lưu quy trình" (primary button with shadow)

---

## Epic 5: Leave Management

Employee gửi đơn nghỉ phép (5 loại), xem pipeline duyệt real-time. Manager/Admin duyệt/từ chối qua Approval Inbox. Hệ thống quản lý số dư phép tự động (cấp đầu năm, pro-rata, trừ khi duyệt).

### Story 5.1: Leave Request Submission

As an **Employee**,
I want to submit leave requests with type and date range,
So that I can request time off through the system instead of paper/email.

**Acceptance Criteria:**

**Given** an Employee navigates to Nghỉ phép → "Tạo đơn"
**When** the Drawer (560px) opens
**Then** it shows: Loại phép* (Select: Phép năm, Nghỉ ốm, Không lương, Nghỉ cưới, Nghỉ tang), Ngày bắt đầu* (DatePicker), Ngày kết thúc* (DatePicker), Lý do (TextArea)
**And** inline display: "Phép năm còn: {X} ngày → sau đơn này: {Y} ngày"

**Given** an Employee submits leave when phép năm balance = 0
**When** they select "Phép năm"
**Then** warning: "Phép năm đã hết. Chuyển sang Nghỉ không lương?"
**And** if confirmed, leave type changes to "Không lương"

**Given** ngày bắt đầu < today
**When** submitted
**Then** validation error: "Ngày bắt đầu không được nhỏ hơn ngày hiện tại"

**Given** a valid leave request is submitted
**When** saved
**Then** system looks up active workflow template for module "LEAVE" in the system
**And** creates approval pipeline per Epic 4 engine
**And** toast: "Đơn nghỉ phép đã gửi"
**And** step 1 approver receives notification (event published)

**Given** Employee views "Đơn của tôi" tab
**When** displayed
**Then** each request shows: loại phép (badge), ngày, trạng thái (pill: Chờ duyệt/Đã duyệt/Từ chối), pipeline indicator (steps with ✓/●/○)

### Story 5.2: Approval Inbox — Review & Decide

As a **Manager or HR Admin**,
I want to review and approve/reject leave requests from my approval queue,
So that I can process requests efficiently without delays.

**Acceptance Criteria:**

**Given** a Manager/Admin navigates to Phê duyệt
**When** the page loads
**Then** they see pill tabs (999px radius, 40px height): "Chờ duyệt" (default, active=primary-soft), "Đã duyệt", "Từ chối", "Tất cả"
**And** data table with columns: Người gửi (gradient avatar + tên bold + phòng ban faint), Loại đơn (pill badge), Nội dung, Ngày gửi, Pipeline dots, Actions

**Given** pipeline dots column
**When** rendered for each request
**Then** Done steps = success-soft bg + checkmark, Current step = primary-soft bg + ring shadow, Pending = gray bg
**And** labels 12px/600, current step = primary color text (UX-DR22)

**Given** an approver clicks "Duyệt" on a request
**When** Popconfirm "Xác nhận duyệt?" is confirmed
**Then** the pipeline step is approved via Epic 4 engine
**And** next step approver is notified
**And** the request moves out of "Chờ duyệt" tab

**Given** an approver clicks "Từ chối"
**When** Modal opens with textarea for reason (required)
**Then** after submit: pipeline is rejected, requester is notified with reason
**And** the request appears in "Từ chối" tab

**Given** an approver only sees requests at their current step
**When** they view "Chờ duyệt"
**Then** only requests where THEY are the current step approver are shown
**And** within their role/department scope

**Given** bulk selection
**When** Admin checks multiple requests
**Then** bulk bar: "Đã chọn {N} đơn" + "Duyệt tất cả" button (no bulk reject)

### Story 5.3: Leave Balance Management

As an **HR Admin**,
I want the system to automatically manage leave balances,
So that leave entitlements are tracked accurately without manual calculation.

**Acceptance Criteria:**

**Given** a new year begins
**When** LeaveBalanceResetJob runs (scheduled annually)
**Then** all employees in all employees receive default annual leave: 12 days (or system-configured amount)
**And** previous year balances reset (unless carry-over is enabled in system config)

**Given** an employee who joined mid-year
**When** their leave balance is initialized
**Then** annual leave is calculated pro-rata based on remaining months

**Given** system config has seniority bonus enabled
**When** an employee has 5+ years tenure
**Then** they receive +1 day per 5 years of seniority

**Given** a leave request is fully approved (pipeline complete)
**When** business logic triggers
**Then** the employee's leave balance for that leave type is deducted by the number of days
**And** balance cannot go negative for "Phép năm" (blocked at submission)

**Given** an Admin
**When** they view leave balances
**Then** they see per-employee: loại phép, tổng cấp, đã dùng, còn lại
**And** Admin can manually adjust balances with reason (auditable)

### Story 5.4: Employee Leave History & Calendar

As an **Employee**,
I want to view my leave history and remaining balance,
So that I can plan my time off and track my requests.

**Acceptance Criteria:**

**Given** an Employee navigates to Nghỉ phép
**When** the page loads
**Then** they see: balance summary cards (Phép năm: X/12 còn, Nghỉ ốm: Y used) + "Tạo đơn" button + request history table

**Given** the request history table
**When** displayed
**Then** columns: Loại phép (badge), Ngày BĐ-KT, Số ngày, Trạng thái (pill), Pipeline progress
**And** click row → detail view with full pipeline steps and reject reason (if any)

**Given** an Admin/Manager views Leave management
**When** they navigate to Nghỉ phép
**Then** they see all leave requests in their scope (Admin: all, Manager: department)
**And** leave calendar view showing who is off on which day (FR-34 partial)

---

## Epic 6: Payroll

Admin cấu hình BHXH/BHYT/BHTN rates, biểu thuế TNCN 7 bậc, phụ cấp linh hoạt, OT. Tạo bảng lương tháng auto tính gross→net cho toàn bộ. NV xem phiếu lương chi tiết. Xuất Excel.

### Story 6.1: Payroll Configuration — Insurance & Tax

As an **HR Admin**,
I want to configure insurance rates and tax brackets for my company,
So that payroll calculations comply with current Vietnamese regulations.

**Acceptance Criteria:**

**Given** an Admin navigates to Cấu hình → Thông số lương
**When** the page loads
**Then** they see configurable fields per-module:
- BHXH: NLĐ 8%, DN 17.5% (defaults)
- BHYT: NLĐ 1.5%, DN 3%
- BHTN: NLĐ 1%, DN 1%
- Giảm trừ bản thân: 11,000,000 VND/tháng
- Giảm trừ người phụ thuộc: 4,400,000 VND/người/tháng
- Lương tối thiểu vùng (for insurance cap)
- Biểu thuế TNCN 7 bậc (editable table)

**Given** an Admin changes BHXH rate from 8% to 8.5%
**When** saved
**Then** changes apply to payroll generated from NEXT month onward
**And** applies from next month onward

**Given** the tax bracket table
**When** displayed
**Then** it shows 7 progressive brackets with: từ (VND), đến (VND), thuế suất (%)
**And** Admin can edit rates (for future regulation changes)

### Story 6.2: Allowance Configuration & Assignment

As an **HR Admin**,
I want to create flexible allowance types and assign them to employees,
So that compensation packages reflect each employee's role and benefits.

**Acceptance Criteria:**

**Given** an Admin navigates to Cấu hình → Phụ cấp
**When** they create an allowance type
**Then** they specify: name (e.g., "Ăn trưa"), type (fixed amount OR % of base salary), amount/percentage

**Given** an allowance "Ăn trưa" = 800,000 VND fixed
**When** assigned to all employees (company-wide)
**Then** every employee's payslip includes this allowance line

**Given** an allowance "Trách nhiệm" = 10% of base salary
**When** assigned to Position = "Manager"
**Then** only employees with that position receive this allowance
**And** amount calculated per employee based on their base salary

**Given** the allowance assignment
**When** Admin assigns allowances
**Then** they can assign by: individual employee, department, or position
**And** employee-level assignments override department/position-level

### Story 6.3: Overtime Recording

As an **HR Admin**,
I want to record overtime hours for employees,
So that OT compensation is calculated correctly in payroll.

**Acceptance Criteria:**

**Given** an Admin
**When** they record OT for an employee
**Then** they specify: employee, date, hours, OT type (weekday/weekend/holiday)

**Given** OT type = weekday
**When** payroll calculates
**Then** OT amount = hours × (base salary / standard work days / 8) × 1.5

**Given** OT type = weekend
**When** payroll calculates
**Then** coefficient = 2.0x

**Given** OT type = holiday
**When** payroll calculates
**Then** coefficient = 3.0x

**Given** standard work days
**When** used in calculation
**Then** it uses system-configured value (default 22 days/month)

**Given** the OT management UI
**When** displayed
**Then** Admin sees monthly OT table per employee with: date, hours, type, calculated amount
**And** can add/edit OT entries

### Story 6.4: Monthly Payroll Generation

As an **HR Admin**,
I want to generate a monthly payroll that automatically calculates gross-to-net for all employees,
So that I can process salaries in hours instead of days.

**Acceptance Criteria:**

**Given** Admin navigates to Tiền lương → "Tạo bảng lương"
**When** they select month (e.g., Tháng 6/2026) and confirm
**Then** system calculates for each active employee with a contract:
- Lương cơ bản × (ngày công thực tế / ngày công chuẩn)
- \+ Phụ cấp (sum of assigned allowances)
- \+ OT (sum of OT amounts for the month)
- \- BHXH (employee portion)
- \- BHYT (employee portion)
- \- BHTN (employee portion)
- \- Thuế TNCN (progressive 7-bracket on taxable income)
- = Lương Net
**And** calculation completes within 30 seconds for 500 employees (NFR-9)

**Given** the payroll is generated
**When** displayed
**Then** status = "Nháp" (Tag), full page table: NV name, ngày công, lương cơ bản, phụ cấp, OT, BHXH, BHYT, BHTN, thuế TNCN, lương net
**And** Admin can review and make corrections

**Given** Admin clicks "Tính lại"
**When** triggered
**Then** payroll recalculates (e.g., after attendance correction)

**Given** a month that already has a confirmed payroll
**When** Admin tries to create another
**Then** rejected: "Bảng lương tháng này đã được xác nhận"

### Story 6.5: Payroll Confirmation & Excel Export

As an **HR Admin**,
I want to confirm the payroll and export it to Excel,
So that finalized salary data is locked and available for accounting.

**Acceptance Criteria:**

**Given** Admin reviews the draft payroll
**When** they click "Xác nhận bảng lương"
**Then** Modal.confirm: "Xác nhận bảng lương tháng 6/2026? Sau khi xác nhận không thể chỉnh sửa."
**And** after confirm: status → "Đã xác nhận" (green pill), table becomes read-only

**Given** a confirmed payroll
**When** confirmed
**Then** system publishes PayrollConfirmedEvent
**And** each employee receives payslip notification (event for Epic 7)

**Given** a confirmed payroll
**When** Admin clicks "Xuất Excel"
**Then** file `bang_luong_2026-06.xlsx` downloads within 10 seconds for 500 rows (NFR-10)
**And** contains all columns matching the payroll table

**Given** a confirmed payroll
**When** Admin tries to edit
**Then** all edit actions are disabled, only "Xuất Excel" is available

### Story 6.6: Employee Payslip View

As an **Employee**,
I want to view my monthly payslip,
So that I understand my salary breakdown without asking HR.

**Acceptance Criteria:**

**Given** an Employee navigates to Phiếu lương
**When** the page loads
**Then** they see a list of monthly payslips (only confirmed months)
**And** each shows: month, lương net (formatted VND), status badge

**Given** an Employee clicks on a payslip
**When** detail view opens
**Then** it shows breakdown: lương cơ bản, phụ cấp (each line item), OT, BHXH, BHYT, BHTN, thuế TNCN, lương net
**And** all amounts formatted with `Intl.NumberFormat('vi-VN')` + ₫

**Given** the tax calculation display
**When** shown
**Then** Thu nhập chịu thuế = Gross - Giảm trừ bản thân - Giảm trừ NPT - BH bắt buộc
**And** if Thu nhập chịu thuế ≤ 0, thuế = 0

**Given** an Employee
**When** they view payslips
**Then** they only see their own payslips, never other employees'

---

## Epic 7: Notifications

User nhận thông báo in-app (polling) + email SMTP cho mọi sự kiện nghiệp vụ. Badge count, dropdown 360px, full page. Không WebSocket V1.

### Story 7.1: Notification Infrastructure & Event Listeners

As the **System**,
I want to listen for business events and create notifications,
So that users are informed of important actions across all modules.

**Acceptance Criteria:**

**Given** a business event is published (LeaveRequestCreatedEvent, LeaveApprovedEvent, LeaveRejectedEvent, ContractExpiryEvent, PayrollConfirmedEvent, AccountCreatedEvent)
**When** the @EventListener in NotificationService receives it
**Then** a Notification record is created: recipientUserId, userId, title, content, resourceType, resourceId, read=false, createdAt

**Given** a notification is created
**When** the @Async email listener processes it
**Then** an email is sent via SMTP to the recipient's email
**And** email contains: subject, body with event details, link to resource
**And** SMTP config is shared system-wide (V1)

**Given** email sending fails
**When** SMTP is unavailable
**Then** the in-app notification is still created (email failure doesn't block)
**And** error is logged server-side

**Given** the event mapping
**When** events are published
**Then** correct recipients receive notifications:
- Leave request created → step 1 approver
- Leave approved at step N → step N+1 approver (or requester if final)
- Leave rejected → requester
- Contract expiry (30 days) → Admin(s)
- Payroll confirmed → each employee (payslip ready)
- Account created → new user (credentials)

### Story 7.2: In-App Notification UI — Dropdown & Badge

As a **User**,
I want to see my notifications in a dropdown from the top bar,
So that I stay informed without navigating away from my current page.

**Acceptance Criteria:**

**Given** the notification bell icon in the topbar
**When** there are unread notifications
**Then** a red badge shows the unread count (min-width 22px, 9999px radius, white text on #EF4444)
**And** badge count updates via polling every 30 seconds

**Given** a user clicks the bell icon
**When** the dropdown opens (360px width)
**Then** it shows notifications sorted by newest first
**And** each item: icon (by type) + content (1-2 lines) + relative time ("5 phút trước", "Hôm qua 14:30")
**And** unread items have background primary-bg (#EFF6FF)
**And** footer: "Xem tất cả thông báo" link

**Given** a user clicks a notification in the dropdown
**When** clicked
**Then** the notification is marked as read (background changes to white)
**And** user navigates to the related resource (e.g., leave request detail, payslip)

**Given** accessibility
**When** badge count updates
**Then** `aria-live="polite"` announces the change to screen readers

### Story 7.3: Full Notification Page

As a **User**,
I want a full page listing all my notifications,
So that I can review my complete notification history.

**Acceptance Criteria:**

**Given** a user clicks "Xem tất cả thông báo" or navigates to notification page
**When** the page loads
**Then** it shows all notifications in a data table/list: icon, content, time, read/unread status
**And** paginated (default 20)
**And** scoped to current user

**Given** the notification list
**When** displayed
**Then** unread items are visually distinct (primary-bg background or bold text)
**And** "Đánh dấu tất cả đã đọc" action available

**Given** a user clicks a notification
**When** clicked
**Then** marked as read + navigates to resource

---

## Epic 8: Audit Log

Mọi thay đổi dữ liệu tự động ghi nhận via AOP. Admin tra cứu audit log + login history. Immutable records (NFR-20).

### Story 8.1: Automatic Audit Logging via AOP

As the **System**,
I want all data changes automatically recorded via AOP,
So that every CREATE, UPDATE, DELETE operation has an immutable audit trail.

**Acceptance Criteria:**

**Given** any @Service method that performs a CREATE operation
**When** the AuditLogAspect intercepts it
**Then** an AuditLog record is created: userId, userId, timestamp, entityType, entityId, action=CREATE, newValue (JSON snapshot)
**And** the record is immutable (no UPDATE/DELETE API for audit_logs)

**Given** an UPDATE operation on any business entity
**When** intercepted
**Then** AuditLog records: oldValue (JSON) + newValue (JSON) showing exactly what changed

**Given** a soft DELETE operation
**When** intercepted
**Then** AuditLog records: action=DELETE, oldValue (entity state before deletion)

**Given** the audit_logs table
**When** any API request attempts to modify or delete audit records
**Then** the request is rejected (no PUT/DELETE endpoints exist for audit_logs)

**Given** the AOP aspect
**When** deployed to a system with existing modules (Epics 2-6)
**Then** it retroactively captures all CUD operations from that point forward
**And** no changes needed in existing service code

### Story 8.2: Login History Recording

As the **System**,
I want all login attempts recorded with details,
So that security events can be investigated.

**Acceptance Criteria:**

**Given** a successful login
**When** JWT is issued
**Then** a login history record is created: userId, userId, timestamp, IP address, userAgent, status=SUCCESS

**Given** a failed login (wrong password)
**When** authentication fails
**Then** a login history record is created: username (attempted), timestamp, IP, userAgent, status=FAILED, reason="Sai mật khẩu"

**Given** a failed login (account locked)
**When** authentication is blocked
**Then** login history: status=FAILED, reason="Tài khoản bị khoá"

**Given** login history records
**When** stored
**Then** they are immutable (same as audit logs — no UPDATE/DELETE)

### Story 8.3: Audit Log Viewer & Login History UI

As an **HR Admin**,
I want to search and filter audit logs and login history,
So that I can investigate data changes and security events.

**Acceptance Criteria:**

**Given** an Admin navigates to Audit Log
**When** the page loads
**Then** they see a data table with filters: Module (select: Employee, Contract, Payroll, Leave...), User (select), Nhân viên liên quan, Khoảng thời gian (DateRange), Action (CREATE/UPDATE/DELETE)
**And** results sorted by newest first, paginated

**Given** an Admin applies filters
**When** results display
**Then** each row shows: timestamp, user (who made the change), module, entity, action, summary of change
**And** click row → detail view with full oldValue/newValue JSON diff

**Given** an Admin views Login History tab
**When** displayed
**Then** table: user, timestamp, IP, status (SUCCESS green / FAILED red badge), reason (if failed)
**And** filter by user, time range, status

**Given** audit log data
**When** queried
**Then** all audit records are returned for the Admin

---

## Epic 9: Dashboard & Reports

Admin/Manager thấy KPI ngay khi đăng nhập. Quick actions. Stat cards + sparkline. 3 báo cáo (nhân viên, chấm công, bảng lương) + xuất Excel.

### Story 9.1: HR Dashboard — KPI Stats & Quick Actions

As an **HR Admin or Manager**,
I want to see key HR metrics immediately when I log in,
So that I know what needs attention today.

**Acceptance Criteria:**

**Given** an Admin lands on Dashboard
**When** the page loads
**Then** they see:
- Page header: "Xin chào, {name}" (32px/800) + subtitle with date
- Quick action cards (4-column grid, glass-morphism: rgba(255,255,255,0.76) + backdrop-filter blur(10px), 18px radius): Thêm nhân viên, Tạo bảng lương, Duyệt đơn ({count}), Xuất báo cáo
- Stat cards (4-column): Tổng nhân viên (blue, sparkline), Nhân viên mới tháng (green, sparkline), HĐ sắp hết hạn (orange), Đơn chờ duyệt (red)

**Given** each stat card
**When** rendered
**Then** it follows UX-DR6: icon 48px/16px radius/tinted bg + sparkline SVG 74x28px right, value 36px/800/-0.05em, label 13px/600/muted, decorative gradient circle 120px top-right
**And** hover: translateY(-3px) + shadow-hover
**And** click navigates to corresponding module

**Given** a Manager
**When** they view the Dashboard
**Then** stats are scoped to their department (employee count in dept, leave requests in dept)

**Given** an Employee
**When** they view the Dashboard
**Then** they see personal stats: Ngày công tháng này, Phép còn lại, Check-in button (prominent)

### Story 9.2: Contract Deadlines & Activity Timeline

As an **HR Admin**,
I want to see contract deadlines and recent activity on the dashboard,
So that I can prioritize urgent tasks.

**Acceptance Criteria:**

**Given** the Dashboard (below stat cards)
**When** "Hợp đồng sắp hết hạn" section renders
**Then** it shows a table within a card (radius 20px): employee name, contract type, end date, "X ngày" with progress bar (UX-DR15)
**And** sorted by urgency (fewest days first)
**And** "Xem tất cả" link to contract list filtered by expiring

**Given** the "Hoạt động gần đây" section
**When** rendered
**Then** it shows an activity timeline (UX-DR16): vertical dots (10px, semantic color + 5px ring shadow)
**And** each item: action text (14px/700) + relative time (12px faint)
**And** colors: blue (default), amber (warning/pending), green (approved/success)
**And** shows latest 5-8 activities

### Story 9.3: Payroll Dashboard Card

As an **HR Admin**,
I want to see payroll summary on the dashboard,
So that I can track salary costs at a glance.

**Acceptance Criteria:**

**Given** the Dashboard
**When** "Chi phí lương" card renders
**Then** it shows: title "Chi phí lương" + "Xem chi tiết" link, currency value (32px/850/-0.05em formatted VND), trend text vs previous month (↑/↓ % colored)
**And** SVG area chart (gradient fill + stroke line) showing last 6 months
**And** status badge "Đã xác nhận" (green pill) if latest month is confirmed

**Given** no confirmed payroll exists
**When** the card renders
**Then** it shows "Chưa có dữ liệu" with prompt to create payroll

### Story 9.4: Leave Dashboard

As an **HR Admin or Manager**,
I want to see leave overview on the dashboard,
So that I can monitor team absences.

**Acceptance Criteria:**

**Given** an Admin views the Dashboard
**When** leave section renders
**Then** it shows: tổng ngày nghỉ tháng này, phân bổ theo loại phép (mini bar or breakdown), phòng ban nghỉ nhiều nhất

**Given** a Manager
**When** they view leave stats
**Then** data is scoped to their department only

**Given** a leave calendar widget (optional)
**When** rendered
**Then** it shows which employees are off today/this week
**And** color-coded by leave type

### Story 9.5: Employee Report with Export

As an **HR Admin**,
I want to generate an employee report with filters and export to Excel,
So that I can produce personnel reports for management.

**Acceptance Criteria:**

**Given** Admin navigates to Báo cáo → Danh sách nhân viên
**When** the page loads
**Then** they see filters: Phòng ban (Select), Trạng thái (Active/Inactive), Loại HĐ (Select)
**And** preview table with employee data matching filters

**Given** Admin applies filters and clicks "Xuất Excel"
**When** export runs
**Then** file `nhan_vien_{date}.xlsx` downloads within 10 seconds
**And** contains all columns displayed on screen, filtered results only (not full dataset)

### Story 9.6: Attendance & Payroll Reports with Export

As an **HR Admin**,
I want monthly attendance and payroll reports with Excel export,
So that I can provide data for management review and accounting.

**Acceptance Criteria:**

**Given** Admin navigates to Báo cáo → Tổng hợp công tháng
**When** they select a month
**Then** preview table: each employee row with ngày công, đi muộn, về sớm, nghỉ phép, vắng
**And** totals row at bottom
**And** "Xuất Excel" → `cham_cong_{yyyy-mm}.xlsx`

**Given** Admin navigates to Báo cáo → Bảng lương tháng
**When** they select a month (only months with confirmed payroll)
**Then** preview table: each employee row with gross → deductions → net
**And** tổng cột lương net = tổng chi phí lương
**And** "Xuất Excel" → `bang_luong_{yyyy-mm}.xlsx` format phù hợp kế toán

**Given** any report export
**When** generated for 500 rows
**Then** completes within 10 seconds (NFR-10)

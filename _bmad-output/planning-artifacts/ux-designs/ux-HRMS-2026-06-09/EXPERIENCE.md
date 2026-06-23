---
name: HRMS
status: final
sources:
  - {planning_artifacts}/prds/prd-HRMS-2026-06-09/prd.md
  - {planning_artifacts}/architecture.md
updated: 2026-06-20
---

# HRMS — Experience Spine

## Foundation

Desktop-first responsive web. NG-ZORRO (Ant Design for Angular) trên Angular 17.x + TypeScript. UI library: `ng-zorro-antd` — kế thừa 100% component defaults. `DESIGN.md` là visual identity reference — định nghĩa brand-layer delta (dark sidebar, semantic color mapping, composite components) theo hướng **Modern Minimalist** (cảm hứng Linear, Notion, Vercel, Stripe). Experience spine này owns *cách hệ thống hoạt động*.

Hệ thống nội bộ cho một công ty duy nhất — không phải SaaS, không multi-tenant. User có đúng một userRole. Ba vai trò: Admin/HR Admin (toàn hệ thống), Manager (phòng ban), Employee (cá nhân).

Ngôn ngữ giao diện: **Tiếng Việt** duy nhất trong V1.

→ Visual reference: `mockups/key-dashboard.html`, `mockups/key-employee-list.html`, `mockups/key-approval-inbox.html`, `mockups/key-workflow-builder.html`. Spines win on conflict.

## Information Architecture

### Sidebar Navigation

| Menu Item | Sub-items | Vai trò | Surface |
|-----------|-----------|---------|---------|
| Dashboard | — | All | `dashboard` |
| Nhân sự | Nhân viên, Phòng ban, Chức vụ | Admin, Manager (read) | `employee-list`, `department-list`, `position-list` |
| Hợp đồng | — | Admin | `contract-list` |
| Chấm công | — | All (scope theo userRole) | `attendance` |
| Nghỉ phép | — | All (scope theo userRole) | `leave` |
| Tiền lương | — | Admin | `payroll` |
| Phê duyệt | — | Admin, Manager | `approval-inbox` |
| Báo cáo | — | Admin | `report` |
| Cấu hình | Thông số lương, Phụ cấp, IP Whitelist, Quy trình duyệt | Admin | `config-*` |

**Role-based sidebar visibility:**
- **Admin (HR Admin):** Tất cả menu items.
- **Manager:** Dashboard, Nhân sự (phòng ban mình, read-only), Chấm công (phòng ban), Nghỉ phép (phòng ban), Phê duyệt.
- **Employee:** Dashboard, Chấm công (cá nhân), Nghỉ phép (cá nhân), Phiếu lương (cá nhân).

Employee không thấy sidebar item nào ngoài scope — không hiện rồi disable, mà **ẩn hoàn toàn**.

### Topbar

```
[Brand mark] [Breadcrumb]                    [🔍 Search] [🔔 Badge] [Avatar ▾]
```

- **Brand mark:** Logo công ty. Click → Dashboard.
- **Breadcrumb:** NZ Breadcrumb — `Dashboard / Nhân sự / Nhân viên / Nguyễn Văn A`. Luôn hiển thị path từ root.
- **Search:** Global search — NZ `nz-input` with search. Tìm nhân viên theo tên/mã. Kết quả dropdown.
- **Notification Bell:** NZ `nz-badge` + `Bell` icon. Badge count = số thông báo chưa đọc. Click → Dropdown panel danh sách thông báo, phân trang, polling 30s (FR-25).
- **Avatar:** Click → Dropdown menu: Hồ sơ cá nhân, Đổi mật khẩu, Đăng xuất.

### Full Surface Map

| Surface | URL Pattern | Content | Role |
|---------|-------------|---------|------|
| Login | `/login` | Form đăng nhập (username + password) | Public |
| Dashboard | `/dashboard` | Stat cards + alerts (userRole-scoped) | All |
| Employee List | `/employees` | ProTable: tìm, lọc, CRUD, export/import | Admin; Manager (read) |
| Employee Detail | `/employees/:id` | Tabs: Thông tin, Hợp đồng, Chấm công, Nghỉ phép, Lương, Tài liệu | Admin; Manager (read phòng ban); Employee (self) |
| Department List | `/departments` | ProTable: CRUD phòng ban | Admin |
| Position List | `/positions` | ProTable: CRUD chức vụ | Admin |
| Contract List | `/contracts` | ProTable: CRUD hợp đồng, filter hết hạn | Admin |
| Attendance | `/attendance` | Employee: Check-in/out + lịch sử. Admin: Bảng tổng hợp công tháng | All (scoped) |
| Leave | `/leave` | Employee: Tạo đơn + lịch sử. Admin: Quản lý phép, số dư | All (scoped) |
| Payroll | `/payroll` | Danh sách bảng lương tháng, tạo/review/confirm | Admin |
| Payroll Detail | `/payroll/:id` | Bảng lương chi tiết từng nhân viên | Admin |
| Payslip | `/payslip` | Employee xem phiếu lương cá nhân | Employee |
| Approval Inbox | `/approvals` | Danh sách đơn chờ duyệt + lịch sử duyệt | Admin, Manager |
| Reports | `/reports` | 3 loại báo cáo + export Excel | Admin |
| Audit Log | `/audit-log` | Tra cứu log: filter module/user/time/action | Admin |
| Config - Payroll | `/config/payroll` | BHXH/BHYT/BHTN rates, biểu thuế, lương tối thiểu | Admin |
| Config - Allowance | `/config/allowance` | CRUD loại phụ cấp | Admin |
| Config - IP Whitelist | `/config/ip-whitelist` | CRUD IP cho phép chấm công | Admin |
| Config - Workflow | `/config/workflow` | Step Builder: cấu hình chuỗi duyệt per-module | Admin |
| Profile | `/profile` | Thông tin cá nhân, đổi mật khẩu | All |
| 403 | — | Không có quyền truy cập | All |
| 404 | — | Trang không tồn tại | All |

## Voice and Tone

Microcopy. Brand voice và aesthetic posture nằm ở `DESIGN.md.Brand & Style`.

| Do | Don't |
|---|---|
| "Thêm nhân viên" | "Tạo mới hồ sơ nhân viên vào hệ thống" |
| "Chưa có dữ liệu" | "Không có kết quả nào được tìm thấy!" |
| "Đã duyệt" | "Đơn của bạn đã được duyệt thành công ✅" |
| "Xác nhận xoá?" | "Bạn có chắc chắn muốn xoá không? Hành động này không thể hoàn tác!" |
| "3 hợp đồng sắp hết hạn" | "Cảnh báo: Có 3 hợp đồng sẽ hết hạn trong 30 ngày tới ⚠️" |
| "Lưu thành công" | "Dữ liệu đã được lưu thành công vào hệ thống!" |
| "Lỗi kết nối. Thử lại sau." | "Đã xảy ra lỗi không mong muốn. Vui lòng liên hệ quản trị viên!" |

**Quy tắc chung:**
- Ngắn gọn. Bỏ "vui lòng", "bạn", "thành công" khi thừa.
- Không emoji trong app. Không "!" cho thông báo bình thường.
- Verb trước: "Thêm nhân viên", "Xuất Excel", "Tạo bảng lương".
- Số liệu cụ thể hơn mô tả chung: "3 đơn chờ duyệt" tốt hơn "Có đơn chờ duyệt".
- Error message nói rõ vấn đề + gợi ý hành động: "Email đã tồn tại. Kiểm tra lại hoặc dùng email khác."

## Component Patterns

Behavioral. Visual specs nằm ở `DESIGN.md.Components` hoặc NG-ZORRO defaults.

### Data Table (ProTable)

Trọng tâm UX — 90% thời gian HR dùng hệ thống là nhìn bảng.

| Tính năng | Hành vi |
|-----------|---------|
| Search | Input.Search ở toolbar, debounce 300ms, partial match không phân biệt hoa thường |
| Filter | Select inline trên toolbar (Department, Status...). Active filters hiện dạng Tag row bên dưới, xoá từng cái hoặc "Xoá tất cả" |
| Sort | Click header column → asc/desc/none. Một column sort tại một thời điểm |
| Pagination | NZ Pagination dưới bảng. Default 20 rows/page, options: 10/20/50/100. Hiển thị "Tổng: 118 nhân viên" |
| Row click | Click row → navigate đến detail page (Employee, Contract). Không phải click icon |
| Bulk select | Checkbox column đầu tiên. Chọn → action bar slide lên: "Đã chọn 5 — [Xuất Excel] [Xoá]" |
| Export Excel | Button "Xuất Excel" trên toolbar → export filtered results (không phải toàn bộ). Tên file: `{module}_{date}.xlsx` |
| Import Excel | Button "Import Excel" → Modal: upload file + preview dữ liệu + báo lỗi từng dòng (FR-38) |
| Column settings | Icon ⚙️ → Popover checkbox list: ẩn/hiện cột. Lưu preference vào localStorage. `[ASSUMPTION]` |
| Empty state | NZ `nz-empty` + một câu + button action chính. Ví dụ: "Chưa có nhân viên. [Thêm nhân viên]" |
| Loading | NZ `nz-skeleton` rows (5 dòng) khớp layout bảng. Không spinner toàn trang |

### Form Patterns

| Loại form | Pattern | Dùng cho |
|-----------|---------|---------|
| Simple CRUD (≤5 fields) | NZ `nz-modal` | Department, Position, Leave Type, IP Whitelist, Allowance Type |
| Medium CRUD (6-15 fields) | NZ `nz-drawer` (width 560px) `[ASSUMPTION]` | Employee create/edit, Contract create/edit, Leave request |
| Complex operation | Full page | Payroll run (review bảng lương), Reports (filter + preview) |
| Confirmation | NZ `nz-popconfirm` | Delete, Approve |
| Confirmation + input | NZ `nz-modal` | Reject (nhập lý do), Password reset |

**Form behavior chung:**
- Validation: inline, real-time (Angular Reactive Forms + NZ Form rules). Hiện error message dưới field khi blur hoặc submit.
- Required fields: label có dấu `*` đỏ (NG-ZORRO default).
- Submit: Button primary ở footer. Loading state khi đang gửi — disable button, show spinner.
- Success: NzMessageService.success("Lưu thành công") — toast 3 giây, auto dismiss.
- Error: NzMessageService.error("Lỗi: {message}"). Server validation errors → hiện inline tại field tương ứng.
- Cancel: Close modal/drawer. Nếu có thay đổi → Popconfirm "Huỷ thay đổi?".

### Stat Card (Dashboard)

- Click → navigate đến module tương ứng (e.g., click "Hợp đồng sắp hết hạn" → Contract list filtered by expiring).
- Số liệu lớn (H4 20px 600 weight), label nhỏ (caption 12px), icon bên trái (48px, background tròn dùng semantic color nhạt).
- Trend indicator: mũi tên lên/xuống + % thay đổi so với tháng trước, success/danger color. Chỉ áp dụng cho payroll stats.
- Cards dùng subtle elevation (xem `DESIGN.md.Components.card` cho shadow values).

### Step Builder (Workflow Config)

Full page layout, chia 3 section card tách biệt:

**Section 1 — Thông tin workflow:** Card đầu tiên. 2-column form: Tên quy trình (input) + Module (select: Nghỉ phép, OT...). Thông tin cơ bản, không lẫn với logic.

**Section 2 — Điều kiện áp dụng:** Card riêng. Điều kiện nằm ở đây — KHÔNG nằm trong từng step. Mỗi điều kiện là một row: Field select (leave_days, department...) + Operator select (>=, <=, =) + Value input. "Thêm điều kiện" button dashed. Xoá từng điều kiện bằng icon X. Bỏ trống = áp dụng cho tất cả.

**Section 3 — Chuỗi phê duyệt:** Card chính, chứa:
- **Vertical timeline:** Steps xếp dọc, nối đường line dọc 2px. Mỗi step là một node (số thứ tự, rounded square) + card ngang.
- **Step 1 cố định:** "Người gửi đơn" — muted style, badge "Cố định", không xoá/edit được.
- **Step 2+:** Mỗi step card có 2-column form: Loại người duyệt (select: Direct Manager, Department Manager, Role, Specific User) + Vai trò/Người duyệt (select dynamic theo loại). Nút xoá step ở góc phải.
- **"Thêm bước duyệt":** Button dashed full-width cuối timeline.
- **Preview pipeline:** Strip ngang cuối section — chip pills: `Employee → Team Lead → Manager → HR Admin`. Chip Employee dùng muted bg, chip step dùng primary-bg. Điều kiện hiện text nhỏ dưới chip có condition ("khi >= 3 ngày").
- **Footer:** Button "Huỷ" (default) + "Lưu quy trình" (primary).

### Notification Dropdown

- Trigger: click Bell icon trên Topbar.
- Panel: NZ Dropdown (width 360px) `[ASSUMPTION]` — danh sách thông báo, mới nhất trên cùng.
- Mỗi item: Icon (loại thông báo) + Nội dung (1-2 dòng) + Thời gian ("5 phút trước", "Hôm qua 14:30").
- Unread: background nhạt `{colors.primary-bg}`. Click → mark as read + navigate đến resource.
- Footer: "Xem tất cả thông báo" → full notification page.
- Polling 30s — badge count cập nhật tự động. Không real-time push V1.

### Approval Inbox

- Default view: Table layout (không phải card layout) — phù hợp bulk review.
- Columns: Người gửi (Avatar + tên), Loại đơn (Tag), Nội dung tóm tắt, Ngày gửi, Bước hiện tại, Trạng thái, Actions.
- Tab filter: "Chờ duyệt" (default) | "Đã duyệt" | "Đã từ chối" | "Tất cả".
- Quick actions trong row: Button "Duyệt" (primary, small) + "Từ chối" (danger, small).
- Duyệt: Popconfirm "Xác nhận duyệt?".
- Từ chối: Modal nhập lý do (textarea, required) → confirm.
- Bulk approve: chọn nhiều row → "Duyệt tất cả" (chỉ cho Approve, không bulk reject).

## State Patterns

| State | Surface | Treatment |
|-------|---------|-----------|
| Initial load | Mọi page | NZ `nz-skeleton` khớp layout expected. Table: 5 skeleton rows. Dashboard: skeleton cards. Resolves khi data load xong |
| Empty — chưa có data | List pages | NZ `nz-empty` image + text ngắn + button action chính. "Chưa có nhân viên. [Thêm nhân viên]" |
| Empty — filter no results | List pages | NZ `nz-empty` (no image) + "Không tìm thấy kết quả. [Xoá bộ lọc]" |
| Error — API fail | Mọi page | NzMessageService.error() toast. Nếu full page fail → NZ `nz-result` status="error" + "Thử lại" button |
| Error — form validation | Form | Inline error dưới field (Angular Reactive Forms + NZ Form default). Scroll to first error field on submit |
| 403 — không có quyền | Protected pages | NZ `nz-result` status="403": "Bạn không có quyền truy cập trang này." + "Về Dashboard" button |
| 404 — không tồn tại | Any URL | NZ `nz-result` status="404": "Trang không tồn tại." + "Về Dashboard" button |
| Offline | Global | Không xử lý đặc biệt V1 — API fail → error toast. `[ASSUMPTION]` |
| Session expired | Global | JWT hết hạn, refresh fail → redirect `/login` + NzMessageService.warning("Phiên đăng nhập hết hạn") |
| Account locked | Login | Sau 5 lần sai → "Tài khoản đã bị khoá. Thử lại sau 15 phút." (FR-1) |
| Unsaved changes | Form (Modal/Drawer) | Close khi dirty → NzModalService.confirm: "Huỷ thay đổi? Dữ liệu chưa lưu sẽ bị mất." `[ASSUMPTION]` |
| Payroll — Draft vs Confirmed | Payroll detail | Draft: editable table, button "Xác nhận". Confirmed: read-only, button "Xuất Excel" chỉ. Tag status trên header |
| Approval — Pipeline progress | Leave detail | Steps indicator (NZ Steps horizontal): ✓ Employee → ✓ Team Lead → ● Manager → ○ HR Admin. Current step highlighted |

## Interaction Primitives

HRMS là công cụ nghiệp vụ cho HR — **mouse-first**, không keyboard-first như developer tools.

- **Click row** → navigate đến detail (Employee, Contract). Cursor pointer trên row hover.
- **Double-click** → không dùng. Single click cho mọi action.
- **Hover** → row highlight (NZ Table default). Hiện action icons inline (Edit, Delete) bên phải row. Transition 150-200ms cho mọi hover state (background, opacity, transform).
- **Drag & drop** → không có V1. Step Builder dùng form, không drag.
- **Search** → Topbar global search (nhân viên) + inline search per table.
- **Keyboard** → `Enter` submit form, `Esc` close modal/drawer, `Tab` navigate fields. Không custom shortcuts V1.
- **Breadcrumb** → click để navigate lên cấp trên.
- **Sidebar collapse** → toggle button trên topbar hoặc sidebar bottom. State lưu localStorage.
- **Transitions** → tất cả hover/focus state dùng `transition: 150ms ease` hoặc `200ms ease`. Không animation phức tạp — tốc độ thao tác quan trọng hơn visual flair.

**Navigation patterns:**
- List → Detail: click row (hoặc tên nhân viên link).
- Detail → Edit: Button "Chỉnh sửa" trên detail header → Drawer.
- Detail → Sub-entity: Tabs trong detail page (Employee → tab Hợp đồng → click hợp đồng).
- Back: Breadcrumb hoặc browser back. Không custom "Back" button.

## Accessibility Floor

Behavioral. Visual contrast kế thừa NG-ZORRO (WCAG AA compliant by default).

- WCAG 2.1 AA cho web surface. NG-ZORRO components đáp ứng sẵn.
- `Tab` order theo reading order trên mọi surface. `Esc` close modal/drawer/popover.
- Angular Reactive Forms + NZ Form: `aria-required`, `aria-invalid`, error text liên kết qua `aria-describedby` (NG-ZORRO default).
- NZ Table: `userRole="table"`, header cells dùng `scope="col"` (NG-ZORRO default).
- Status Tag: không chỉ dùng màu — kèm text label ("Đã duyệt", "Từ chối"). Color không phải kênh thông tin duy nhất.
- Focus ring: NG-ZORRO default outline. Không customize.
- Ảnh/icon decorative: `aria-hidden="true"`. Avatar có `alt` = tên nhân viên.
- Notification dropdown: `aria-live="polite"` cho badge count update.

## Responsive & Platform

| Breakpoint | Hành vi |
|------------|---------|
| `≥ 1200px` (xl+) | Layout đầy đủ: Sidebar mở + Topbar + Content. Data table hiển thị tất cả cột. Dashboard stat cards 4 cột |
| `992–1199px` (lg) | Sidebar có thể thu gọn (icon only). Dashboard stat cards 2 cột. Table ẩn một số cột phụ |
| `768–991px` (md) | Sidebar thu gọn mặc định. Table responsive: ẩn cột ít quan trọng, giữ cột chính. Dashboard 2 cột |
| `< 768px` (sm) | Sidebar ẩn — hamburger menu mở Drawer. Table chuyển mobile layout (card per row hoặc scroll ngang). Dashboard 1 cột. `[ASSUMPTION]` |

**Desktop-first:** Mọi feature thiết kế và test trên desktop trước. Tablet và mobile là "functional" — sử dụng được nhưng không tối ưu.

**Không làm V1:**
- Responsive table card layout trên mobile — dùng scroll ngang.
- Mobile-specific navigation (bottom tab bar).
- Touch-optimized input (larger hit targets).
- PWA / offline support.

## Inspiration & Anti-patterns

**Lấy từ Linear / Vercel:**
- Layout Sidebar + Topbar + Content. Dark sidebar. Breadcrumb.
- Tối giản, spacing rộng rãi, typography hierarchy rõ ràng.
- Subtle elevation thay vì dramatic shadow.

**Lấy từ Notion / Stripe:**
- ProTable pattern: toolbar trên, table dưới, pagination dưới cùng.
- Dashboard stat cards row + alerts below.
- Calm UI, data-focused, không visual noise.

**Patterns giữ lại:**
- Quy trình duyệt dạng pipeline steps (visual).
- Employee detail page dùng tabs.
- Data-heavy list pages, filter bar nổi bật.
- Approval inbox dạng queue — danh sách đơn chờ, quick actions inline.
- Status tag rõ ràng trên mỗi item.

**Từ chối:**
- **BPMN drag-drop workflow builder** — quá phức tạp cho SME. Step Builder form-based đủ cho V1. Drag-drop deferred V2+.
- **Complex charts/graphs trên Dashboard** — HR SME cần số liệu nhanh, không cần analytics sâu. Stat cards + simple table đủ.
- **Kanban board cho approvals** — không phù hợp workflow tuần tự. Table/list với tab filter hiệu quả hơn.
- **Infinite scroll** — pagination only. HR cần biết "đang ở trang nào" và "tổng bao nhiêu".
- **Dark mode là default** — light mode default, dark mode opt-in via settings.
- **Animation/transition phức tạp** — tốc độ thao tác quan trọng hơn visual flair. Chỉ dùng subtle transitions (150-200ms ease).
- **Emoji trong app** — không. Professional tone.

## Key Flows

### Flow 1 — Lan onboard nhân viên mới (HR Admin, 9h sáng thứ Hai)

1. Lan đăng nhập hệ thống. Dashboard hiện: "2 hợp đồng sắp hết hạn", "4 đơn chờ duyệt". Nhưng hôm nay ưu tiên: nhận nhân viên mới.
2. Sidebar → Nhân sự → Nhân viên. Bảng nhân viên hiện 118 người. Lan click "Thêm nhân viên" (button primary trên toolbar).
3. Drawer (560px) mở từ phải. Form: Họ tên, Ngày sinh, CCCD, Email, SĐT, Phòng ban (Select), Chức vụ (Select). Lan điền từng field — validation inline khi blur.
4. Lan lưu. Toast: "Thêm nhân viên thành công". Drawer đóng. Bảng cập nhật: 119 nhân viên. Nhân viên mới xuất hiện đầu danh sách.
5. Lan click vào tên nhân viên mới → Employee Detail. Tab "Tài liệu" → "Upload" → chọn CCCD scan (JPG) + CV (PDF).
6. Tab "Hợp đồng" → "Thêm hợp đồng" (Drawer). Loại: Thử việc, Ngày bắt đầu, Ngày kết thúc, Mức lương. Lưu.
7. **Climax:** Hệ thống tự tạo tài khoản cho nhân viên mới và gửi email thông tin đăng nhập. Lan thấy nhân viên mới trong danh sách phòng ban với Tag "Thử việc" (`{colors.warning}`). Không cần qua IT, không cần Excel — từ quyết định tuyển đến hồ sơ hoàn chỉnh trong một phiên làm việc.

Failure: Upload file > 10MB → `message.error("File vượt quá 10MB")`. File type sai → `message.error("Chỉ chấp nhận PDF, JPG, PNG, DOCX")`.

### Flow 2 — Minh check-in đầu ngày (Employee, 8:05 sáng)

1. Minh ngồi vào bàn, mở trình duyệt, đăng nhập HRMS (IP công ty nằm trong whitelist).
2. Dashboard Employee: stat cards nhỏ — "Ngày công tháng này: 15/22", "Phép còn lại: 8 ngày", nút "Check-in" nổi bật (Button primary, size large).
3. Minh bấm "Check-in". Hệ thống kiểm tra IP whitelist → pass.
4. **Climax:** Toast: "Check-in 8:05 — Đúng giờ". Button chuyển thành "Check-out" (disabled đến cuối giờ hoặc luôn available). `[ASSUMPTION]` Bảng chấm công cá nhân bên dưới cập nhật: dòng hôm nay hiện "8:05 | —" (chưa check-out). Minh biết mình đã ghi nhận — 2 giây, xong.

Failure: IP không trong whitelist → `message.error("Không thể check-in. Vui lòng kết nối mạng công ty.")`. Đã check-in rồi → button disabled + tooltip "Đã check-in lúc 8:05".

### Flow 3 — Hoa gửi đơn nghỉ phép, chuỗi duyệt 3 cấp (Employee + Manager + HR Admin)

1. Hoa, nhân viên marketing, đăng nhập. Sidebar → Nghỉ phép → "Tạo đơn" (button primary trên toolbar).
2. Drawer mở: Loại phép (Select: Phép năm / Nghỉ ốm / Nghỉ không lương), Ngày bắt đầu (DatePicker), Ngày kết thúc (DatePicker), Lý do (TextArea). Hoa chọn "Phép năm", 2 ngày. Phép còn lại hiện inline: "Phép năm còn: 8 ngày → sau đơn này: 6 ngày".
3. Hoa bấm "Gửi đơn". Toast: "Đơn nghỉ phép đã gửi".
4. Tab "Đơn của tôi": đơn mới xuất hiện — Status Tag "Chờ duyệt" (`{colors.warning}`). Pipeline indicator (NZ Steps mini): `● Team Lead → ○ Manager → ○ HR Admin`.
5. **Team Lead Tuấn** nhận notification (bell badge +1). Click → Phê duyệt inbox. Thấy đơn của Hoa. Click "Duyệt" → Popconfirm → xác nhận. Steps update: `✓ Team Lead → ● Manager → ○ HR Admin`.
6. **Manager Hùng** nhận notification. Mở Phê duyệt → "Duyệt".
7. **HR Admin Lan** nhận notification. Mở Phê duyệt → "Duyệt".
8. **Climax:** Hoa nhận notification: "Đơn nghỉ phép đã được duyệt". Tab "Đơn của tôi" → Status Tag chuyển "Đã duyệt" (`{colors.success}`). Steps: `✓ Team Lead → ✓ Manager → ✓ HR Admin`. Phép còn lại tự trừ: 6 ngày. Toàn bộ quy trình — gửi đến duyệt xong — không cần email, không cần giấy, không cần hỏi "đơn tôi đến đâu rồi".

Failure: Tuấn từ chối → Modal lý do → Hoa nhận notification kèm lý do. Steps: `✗ Team Lead`. Status "Từ chối" (`{colors.danger}`). Pipeline dừng ngay. Hoa gửi đơn khi hết phép năm → cảnh báo inline: "Phép năm đã hết. Chuyển sang Nghỉ không lương?" → confirm → gửi.

### Flow 4 — Lan chạy bảng lương tháng (HR Admin, cuối tháng)

1. Lan vào Tiền lương → "Tạo bảng lương" (button primary). Select: Tháng 6/2026.
2. Hệ thống tính toán (loading bar: "Đang tính lương cho 118 nhân viên..."). Mất ~15 giây.
3. Full page Payroll Detail hiện: bảng lương draft. Table: Nhân viên | Ngày công | Lương cơ bản | Phụ cấp | OT | BHXH | BHYT | BHTN | Thuế TNCN | Lương Net. Header hiện Tag "Nháp" (text tertiary).
4. Lan review từng dòng. Scroll table, sort theo phòng ban. Phát hiện một nhân viên chấm công thiếu → click tên → Employee Detail tab Chấm công → chỉnh sửa → back → "Tính lại" button.
5. Mọi thứ đúng. Lan click "Xác nhận bảng lương" → Modal.confirm: "Xác nhận bảng lương tháng 6/2026? Sau khi xác nhận không thể chỉnh sửa."
6. **Climax:** Xác nhận. Tag chuyển "Đã xác nhận" (`{colors.success}`). Table lock read-only. Hệ thống gửi phiếu lương cho 118 nhân viên qua email + in-app notification. Button "Xuất Excel" xuất hiện. Lan click → file `bang_luong_2026-06.xlsx` download. Từ tính toán đến phát lương — dưới 1 giờ thay vì 2-3 ngày Excel.

Failure: Tính lương fail (dữ liệu chấm công chưa đủ) → error message chỉ rõ nhân viên nào thiếu dữ liệu, link đến Employee Detail để bổ sung.

### Flow 5 — Lan cấu hình workflow duyệt cho công ty (HR Admin)

1. Lan vào Cấu hình → Quy trình duyệt. Thấy danh sách workflow templates hiện có: "Nghỉ phép" (default: Employee → Manager → HR Admin).
2. Click vào "Nghỉ phép" → Step Builder page. Hiện chuỗi dọc: Step 1 "Employee" (fixed, không xoá được) → Step 2 "Manager" (Role Select) → Step 3 "HR Admin" (Role Select).
3. Lan click "Thêm bước" giữa Employee và Manager. Step mới xuất hiện → Lan chọn Role: "Team Lead". Thêm condition (optional): `leave_days >= 3`.
4. Preview chuỗi: "Employee → Team Lead (khi ≥ 3 ngày) → Manager → HR Admin".
5. **Climax:** Lan bấm "Lưu". Toast: "Cập nhật quy trình thành công". Từ nay mọi đơn nghỉ phép ≥ 3 ngày đi qua 4 bước. Đơn < 3 ngày bỏ qua Team Lead. Không cần developer, không cần config file — HR tự làm.

Failure: Lưu workflow không có step nào sau Employee → `message.error("Cần ít nhất một bước duyệt")`. Xoá step cuối cùng (ngoài Employee) → Popconfirm cảnh báo.

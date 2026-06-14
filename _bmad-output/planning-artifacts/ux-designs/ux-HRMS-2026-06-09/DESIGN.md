---
name: HRMS
description: Hệ thống quản lý nhân sự SaaS-ready cho SME Việt Nam. NG-ZORRO (Ant Design for Angular) base — premium brand-layer delta lấy cảm hứng từ Rippling/Linear/Notion, giữ nghiệp vụ HR data-focused.
status: final
sources:
  - {planning_artifacts}/prds/prd-HRMS-2026-06-09/prd.md
  - {planning_artifacts}/architecture.md
updated: 2026-06-09
colors:
  primary: '#1677FF'
  primary-hover: '#4096FF'
  primary-active: '#0958D9'
  primary-soft: 'rgba(22,119,255,0.10)'
  primary-bg: '#EFF6FF'
  success: '#059669'
  success-soft: '#ECFDF5'
  warning: '#D97706'
  warning-soft: '#FFFBEB'
  danger: '#DC2626'
  danger-soft: '#FEF2F2'
  orange: '#EA580C'
  orange-soft: '#FFF7ED'
  # Layout — Premium dark navy sidebar
  sidebar-bg: '#0F172A'
  sidebar-muted: '#94A3B8'
  sidebar-text: '#CBD5E1'
  sidebar-text-active: '#FFFFFF'
  sidebar-selected-bg: 'rgba(22,119,255,0.16)'
  sidebar-selected-indicator: '#1677FF'
  sidebar-gradient: 'radial-gradient(circle at 20% 0%, rgba(22,119,255,0.20), transparent 28%)'
  layout-bg: '#F6F8FB'
  surface: '#FFFFFF'
  # Text
  text: '#111827'
  muted: '#667085'
  faint: '#98A2B3'
  line: '#EEF2F7'
typography:
  font-family: "'Inter', system-ui, -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif"
  base-size: 14px
  line-height: 1.6
  page-title:
    fontSize: 32px
    fontWeight: '800'
    lineHeight: '1.15'
    letterSpacing: '-0.04em'
  page-description:
    fontSize: 15px
    fontWeight: '400'
    color: '{colors.muted}'
  section-title:
    fontSize: 18px
    fontWeight: '800'
    letterSpacing: '-0.02em'
  stat-value:
    fontSize: 36px
    fontWeight: '800'
    lineHeight: '1'
    letterSpacing: '-0.05em'
  stat-label:
    fontSize: 13px
    fontWeight: '600'
    color: '{colors.muted}'
  table-header:
    fontSize: 12px
    fontWeight: '800'
    letterSpacing: '0.07em'
    textTransform: 'uppercase'
    color: '#94A3B8'
  breadcrumb:
    fontSize: 13px
    fontWeight: '600'
    color: '{colors.faint}'
rounded:
  sm: 8px
  md: 12px
  lg: 16px
  xl: 20px
  full: 9999px
spacing:
  content-padding: 28px 36px
  card-padding: 22px
  card-gap: 20px
  section-gap: 22px
  sidebar-width: 260px
components:
  card:
    background: '{colors.surface}'
    radius: '{rounded.xl}'
    padding: '{spacing.card-padding}'
    shadow: '0 18px 50px rgba(15,23,42,0.06), 0 3px 10px rgba(15,23,42,0.04)'
    shadow-hover: '0 24px 70px rgba(15,23,42,0.10), 0 8px 18px rgba(15,23,42,0.06)'
    border: 'none'
  stat-card:
    background: '{colors.surface}'
    radius: '{rounded.xl}'
    padding: '22px'
    shadow: '{components.card.shadow}'
    icon-size: '48px'
    icon-radius: '{rounded.lg}'
    value-size: '36px'
    sparkline: true
    decorative-gradient: true
  data-table:
    radius: '{rounded.xl}'
    shadow: '{components.card.shadow}'
    header-bg: '#F8FAFF'
    row-hover: '#FBFDFF'
    row-height: '56px'
    cell-padding: '16px 24px'
  input:
    height: '44px'
    radius: '{rounded.md}'
    border: '1px solid {colors.line}'
    focus-shadow: '0 0 0 4px rgba(22,119,255,0.08)'
  button-primary:
    height: '44px'
    radius: '{rounded.md}'
    background: '{colors.primary}'
    shadow: '0 14px 30px rgba(22,119,255,0.18)'
  tag:
    radius: '{rounded.full}'
    padding: '5px 12px'
    font-size: '12px'
    font-weight: '800'
    border: 'none'
  step-timeline:
    line-color: 'linear-gradient(to bottom, {colors.primary-soft}, {colors.line})'
    node-size: '44px'
    node-radius: '{rounded.lg}'
    card-bg: '#F8FAFF'
    card-radius: '{rounded.lg}'
  nav-item:
    height: '42px'
    radius: '13px'
    hover-transform: 'translateX(2px)'
  approval-badge:
    background: '#EF4444'
    foreground: '#FFFFFF'
    min-width: '22px'
    radius: '{rounded.full}'
    font-size: '12px'
    font-weight: '700'
---

## Brand & Style

HRMS là nền tảng quản lý nhân sự SaaS cho doanh nghiệp vừa và nhỏ Việt Nam. Phong cách thiết kế: **Premium SaaS** — hiện đại, cao cấp, sạch, thoáng, data-focused. Mục tiêu: trông như sản phẩm thương mại cạnh tranh với Rippling, BambooHR, HiBob.

Tham khảo: **Rippling** (premium KPI cards, soft elevation), **Linear** (sidebar, typography, spacing), **Notion** (tối giản, calm), **Stripe Dashboard** (polished detail), **Jira Cloud** (sidebar navigation), **Base.vn** (nghiệp vụ Việt Nam).

HRMS dùng NG-ZORRO (Ant Design for Angular) làm nền tảng component nhưng **tinh chỉnh visual layer mạnh** qua SCSS variables + NZ global config: radius 20px cards, dramatic shadow, typography 800 weight, gradient accents, sparkline charts. Cảm giác "expensive and polished" — không phải admin template.

Brand mark: chữ "H" trắng trên gradient square (`linear-gradient(135deg, #1677ff, #69b1ff)`), border-radius 14px, shadow `0 14px 30px rgba(22,119,255,0.30)`. Subtitle: "People Operations".

Không dùng emoji trong giao diện. Icon dùng SVG inline — line style, stroke-width 2, consistent 19x19px across sidebar và UI.

Dark mode hỗ trợ qua `ConfigProvider` `algorithm: theme.darkAlgorithm` — không ưu tiên V1.

## Colors

Bảng màu dùng **Tailwind-inspired tones** — softer, premium hơn NG-ZORRO defaults:

- **Primary Blue (`#1677FF`)** — Hành động chính, active state, link, button primary. Giữ nguyên NG-ZORRO default.
- **Success (`#059669`)** — Trạng thái tích cực: Active, Approved, Confirmed. Text color — background dùng `#ECFDF5`.
- **Warning (`#D97706`)** — Cần chú ý: Pending, Expiring. Text color — background dùng `#FFFBEB`.
- **Danger (`#DC2626`)** — Tiêu cực: Rejected, Error. Text color — background dùng `#FEF2F2`.
- **Orange (`#EA580C`)** — Thử việc, Probation. Text color — background dùng `#FFF7ED`.

**Status Badge colors (pill, soft background, no border, weight 800):**

| Trạng thái | Background | Text | Dùng tại |
|-----------|------------|------|---------|
| Active, Approved, Confirmed | `#ECFDF5` | `#047857` | Employee, Leave, Payroll |
| Thử việc, Probation | `#FFF7ED` | `#C2410C` | Contract |
| Pending, Chờ duyệt | `#FFFBEB` | `#B45309` | Approval |
| Nghỉ phép năm | `#EFF6FF` | `#1D4ED8` | Leave type |
| Nghỉ ốm | `#FFFBEB` | `#B45309` | Leave type |
| Terminated, Rejected | `#FEF2F2` | `#B91C1C` | Employee, Leave |
| Draft, Neutral | `#F1F5F9` | `#475569` | Payroll, General |
| Department (purple) | `#F5F3FF` | `#6D28D9` | Employee list |

Tags dùng pill shape (`border-radius: 9999px`), padding `5px 12px`, font-weight 800.

**Layout colors:** Sidebar `#0F172A` (dark navy) + radial gradient accent. Content background `#F6F8FB`. Surface/cards `#FFFFFF` với dramatic shadow.

**Text colors:** Primary `#111827`, Muted `#667085`, Faint `#98A2B3`. Line/divider `#EEF2F7`.

## Typography

Dùng **Inter** weight 400–800 — bolder hơn typical SaaS, tạo visual hierarchy mạnh.

```
font-family: 'Inter', system-ui, -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
```

| Role | Size | Weight | Letter-spacing | Dùng tại |
|------|------|--------|---------------|---------|
| Page title | 32px | 800 | -0.04em | Heading trang ("Xin chao, Lan") |
| Section title | 18px | 800 | -0.02em | Card title, panel title |
| Stat value | 36px | 800 | -0.05em | Dashboard KPI số lớn |
| Stat label | 13px | 600 | normal | KPI card label |
| Body | 14px | 500 | normal | Nav items, table cells |
| Table header | 12px | 800 | 0.07em | Column header (uppercase) |
| Badge | 12px | 800 | normal | Status badges, counts |
| Breadcrumb | 13px | 600 | normal | Navigation breadcrumb |
| Caption | 12px | 400 | normal | Helper text, timestamp |
| Money | 32px | 850 | -0.05em | Currency values |

**Tighter letter-spacing** trên heading/stat tạo cảm giác tight, premium — giống Linear. Weight 800 thay vì 600–700 tạo visual impact mạnh hơn.

## Layout & Spacing

**Cấu trúc:** Sidebar trái (fixed, 260px) + Content area full height. Topbar trong content area (không fixed sticky riêng).

```
┌─────────────┬───────────────────────────────────────────────┐
│  Sidebar    │ Content Area [padding: 28px 36px]             │
│  [260px]    │                                               │
│             │  Breadcrumb                                   │
│  Brand      │  Page Title (32px/800)     [Search] [🔔] [Av] │
│  ────       │  Subtitle                                     │
│  Nav items  │                                               │
│  (SVG+text) │  ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐        │
│             │  │Quick │ │Quick │ │Quick │ │Quick │        │
│             │  └──────┘ └──────┘ └──────┘ └──────┘        │
│             │                                               │
│             │  ┌─────────────────────────────────────────┐  │
│             │  │ Card (radius: 20px, premium shadow)     │  │
│             │  │                                         │  │
│             │  └─────────────────────────────────────────┘  │
│  ────       │                                               │
│  Profile    │                                               │
└─────────────┴───────────────────────────────────────────────┘
```

- **Sidebar width:** 260px. Collapsible (icon only ~72px).
- **Content padding:** `28px 36px` — spacious.
- **Card gap:** 20px giữa các card cùng section.
- **Section gap:** 22px giữa các section lớn.
- **Table row height:** 56px — generous spacing.
- **Input/button height:** 44px — large click target, premium feel.

**Sidebar:** Dark navy `#0F172A` + radial gradient accent blue top-left. Nav items: 42px height, 13px radius, hover translateX(2px). Active item: blue bg + 3px left indicator. Groups: uppercase 11px, weight 700, 0.12em spacing. Bottom profile: avatar circle + name + role.

**Topbar:** Integrated into content area. Search bar (pill, 320px, 44px), notification icon (40px circle), message icon, avatar image. Không border cho icons — chỉ hover background + shadow.

## Elevation & Depth

Cards nổi bằng dramatic shadow, không border:
- **Card default:** `0 18px 50px rgba(15,23,42,0.06), 0 3px 10px rgba(15,23,42,0.04)` — softer và spread hơn admin template
- **Card hover:** `0 24px 70px rgba(15,23,42,0.10), 0 8px 18px rgba(15,23,42,0.06)` — lift effect
- **Button primary:** `0 14px 30px rgba(22,119,255,0.18)` — blue glow
- **Brand mark:** `0 14px 30px rgba(22,119,255,0.30)` — prominent glow
- **Search bar:** `0 10px 30px rgba(15,23,42,0.04)` — subtle depth
- **Dropdown/Popover:** `0 4px 16px rgba(0,0,0,0.12)`
- **Modal/Drawer:** `0 8px 30px rgba(0,0,0,0.16)`

Shadow sử dụng dual-layer (blur lớn + blur nhỏ) tạo natural depth. Hover lift cards translateY(-3px). Không dùng border cho cards.

## Shapes

Rounder, premium — tiêu chuẩn 2025 SaaS:

- **Cards, Panels, KPI cards:** `{rounded.xl}` (20px) — much rounder
- **Section cards (form):** `{rounded.xl}` (20px)
- **Buttons, Inputs, Selects:** `{rounded.md}` (12px)
- **Tags/Badges:** `{rounded.full}` (pill, 9999px)
- **Nav items:** 13px — slightly rounded
- **Timeline node:** `{rounded.lg}` (16px) — squared pill
- **Brand mark:** 14px
- **Avatar:** circle (999px)
- **Quick action card:** 18px
- **Page button (small):** 10px

## Components

### Quick Actions (Dashboard)

4-column grid ngay dưới topbar. Mỗi card: glass-morphism (`rgba(255,255,255,0.76)`, `backdrop-filter: blur(10px)`), border subtle, radius 18px. Icon (38px, 13px radius, primary-soft bg) + Title (14px, 700) + Description (12px, faint). Hover: translateY(-2px) + shadow.

### Stat Card (Dashboard KPI)

Card trắng, radius 20px, shadow premium. Hover lift translateY(-3px). Decorative gradient circle (120px, opacity, vị trí top-right).

- **Header:** Icon (48px, 16px radius, tinted bg) bên trái + Sparkline mini chart (74x28px, stroke 4px, opacity 0.65) bên phải.
- **Content:** Label (13px, 600, muted) + Value (36px, 800, -0.05em) + Trend text (13px, faint, bold colored up/down arrow).
- **Colors:** Blue (people), Green (new hires), Orange (contracts), Red (approvals).

### Data Table

Card trắng, radius 20px, shadow premium. Toolbar nằm ngoài card hoặc trong card.

- **Toolbar:** Search input (280px, 44px, 12px radius, icon inline) + Filter buttons (44px, 12px radius, border line, hover primary) + Spacer + Action buttons (Import, Export, Primary CTA).
- **Header:** Background `#F8FAFF`, text 12px/800/0.07em uppercase, color `#94A3B8`.
- **Row:** Hover `#FBFDFF`, cell padding `16px 24px`, divider `{colors.line}` 1px. Employee column: gradient avatar circle + Name (700) + Email (12px faint) stacked.
- **Hover actions:** Edit/Delete icons appear on row hover (opacity 0→1), 32px circle, transparent bg → primary-soft/danger-soft on hover.
- **Pagination:** Below table, 16px padding. "Tong: 118 nhan vien" left, page buttons (36px, 10px radius) right. Active page: primary bg.
- **Bulk bar:** Primary-soft bg, weight 700, slide up when rows selected.

### Employee Avatar

Gradient circle (38px), white text (14px, 800), initials. Mỗi người dùng gradient khác nhau:
- Blue: `linear-gradient(135deg, #3b82f6, #60a5fa)`
- Purple: `linear-gradient(135deg, #8b5cf6, #a78bfa)`
- Amber: `linear-gradient(135deg, #f59e0b, #fbbf24)`
- Green: `linear-gradient(135deg, #10b981, #34d399)`
- Pink: `linear-gradient(135deg, #ec4899, #f472b6)`
- Indigo: `linear-gradient(135deg, #6366f1, #818cf8)`

### Contract Deadline Widget

Progress bar thay vì plain text countdown. Mỗi row: "X ngay" text (800 weight, semantic color) + progress bar (5px, 999px radius, semantic color fill).

| Range | Color | Bar fill |
|-------|-------|----------|
| 0–7 ngày | Danger red | ~80% |
| 8–15 ngày | Warning amber | ~50% |
| 16+ ngày | Success green | ~30% |

### Step Builder (Workflow Config)

Redesigned: tách rõ 3 section card (radius 20px).

1. **Thông tin workflow** — Card trên cùng: Tên workflow (Input 44px) + Module (Select 44px), 2-column grid.
2. **Điều kiện áp dụng** — Card thứ hai: Condition rows (field select + operator select + value input + delete X), button dashed "Them dieu kien". Bỏ trống = áp dụng tất cả.
3. **Chuỗi phê duyệt** — Card lớn nhất:
   - **Timeline:** Vertical, line gradient (primary-soft → line), node 44px/16px radius.
   - **Step 1:** "Nguoi gui don" — muted card, badge "Co dinh".
   - **Step 2+:** Card bg `#F8FAFF`, 2-column form: Loại người duyệt (Direct Manager / Department Manager / Role / Specific User) + Vai trò/Người duyệt (dynamic select). Delete button góc phải.
   - **Add step:** Full-width dashed button, 16px radius.
   - **Preview:** Gradient bg (`#F8FAFF → #F0F7FF`), chip pills. Employee (gray) → steps (primary-soft). Condition text nhỏ dưới conditional step.
   - **Footer:** "Huy" (default) + "Luu quy trinh" (primary, shadow).

### Approval Inbox

Table trong card shadow, tab filter ở trên.

- **Tabs:** Pill tabs (999px radius, 40px height, 20px padding), active = primary-soft bg. Weight 700.
- **Table:** Requester (gradient avatar 38px + tên bold + phòng ban faint) + Type badge (pill) + Content (main + sub) + Date + Pipeline + Actions.
- **Pipeline:** Inline dots: Done (success-soft, check), Current (primary-soft, circle, 4px ring shadow), Pending (gray). Labels 12px/600 — current = primary color.
- **Actions:** "Duyet" (primary button, 34px, 10px radius, shadow) + "Tu choi" (outline, border line, danger text).
- **Bulk bar:** Primary-soft bg, "Da chon 2 don" + "Duyet tat ca" button.

### Activity Timeline (Dashboard)

Vertical dot timeline. Mỗi item: dot (10px circle, color + 5px ring shadow) + text (14px/700) + time (12px faint). Colors: blue (default), amber (warning), green (success).

### Payroll Card (Dashboard)

Card trắng, radius 20px. Title row: "Chi phi luong" + "Xem chi tiet" link. Main: currency value (32px/850/-0.05em) + trend text. SVG area chart (gradient fill + stroke line). Status badge "Da xac nhan" (green pill).

### Page Header

Mọi page có header pattern thống nhất:
- **Breadcrumb:** 13px/600/faint, phía trên title.
- **Title:** 32px/800/-0.04em (heavy impact).
- **Subtitle:** 15px/400/muted.
- **Utilities:** Search pill + notification icons + avatar — phải, cùng hàng title.
- Spacing: 28px giữa header block và content.

## Do's and Don'ts

| Do | Don't |
|---|---|
| Radius 20px cho cards, 12px cho inputs/buttons | Radius 4px hay 8px cho cards (quá sắc) |
| Shadow dramatic (18px blur + 3px blur dual layer) | Shadow nhẹ `0 1px 2px` (không đủ depth) |
| Weight 800 cho titles/badges, 700 cho names | Weight 600 cho titles (thiếu impact) |
| Letter-spacing -0.04em đến -0.05em cho headings | Letter-spacing bình thường (thiếu premium feel) |
| Gradient avatars, gradient brand mark | Solid color avatars flat |
| Sparkline charts trong KPI cards | KPI cards chỉ có số, không visual trend |
| Progress bars cho deadline countdown | Plain text "6 ngay" |
| SVG icons inline (stroke-width 2, 19x19) | Emoji hoặc icon font nặng |
| Quick action cards dưới header | Không có shortcut area |
| Activity timeline dot-style | Plain list không timeline visual |
| Sidebar dark navy `#0F172A` + gradient accent | Sidebar solid đen hoặc NG-ZORRO `#001529` |
| Nav items hover translateX(2px) + color shift | Nav items chỉ đổi background |
| Pill search bar (999px radius) trong topbar | Search box vuông border |
| Glass-morphism (backdrop-filter) cho cards nhẹ | Solid opaque cards everywhere |
| Input/button height 44px, generous click target | Input 32px (quá nhỏ, admin template cũ) |
| Dual-role text (Name bold + email faint stacked) | Chỉ hiện tên, không email |
| Table header 12px/800/0.07em uppercase | Header bold 14px giống cell text |

---
name: HRMS
description: Hệ thống quản lý nhân sự nội bộ. NG-ZORRO (Ant Design for Angular) base — modern minimalist brand-layer delta lấy cảm hứng từ Linear/Notion/Vercel, giữ nghiệp vụ HR data-focused.
status: final
sources:
  - {planning_artifacts}/prds/prd-HRMS-2026-06-09/prd.md
  - {planning_artifacts}/architecture.md
updated: 2026-06-20
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
  # Layout — Dark navy sidebar
  sidebar-bg: '#0F172A'
  sidebar-muted: '#94A3B8'
  sidebar-text: '#CBD5E1'
  sidebar-text-active: '#FFFFFF'
  sidebar-selected-bg: 'rgba(22,119,255,0.16)'
  sidebar-selected-indicator: '#1677FF'
  sidebar-gradient: 'radial-gradient(circle at 20% 0%, rgba(22,119,255,0.15), transparent 28%)'
  layout-bg: '#FAFAFA'
  surface: '#FFFFFF'
  # Text — neutral slate, softer than near-black
  text: '#1E293B'
  muted: '#64748B'
  faint: '#94A3B8'
  line: '#E2E8F0'
typography:
  font-family: "'Inter', system-ui, -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif"
  base-size: 14px
  line-height: 1.6
  page-title:
    fontSize: 28px
    fontWeight: '700'
    lineHeight: '1.2'
    letterSpacing: '-0.03em'
  page-description:
    fontSize: 15px
    fontWeight: '400'
    color: '{colors.muted}'
  section-title:
    fontSize: 16px
    fontWeight: '600'
    letterSpacing: '-0.01em'
  stat-value:
    fontSize: 32px
    fontWeight: '700'
    lineHeight: '1'
    letterSpacing: '-0.04em'
  stat-label:
    fontSize: 13px
    fontWeight: '500'
    color: '{colors.muted}'
  table-header:
    fontSize: 12px
    fontWeight: '600'
    letterSpacing: '0.06em'
    textTransform: 'uppercase'
    color: '#94A3B8'
  breadcrumb:
    fontSize: 13px
    fontWeight: '500'
    color: '{colors.faint}'
rounded:
  sm: 6px
  md: 10px
  lg: 12px
  xl: 16px
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
    radius: '{rounded.lg}'
    padding: '{spacing.card-padding}'
    shadow: '0 1px 3px rgba(0,0,0,0.05), 0 1px 2px rgba(0,0,0,0.03)'
    shadow-hover: '0 4px 12px rgba(0,0,0,0.08), 0 2px 4px rgba(0,0,0,0.04)'
    border: '1px solid {colors.line}'
    transition: 'box-shadow 150ms ease, border-color 150ms ease'
  stat-card:
    background: '{colors.surface}'
    radius: '{rounded.lg}'
    padding: '22px'
    shadow: '{components.card.shadow}'
    border: '{components.card.border}'
    icon-size: '44px'
    icon-radius: '{rounded.md}'
    value-size: '32px'
    sparkline: true
    decorative-gradient: false
  data-table:
    radius: '{rounded.lg}'
    shadow: '{components.card.shadow}'
    border: '{components.card.border}'
    header-bg: '#FAFAFA'
    row-hover: '#F8FAFC'
    row-height: '52px'
    cell-padding: '14px 20px'
  input:
    height: '44px'
    radius: '{rounded.md}'
    border: '1px solid {colors.line}'
    focus-shadow: '0 0 0 3px rgba(22,119,255,0.08)'
  button-primary:
    height: '44px'
    radius: '{rounded.md}'
    background: '{colors.primary}'
    shadow: '0 1px 3px rgba(22,119,255,0.12)'
    transition: 'background 150ms ease, box-shadow 150ms ease'
  tag:
    radius: '{rounded.full}'
    padding: '4px 10px'
    font-size: '12px'
    font-weight: '600'
    border: 'none'
  step-timeline:
    line-color: 'linear-gradient(to bottom, {colors.primary-soft}, {colors.line})'
    node-size: '40px'
    node-radius: '{rounded.md}'
    card-bg: '{colors.layout-bg}'
    card-radius: '{rounded.md}'
  nav-item:
    height: '40px'
    radius: '10px'
    hover-transform: 'translateX(2px)'
    transition: 'all 150ms ease'
  approval-badge:
    background: '#EF4444'
    foreground: '#FFFFFF'
    min-width: '20px'
    radius: '{rounded.full}'
    font-size: '11px'
    font-weight: '600'
---

## Brand & Style

HRMS là hệ thống quản lý nhân sự nội bộ cho doanh nghiệp vừa và nhỏ Việt Nam. Phong cách thiết kế: **Modern Minimalist** — sạch, thoáng, bình tĩnh, data-focused. Mục tiêu: giao diện chuyên nghiệp, dễ dùng hàng ngày, không gây mệt mắt.

Tham khảo: **Linear** (sidebar, typography, spacing), **Notion** (tối giản, calm), **Vercel Dashboard** (neutral palette, subtle elevation), **Stripe Dashboard** (polished detail, clean data display).

HRMS dùng NG-ZORRO (Ant Design for Angular) làm nền tảng component, tinh chỉnh visual layer qua SCSS variables + NZ global config: radius 12px cards, subtle border + shadow, typography weight 600–700, sparkline charts. Cảm giác "clean and functional" — chuyên nghiệp mà không phô trương.

Brand mark: chữ "H" trắng trên gradient square (`linear-gradient(135deg, #1677ff, #69b1ff)`), border-radius 12px, shadow `0 4px 12px rgba(22,119,255,0.16)`. Subtitle: "People Operations".

Không dùng emoji trong giao diện. Icon dùng **Lucide** — line style, stroke-width 2, 20x20px, consistent across sidebar và UI.

Transitions: 150–200ms ease cho tất cả hover/focus states. Không dùng animation phức tạp — chỉ shadow, color, opacity transitions.

Dark mode hỗ trợ qua `ConfigProvider` `algorithm: theme.darkAlgorithm` — không ưu tiên V1.

## Colors

Bảng màu dùng **Tailwind Slate** neutrals — trung tính, chuyên nghiệp, nhẹ nhàng:

- **Primary Blue (`#1677FF`)** — Hành động chính, active state, link, button primary. Giữ nguyên NG-ZORRO default.
- **Success (`#059669`)** — Trạng thái tích cực: Active, Approved, Confirmed. Text color — background dùng `#ECFDF5`.
- **Warning (`#D97706`)** — Cần chú ý: Pending, Expiring. Text color — background dùng `#FFFBEB`.
- **Danger (`#DC2626`)** — Tiêu cực: Rejected, Error. Text color — background dùng `#FEF2F2`.
- **Orange (`#EA580C`)** — Thử việc, Probation. Text color — background dùng `#FFF7ED`.

**Status Badge colors (pill, soft background, no border, weight 600):**

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

Tags dùng pill shape (`border-radius: 9999px`), padding `4px 10px`, font-weight 600.

**Layout colors:** Sidebar `#0F172A` (dark navy) + subtle radial gradient accent. Content background `#FAFAFA`. Surface/cards `#FFFFFF` với subtle border + light shadow.

**Text colors:** Primary `#1E293B` (dark slate), Muted `#64748B` (slate-500), Faint `#94A3B8` (slate-400). Line/divider `#E2E8F0` (slate-200).

## Typography

Dùng **Inter** weight 400–700 — hierarchy rõ ràng, nhẹ nhàng, dễ đọc.

```
font-family: 'Inter', system-ui, -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
```

| Role | Size | Weight | Letter-spacing | Dùng tại |
|------|------|--------|---------------|---------|
| Page title | 28px | 700 | -0.03em | Heading trang |
| Section title | 16px | 600 | -0.01em | Card title, panel title |
| Stat value | 32px | 700 | -0.04em | Dashboard KPI số lớn |
| Stat label | 13px | 500 | normal | KPI card label |
| Body | 14px | 400 | normal | Paragraphs, descriptions |
| Body medium | 14px | 500 | normal | Nav items, table cells, names |
| Table header | 12px | 600 | 0.06em | Column header (uppercase) |
| Badge | 12px | 600 | normal | Status badges, counts |
| Breadcrumb | 13px | 500 | normal | Navigation breadcrumb |
| Caption | 12px | 400 | normal | Helper text, timestamp |
| Money | 28px | 700 | -0.04em | Currency values |

Weight 700 cho headings, 600 cho labels/badges, 500 cho body medium — hierarchy qua weight differences, không cần tất cả đều bold.

## Layout & Spacing

**Cấu trúc:** Sidebar trái (fixed, 260px) + Content area full height. Topbar trong content area (không fixed sticky riêng).

```
┌─────────────┬───────────────────────────────────────────────┐
│  Sidebar    │ Content Area [padding: 28px 36px]             │
│  [260px]    │                                               │
│             │  Breadcrumb                                   │
│  Brand      │  Page Title (28px/700)     [Search] [Bell] [Av]│
│  ────       │  Subtitle                                     │
│  Nav items  │                                               │
│  (Lucide)   │  ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐        │
│             │  │Quick │ │Quick │ │Quick │ │Quick │        │
│             │  └──────┘ └──────┘ └──────┘ └──────┘        │
│             │                                               │
│             │  ┌─────────────────────────────────────────┐  │
│             │  │ Card (radius: 12px, subtle border)      │  │
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
- **Table row height:** 52px — comfortable without excess.
- **Input/button height:** 44px — generous click target.

**Sidebar:** Dark navy `#0F172A` + subtle radial gradient accent blue top-left. Nav items: 40px height, 10px radius, hover translateX(2px) + color shift. Active item: blue bg + 3px left indicator. Groups: uppercase 11px, weight 600, 0.06em spacing. Bottom profile: avatar circle + name + userRole.

**Topbar:** Integrated into content area. Search bar (pill, 320px, 44px), notification bell (38px circle), avatar. Không border cho icons — chỉ hover background subtle.

## Elevation & Depth

Elevation qua subtle shadow + border thay vì dramatic shadow:

- **Card default:** `0 1px 3px rgba(0,0,0,0.05), 0 1px 2px rgba(0,0,0,0.03)` + `border: 1px solid {colors.line}` — barely visible depth
- **Card hover:** `0 4px 12px rgba(0,0,0,0.08), 0 2px 4px rgba(0,0,0,0.04)` — gentle lift
- **Button primary:** `0 1px 3px rgba(22,119,255,0.12)` — minimal blue tint
- **Brand mark:** `0 4px 12px rgba(22,119,255,0.16)` — subtle glow
- **Search bar:** `0 1px 3px rgba(0,0,0,0.04)` — barely there
- **Dropdown/Popover:** `0 4px 16px rgba(0,0,0,0.10), 0 1px 3px rgba(0,0,0,0.06)`
- **Modal/Drawer:** `0 8px 24px rgba(0,0,0,0.12), 0 2px 6px rgba(0,0,0,0.06)`

Hover trên cards: chỉ thay đổi shadow và border-color — không translateY. Transitions: 150ms ease.

## Shapes

Clean, modern — tiêu chuẩn minimalist SaaS 2026:

- **Cards, Panels, KPI cards, Tables:** `{rounded.lg}` (12px)
- **Section cards (form):** `{rounded.lg}` (12px)
- **Buttons, Inputs, Selects:** `{rounded.md}` (10px)
- **Tags/Badges:** `{rounded.full}` (pill, 9999px)
- **Nav items:** `{rounded.md}` (10px)
- **Timeline node:** `{rounded.md}` (10px)
- **Brand mark:** 12px
- **Avatar:** circle (9999px)
- **Quick action card:** `{rounded.lg}` (12px)
- **Page button (small):** `{rounded.sm}` (6px)

## Components

### Quick Actions (Dashboard)

4-column grid ngay dưới topbar. Mỗi card: white bg, `border: 1px solid {colors.line}`, radius 12px. Icon (36px, 10px radius, primary-soft bg) + Title (14px, 600) + Description (12px, faint). Hover: border-color darkens to `{colors.muted}`, shadow increases. Transition 150ms.

### Stat Card (Dashboard KPI)

Card trắng, radius 12px, subtle border + shadow. Không decorative gradient — clean flat.

- **Header:** Icon (44px, 10px radius, tinted bg) bên trái + Sparkline mini chart (74x28px, stroke 3px, opacity 0.5) bên phải.
- **Content:** Label (13px, 500, muted) + Value (32px, 700, -0.04em) + Trend text (13px, 500, colored up/down arrow).
- **Colors:** Blue (people), Green (new hires), Orange (contracts), Red (approvals).
- **Hover:** shadow tăng, border-color shift — không translateY.

### Data Table

Card trắng, radius 12px, border + subtle shadow. Toolbar nằm ngoài card hoặc trong card.

- **Toolbar:** Search input (280px, 44px, 10px radius, icon inline) + Filter buttons (44px, 10px radius, border line, hover primary) + Spacer + Action buttons (Import, Export, Primary CTA).
- **Header:** Background `#FAFAFA`, text 12px/600/0.06em uppercase, color `#94A3B8`. Border-bottom 1px `{colors.line}`.
- **Row:** Hover `#F8FAFC`, cell padding `14px 20px`, divider `{colors.line}` 1px. Employee column: gradient avatar circle + Name (500) + Email (12px faint) stacked.
- **Hover actions:** Edit/Delete Lucide icons appear on row hover (opacity 0→1, transition 150ms), 30px circle, transparent bg → primary-soft/danger-soft on hover.
- **Pagination:** Below table, 14px padding. "Tổng: 118 nhân viên" left, page buttons (34px, 6px radius) right. Active page: primary bg.
- **Bulk bar:** Primary-soft bg, weight 600, slide up when rows selected.

### Employee Avatar

Gradient circle (38px), white text (14px, 700), initials. Mỗi người dùng gradient khác nhau:
- Blue: `linear-gradient(135deg, #3b82f6, #60a5fa)`
- Purple: `linear-gradient(135deg, #8b5cf6, #a78bfa)`
- Amber: `linear-gradient(135deg, #f59e0b, #fbbf24)`
- Green: `linear-gradient(135deg, #10b981, #34d399)`
- Pink: `linear-gradient(135deg, #ec4899, #f472b6)`
- Indigo: `linear-gradient(135deg, #6366f1, #818cf8)`

### Contract Deadline Widget

Progress bar thay vì plain text countdown. Mỗi row: "X ngày" text (600 weight, semantic color) + progress bar (4px, 999px radius, semantic color fill).

| Range | Color | Bar fill |
|-------|-------|----------|
| 0–7 ngày | Danger red | ~80% |
| 8–15 ngày | Warning amber | ~50% |
| 16+ ngày | Success green | ~30% |

### Step Builder (Workflow Config)

Tách rõ 3 section card (radius 12px, border).

1. **Thông tin workflow** — Card trên cùng: Tên workflow (Input 44px) + Module (Select 44px), 2-column grid.
2. **Điều kiện áp dụng** — Card thứ hai: Condition rows (field select + operator select + value input + delete X), button dashed "Thêm điều kiện". Bỏ trống = áp dụng tất cả.
3. **Chuỗi phê duyệt** — Card lớn nhất:
   - **Timeline:** Vertical, line gradient (primary-soft → line), node 40px/10px radius.
   - **Step 1:** "Người gửi đơn" — muted card, badge "Cố định".
   - **Step 2+:** Card bg `{colors.layout-bg}`, 2-column form: Loại người duyệt (Direct Manager / Department Manager / Role / Specific User) + Vai trò/Người duyệt (dynamic select). Delete button góc phải.
   - **Add step:** Full-width dashed button, 10px radius.
   - **Preview:** Bg `{colors.layout-bg}`, chip pills. Employee (gray) → steps (primary-soft). Condition text nhỏ dưới conditional step.
   - **Footer:** "Huỷ" (default) + "Lưu quy trình" (primary).

### Approval Inbox

Table trong card với border + subtle shadow, tab filter ở trên.

- **Tabs:** Pill tabs (999px radius, 38px height, 16px padding), active = primary-soft bg. Weight 600.
- **Table:** Requester (gradient avatar 38px + tên medium + phòng ban faint) + Type badge (pill) + Content (main + sub) + Date + Pipeline + Actions.
- **Pipeline:** Inline dots: Done (success-soft, check), Current (primary-soft, circle, 3px ring shadow), Pending (gray). Labels 12px/500 — current = primary color.
- **Actions:** "Duyệt" (primary button, 34px, 10px radius) + "Từ chối" (outline, border line, danger text).
- **Bulk bar:** Primary-soft bg, "Đã chọn 2 đơn" + "Duyệt tất cả" button.

### Activity Timeline (Dashboard)

Vertical dot timeline. Mỗi item: dot (8px circle, color + 3px ring shadow) + text (14px/500) + time (12px faint). Colors: blue (default), amber (warning), green (success).

### Payroll Card (Dashboard)

Card trắng, radius 12px, border. Title row: "Chi phí lương" + "Xem chi tiết" link. Main: currency value (28px/700/-0.04em) + trend text. SVG area chart (gradient fill + stroke line). Status badge "Đã xác nhận" (green pill).

### Page Header

Mọi page có header pattern thống nhất:
- **Breadcrumb:** 13px/500/faint, phía trên title.
- **Title:** 28px/700/-0.03em.
- **Subtitle:** 15px/400/muted.
- **Utilities:** Search pill + notification bell + avatar — phải, cùng hàng title.
- Spacing: 28px giữa header block và content.

### Notification Dropdown

Trigger: click Bell icon trên Topbar. Panel width 360px. Items: Lucide icon + content (1-2 dòng) + time (relative). Unread: bg `{colors.primary-bg}`. Footer: "Xem tất cả thông báo" link. `aria-live="polite"` cho badge count updates. Border + shadow consistent với dropdown token.

## Do's and Don'ts

| Do | Don't |
|---|---|
| Radius 12px cho cards, 10px cho inputs/buttons | Radius 20px+ cho cards (quá round cho minimalist) |
| Subtle border + light shadow (`1px solid + 1px blur`) | Heavy shadow `18px blur` hoặc no-border-no-shadow |
| Weight 700 cho titles, 600 cho labels/badges | Weight 800 everywhere (quá heavy, mệt mắt) |
| Letter-spacing -0.03em cho headings | Letter-spacing -0.05em (quá tight) |
| Gradient avatars, gradient brand mark | Solid color avatars flat |
| Sparkline charts trong KPI cards | KPI cards chỉ có số, không visual trend |
| Progress bars cho deadline countdown | Plain text "6 ngày" |
| Lucide icons (stroke-width 2, 20x20) | Emoji, icon fonts nặng, hoặc mixed icon sets |
| Quick action cards dưới header | Không có shortcut area |
| Sidebar dark navy `#0F172A` + subtle gradient | Sidebar trắng (thiếu contrast) hoặc solid đen |
| Nav items hover translateX(2px) + color shift | Nav items chỉ đổi background |
| Pill search bar (999px radius) trong topbar | Search box vuông border |
| Transitions 150ms ease on all interactive elements | Instant state changes hoặc animation >300ms |
| Background `#FAFAFA` neutral, không tint | Background `#F6F8FB` blue-tinted hoặc pure white |
| Text `#1E293B` dark slate (softer than black) | Text `#000000` pure black (harsh) |
| Cards with subtle `1px solid` border | Cards without border (floating, unclear boundaries) |
| Input/button height 44px, generous click target | Input 32px (quá nhỏ) |
| Dual-userRole text (Name medium + email faint stacked) | Chỉ hiện tên, không email |
| Table header 12px/600/0.06em uppercase | Header bold 14px giống cell text |

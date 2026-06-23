---
title: "PRD — HRMS"
status: final
created: 2026-06-09
updated: 2026-06-09
---

# PRD: HRMS — Hệ thống Quản lý Nhân sự

## 0. Mục đích tài liệu

PRD này dành cho đội phát triển, architect, và product owner — làm cơ sở để thiết kế kiến trúc, tạo epic/story, và triển khai V1 của HRMS. Tài liệu cấu trúc theo: Glossary định nghĩa thuật ngữ thống nhất, Features nhóm theo module với FR đánh số toàn cục (FR-1 → FR-41, tuần tự theo section), giả định đánh dấu `[ASSUMPTION]` inline và tổng hợp ở cuối. Product Brief (`brief-HRMS-2026-06-08/brief.md`) là đầu vào chính.

**Thay đổi quan trọng so với brief:** V1 bổ sung Dynamic Approval Workflow, Audit Log, Employee Documents, Dashboard, và Import dữ liệu so với Product Brief ban đầu.

## 1. Tầm nhìn

HRMS là hệ thống quản lý nhân sự **nội bộ, self-hosted**, thiết kế cho doanh nghiệp vừa và nhỏ tại Việt Nam (50–500 nhân viên). Hệ thống thay thế quy trình HR thủ công bằng Excel/giấy tờ — tập trung hóa hồ sơ nhân viên, hợp đồng lao động, chấm công, nghỉ phép, và tính lương vào một nền tảng web duy nhất.

Đây là hệ thống nội bộ cho **một công ty duy nhất** — không phải SaaS, không multi-tenant. Doanh nghiệp sở hữu hoàn toàn dữ liệu và hệ thống, tự triển khai trên hạ tầng riêng bằng Docker. Stack kỹ thuật Spring Boot + MySQL + Angular phổ biến tại Việt Nam — dễ tuyển developer, dễ bảo trì.

V1 bao gồm: hồ sơ nhân viên, hợp đồng lao động, chấm công, nghỉ phép, tính lương, dynamic approval workflow, audit log, quản lý hồ sơ tài liệu, import dữ liệu, và dashboard — đủ để vận hành HR nghiệp vụ hàng ngày.

## 2. Đối tượng sử dụng

### 2.1 Jobs To Be Done

- **HR Admin** cần quản lý tập trung hồ sơ, hợp đồng, chấm công, và chạy bảng lương hàng tháng chính xác — thay vì dành 2–3 ngày tính tay trên Excel.
- **Manager** cần nắm tình hình nhân sự phòng ban và duyệt đơn nghỉ phép nhanh chóng — thay vì qua email/giấy.
- **Employee** cần tự tra cứu thông tin cá nhân, xem lịch sử chấm công, gửi đơn nghỉ phép, và xem phiếu lương — không phải hỏi HR mỗi lần.

### 2.2 Ngoài đối tượng V1

- Ứng viên tuyển dụng (V2)
- Khách hàng/đối tác bên ngoài
- Quản lý cấp C-level cần analytics nâng cao (V2)

### 2.3 Hành trình người dùng chính

- **UJ-1. HR Admin tạo hồ sơ nhân viên mới và hợp đồng.**
  Lan, HR Admin tại công ty, nhận quyết định tuyển nhân viên mới. Đã đăng nhập hệ thống. Lan vào module Nhân viên → Thêm mới → nhập thông tin cá nhân (họ tên, CCCD, ngày sinh, phòng ban, chức vụ) → upload CCCD scan + CV → lưu → chuyển sang tab Hợp đồng → tạo hợp đồng thử việc (loại, ngày bắt đầu/kết thúc, mức lương) → lưu. Hệ thống tự tạo tài khoản cho nhân viên mới và gửi email thông tin đăng nhập. Lan thấy nhân viên mới trong danh sách phòng ban với trạng thái "Thử việc". Mọi thao tác của Lan được ghi vào audit log.

- **UJ-2. Employee check-in đầu ngày làm việc.**
  Minh, nhân viên kế toán, đến văn phòng lúc 8:05. Mở trình duyệt trên máy công ty (IP nằm trong whitelist) → đăng nhập → bấm "Check-in". Hệ thống ghi nhận 8:05 — trong ngưỡng 15 phút nên không tính đi muộn. Cuối ngày 17:30, Minh bấm "Check-out". Bảng chấm công cá nhân cập nhật ngay.

- **UJ-3. Employee gửi đơn nghỉ phép, quy trình duyệt đa cấp.**
  Hoa, nhân viên marketing, cần nghỉ phép năm 2 ngày. Đăng nhập → Nghỉ phép → Tạo đơn → chọn loại "Phép năm", ngày bắt đầu/kết thúc, lý do → gửi. Hệ thống tra Approval Workflow Template cho module "Nghỉ phép" — chuỗi duyệt: Team Lead → Manager → HR. Team Lead Tuấn nhận thông báo → duyệt → đơn tự động chuyển sang Manager Hùng → duyệt → chuyển sang HR Lan → duyệt. Hoa nhận thông báo "Đơn đã được duyệt". Số ngày phép còn lại tự động trừ. **Edge case:** Tuấn từ chối ở bước 1 → đơn dừng ngay, Hoa nhận thông báo kèm lý do, không chuyển tiếp. **Edge case:** Hoa gửi đơn khi đã hết phép năm — hệ thống cảnh báo và chuyển sang loại "Nghỉ không lương" nếu Hoa xác nhận.

- **UJ-4. HR Admin chạy bảng lương tháng.**
  Cuối tháng, Lan vào module Lương → Tạo bảng lương tháng 6/2026 → hệ thống tự động kéo dữ liệu chấm công, nghỉ phép, phụ cấp, OT → tính gross, trừ BHXH/BHYT/BHTN, tính thuế TNCN lũy tiến → hiển thị bảng lương draft. Lan review từng dòng, điều chỉnh nếu cần → xác nhận. Hệ thống gửi phiếu lương cho từng nhân viên qua email + in-app. Lan xuất bảng lương tổng hợp ra Excel.

- **UJ-5. Employee xem phiếu lương.**
  Đầu tháng, Minh nhận thông báo "Phiếu lương tháng 6 đã sẵn sàng". Đăng nhập → Phiếu lương → xem chi tiết: lương cơ bản, phụ cấp, OT, các khoản trừ (BHXH, BHYT, BHTN, thuế TNCN), lương net. Minh hiểu rõ cách tính mà không cần hỏi HR.

- **UJ-6. HR Admin tra cứu audit log sau sự cố lương.**
  Một nhân viên phản hồi phiếu lương sai. Lan vào Audit Log → filter theo module "Lương", nhân viên liên quan, khoảng thời gian → thấy rõ ai đã chỉnh sửa bảng lương, lúc nào, giá trị cũ → giá trị mới. Lan xác định nguyên nhân và xử lý.

- **UJ-7. HR Admin xem dashboard đầu ngày.**
  Lan mở hệ thống, dashboard hiển thị ngay: 3 hợp đồng sắp hết hạn tuần này, 5 đơn nghỉ phép chờ duyệt, tổng nhân viên active 118/120, chi phí lương tháng trước 1.2 tỷ VND. Lan biết ngay cần xử lý gì trước.

- **UJ-8. Admin cấu hình quy trình duyệt cho công ty.**
  Lan, HR Admin, muốn thêm bước duyệt cho đơn nghỉ phép. Đăng nhập → Cài đặt → Approval Workflow → chọn module "Nghỉ phép" → thấy chuỗi hiện tại: Employee → Manager → HR. Lan thêm bước "Team Lead" giữa Employee và Manager → lưu. Từ nay mọi đơn nghỉ phép đi theo chuỗi: Employee → Team Lead → Manager → HR.

## 3. Thuật ngữ

- **Admin (HR Admin)** — Vai trò quản trị cao nhất trong hệ thống. Quản lý nhân viên, chấm công, lương, cấu hình hệ thống.
- **Nhân viên (Employee)** — Người lao động có hồ sơ trong hệ thống. Thuộc một Phòng ban, giữ một Chức vụ, có một hoặc nhiều Hợp đồng.
- **Phòng ban (Department)** — Đơn vị tổ chức trong công ty. Có một Manager phụ trách.
- **Chức vụ (Position)** — Vị trí công việc của Nhân viên trong Phòng ban.
- **Hợp đồng lao động (Labor Contract)** — Thỏa thuận lao động theo Bộ luật Lao động 2019. Ba loại: Thử việc, Xác định thời hạn, Không xác định thời hạn.
- **Bảng chấm công (Timesheet)** — Tổng hợp dữ liệu check-in/check-out của Nhân viên theo tháng. Ghi nhận ngày công, đi muộn, về sớm.
- **Đơn nghỉ phép (Leave Request)** — Yêu cầu nghỉ của Nhân viên. Đi qua Approval Workflow. Trạng thái: Chờ duyệt → Đã duyệt / Từ chối.
- **Loại phép (Leave Type)** — Phân loại nghỉ phép: Phép năm, Ốm, Không lương, Cưới, Tang.
- **Số dư phép (Leave Balance)** — Số ngày phép còn lại của Nhân viên theo từng Loại phép trong năm.
- **Bảng lương (Payroll)** — Bảng tổng hợp lương tháng cho toàn bộ hoặc một nhóm Nhân viên. Trạng thái: Nháp → Xác nhận.
- **Phiếu lương (Payslip)** — Chi tiết lương của một Nhân viên trong một tháng: gross, các khoản trừ, net.
- **Phụ cấp (Allowance)** — Khoản bổ sung ngoài lương cơ bản, cấu hình linh hoạt (ăn trưa, xăng xe, điện thoại...).
- **OT (Overtime)** — Giờ làm thêm ngoài giờ hành chính, tính theo hệ số: 1.5x ngày thường, 2.0x cuối tuần, 3.0x ngày lễ.
- **BHXH / BHYT / BHTN** — Bảo hiểm Xã hội / Y tế / Thất nghiệp. Tỷ lệ đóng theo quy định hiện hành, cấu hình hệ thống.
- **Thuế TNCN** — Thuế Thu nhập Cá nhân, tính theo biểu thuế lũy tiến 7 bậc.
- **IP Whitelist** — Danh sách địa chỉ IP được phép thực hiện check-in/check-out chấm công. Cấu hình hệ thống.
- **Audit Log** — Bản ghi tự động mọi thao tác thay đổi dữ liệu trong hệ thống: ai, làm gì, lúc nào, giá trị cũ/mới.
- **Employee Document** — Tài liệu đính kèm hồ sơ nhân viên: CCCD, bằng cấp, CV, hợp đồng scan, giấy tờ BHXH.
- **Approval Workflow Template** — Chuỗi duyệt cấu hình hệ thống, per-module. Định nghĩa các bước duyệt tuần tự (ví dụ: Employee → Team Lead → Manager → HR). Mỗi bước gán cho một vai trò hoặc vị trí cụ thể.
- **Approval Step** — Một bước trong Approval Workflow Template. Có trạng thái: Chờ duyệt → Đã duyệt / Từ chối. Từ chối ở bất kỳ bước nào → dừng toàn bộ chuỗi.

## 4. Tính năng

### 4.1 Xác thực & Phân quyền

**Mô tả:** Hệ thống xác thực người dùng bằng username/password, cấp JWT token cho các request tiếp theo. Phân quyền theo 3 vai trò: Admin, Manager, Employee.

**Quyết định kiến trúc: Một user = một vai trò.** Trade-off: đơn giản hóa RBAC và JWT (1 userRole per token, không cần userRole-switching UI) nhưng hạn chế flexibility — nhân viên SME đôi khi kiêm nhiệm (vừa Manager vừa HR). Chấp nhận cho V1 vì đa số SME 50–500 người có phân vai rõ ràng. Multi-userRole deferred to V2 nếu có nhu cầu thực tế.

**Functional Requirements:**

#### FR-1: Đăng nhập bằng username/password

Người dùng đăng nhập bằng username và password. Hệ thống trả về JWT access token (chứa userRole) + refresh token. Realizes UJ-1, UJ-2.

**Testable:**
- Đăng nhập đúng → trả JWT access token (TTL 30 phút) và refresh token (TTL 7 ngày).
- Đăng nhập sai 5 lần liên tiếp → khóa tài khoản 15 phút.
- Refresh token hợp lệ → cấp access token mới mà không cần đăng nhập lại.
- JWT payload chứa: user_id, userRole.

#### FR-2: Phân quyền theo vai trò (RBAC)

Hệ thống kiểm tra vai trò trước mỗi request. Admin truy cập toàn bộ dữ liệu. Manager truy cập dữ liệu phòng ban mình. Employee truy cập dữ liệu cá nhân.

**Testable:**
- Employee truy cập API quản lý nhân viên → HTTP 403.
- Manager xem danh sách nhân viên → chỉ thấy nhân viên phòng ban mình.

#### FR-3: Quản lý tài khoản người dùng

Admin tạo, vô hiệu hóa, và reset password tài khoản. Realizes UJ-1.

**Testable:**
- Tạo nhân viên mới → hệ thống tự tạo tài khoản với password tạm và gửi email.
- Vô hiệu hóa tài khoản → token hiện tại bị revoke, không đăng nhập được.

---

### 4.2 Quản lý Hồ sơ Nhân viên

**Mô tả:** Module trung tâm lưu trữ toàn bộ thông tin nhân viên, phòng ban, chức vụ. Là nền tảng dữ liệu cho tất cả module khác. Realizes UJ-1.

**Functional Requirements:**

#### FR-7: CRUD Nhân viên

Admin tạo, xem, sửa, và vô hiệu hóa hồ sơ nhân viên. Realizes UJ-1. `[ASSUMPTION: Không xóa cứng — chỉ soft delete để bảo toàn dữ liệu lương/chấm công lịch sử.]`

**Testable:**
- Tạo nhân viên → mã nhân viên tự sinh, duy nhất trong hệ thống.
- Thông tin bắt buộc: họ tên, CCCD, ngày sinh, giới tính, phòng ban, chức vụ, ngày vào làm.
- Vô hiệu hóa nhân viên → không xuất hiện trong danh sách active, dữ liệu lịch sử giữ nguyên.

#### FR-8: Quản lý Phòng ban & Chức vụ

Admin tạo, sửa, xóa phòng ban và chức vụ trong hệ thống. Mỗi phòng ban có một Manager.

**Testable:**
- Xóa phòng ban đang có nhân viên → từ chối, yêu cầu chuyển nhân viên trước.
- Gán Manager cho phòng ban → Manager thấy nhân viên phòng ban trong phạm vi quản lý.

#### FR-9: Tìm kiếm & Lọc nhân viên

Tất cả vai trò tìm kiếm nhân viên theo tên, mã, phòng ban (trong phạm vi quyền, trong hệ thống).

**Testable:**
- Tìm theo tên → kết quả partial match, không phân biệt hoa thường.
- Filter theo phòng ban + trạng thái → kết quả chính xác, chỉ trong hệ thống hiện tại.

---

### 4.4 Quản lý Hợp đồng Lao động

**Mô tả:** Quản lý hợp đồng lao động theo Bộ luật Lao động 2019 — 3 loại: Thử việc, Xác định thời hạn (tối đa 36 tháng), Không xác định thời hạn. Hệ thống cảnh báo tự động khi hợp đồng sắp hết hạn. Realizes UJ-1.

**Functional Requirements:**

#### FR-10: CRUD Hợp đồng

Admin tạo, xem, sửa hợp đồng cho nhân viên. Mỗi nhân viên có một hợp đồng hiện hành. Realizes UJ-1.

**Testable:**
- Tạo hợp đồng → bắt buộc: loại, ngày bắt đầu, ngày kết thúc (trừ loại không xác định thời hạn), mức lương cơ bản.
- Tạo hợp đồng xác định thời hạn > 36 tháng → từ chối.
- Tạo hợp đồng mới khi có hợp đồng hiện hành → hợp đồng cũ chuyển trạng thái "Đã kết thúc".

#### FR-11: Cảnh báo hết hạn hợp đồng

Hệ thống gửi thông báo cho Admin khi hợp đồng còn 30 ngày trước ngày kết thúc. Realizes UJ-1, UJ-7.

**Testable:**
- Hợp đồng còn 30 ngày → Admin nhận thông báo email + in-app.
- Hợp đồng không xác định thời hạn → không gửi cảnh báo.
- Dashboard hiển thị danh sách hợp đồng sắp hết hạn.

---

### 4.5 Chấm công

**Mô tả:** Ghi nhận thời gian làm việc qua web, giới hạn trong mạng công ty (IP Whitelist hệ thống). Tổng hợp dữ liệu công theo tháng, đánh dấu đi muộn/về sớm. Realizes UJ-2.

**Functional Requirements:**

#### FR-12: Check-in / Check-out

Employee bấm check-in khi đến và check-out khi về. Hệ thống ghi nhận timestamp. Realizes UJ-2.

**Testable:**
- Check-in từ IP ngoài whitelist của hệ thống → từ chối với thông báo rõ ràng.
- Check-in lần 2 trong ngày → từ chối (đã check-in).
- Check-out mà chưa check-in → từ chối.
- `[ASSUMPTION: Admin có thể chỉnh sửa/bổ sung chấm công cho nhân viên (trường hợp quên check-in/out).]`

#### FR-13: Quản lý IP Whitelist

Admin cấu hình danh sách IP được phép chấm công cho hệ thống mình.

**Testable:**
- Thêm/xóa IP trong whitelist → có hiệu lực ngay.
- Whitelist rỗng → không ai check-in được (fail-safe).

#### FR-14: Quy tắc đi muộn / về sớm

Hệ thống tự động đánh dấu: check-in trễ > 15 phút = đi muộn, check-out sớm > 15 phút = về sớm. Giờ chuẩn theo cấu hình hệ thống. `[ASSUMPTION: Giờ làm việc chuẩn cấu hình hệ thống (mặc định 8:00–17:00).]`

**Testable:**
- Check-in 8:16 (giờ chuẩn 8:00) → đánh dấu "Đi muộn".
- Check-in 8:14 → bình thường.
- Check-out 16:44 (giờ chuẩn 17:00) → đánh dấu "Về sớm".

#### FR-15: Bảng tổng hợp công tháng

Hệ thống tổng hợp số ngày công, số lần đi muộn/về sớm, ngày nghỉ theo tháng.

**Testable:**
- Tổng hợp tháng → hiển thị: tổng ngày công, ngày đi muộn, ngày về sớm, ngày nghỉ phép, ngày vắng.
- Employee xem bảng công cá nhân. Manager xem bảng công phòng ban. Admin xem toàn bộ.

---

### 4.6 Quản lý Nghỉ phép

**Mô tả:** Quy trình gửi và duyệt đơn nghỉ phép qua Dynamic Approval Workflow. Hỗ trợ 5 loại phép theo quy định. Realizes UJ-3.

**Functional Requirements:**

#### FR-16: Gửi đơn nghỉ phép

Employee tạo đơn nghỉ phép: chọn loại phép, ngày bắt đầu/kết thúc, lý do. Realizes UJ-3.

**Testable:**
- Gửi đơn phép năm khi số dư = 0 → cảnh báo, cho phép chuyển sang "Không lương" nếu xác nhận.
- Ngày bắt đầu < ngày hiện tại → từ chối.
- Gửi đơn → approver bước 1 nhận thông báo email + in-app.

#### FR-17: Duyệt / Từ chối đơn nghỉ phép theo Approval Workflow

Đơn nghỉ phép đi qua Approval Workflow Template đã cấu hình cho module "Nghỉ phép" của hệ thống. Mỗi approver trong chuỗi duyệt hoặc từ chối. Realizes UJ-3.

**Testable:**
- Gửi đơn → hệ thống tra Approval Workflow Template → gửi thông báo cho approver bước 1.
- Approver bước 1 duyệt → đơn tự động chuyển sang approver bước 2, thông báo email + in-app.
- Approver cuối cùng duyệt → Employee nhận thông báo "Đã duyệt", Số dư phép trừ tương ứng.
- Bất kỳ approver nào từ chối → đơn dừng ngay, Employee nhận thông báo kèm lý do và bước đã từ chối.
- Approver chỉ thấy đơn đang chờ duyệt ở bước của mình, trong phạm vi quyền.
- Nếu chưa cấu hình workflow → mặc định Employee → Manager → HR Admin.

#### FR-18: Quản lý số dư phép

Hệ thống tự động cấp số ngày phép năm đầu năm và theo dõi số dư. `[ASSUMPTION: Phép năm mặc định 12 ngày/năm theo luật. Tăng thêm theo thâm niên (cứ 5 năm +1 ngày) — Admin có thể cấu hình hệ thống.]` `[ASSUMPTION: Phép năm không chuyển sang năm sau — mặc định reset đầu năm. Admin có thể bật/tắt carry-over hệ thống nếu cần.]`

**Testable:**
- Nhân viên mới vào giữa năm → phép năm tính pro-rata.
- Đầu năm mới → reset số dư phép năm (mặc định).
- Admin xem và điều chỉnh số dư phép thủ công.

---

### 4.7 Tính lương

**Mô tả:** Module tính lương tự động từ dữ liệu chấm công, nghỉ phép, phụ cấp, OT. Tính đúng BHXH, BHYT, BHTN, thuế TNCN lũy tiến theo cấu hình hệ thống. Realizes UJ-4, UJ-5.

**Functional Requirements:**

#### FR-19: Cấu hình thông số lương

Admin cấu hình hệ thống: tỷ lệ BHXH/BHYT/BHTN, biểu thuế TNCN, mức giảm trừ gia cảnh, lương tối thiểu vùng.

**Testable:**
- Tỷ lệ mặc định: BHXH (NLĐ 8%, DN 17.5%), BHYT (NLĐ 1.5%, DN 3%), BHTN (NLĐ 1%, DN 1%).
- Giảm trừ bản thân 11 triệu/tháng, người phụ thuộc 4.4 triệu/người/tháng.
- Admin thay đổi tỷ lệ → áp dụng cho bảng lương tháng tiếp theo, chỉ từ tháng tiếp theo.

#### FR-20: Cấu hình phụ cấp

Admin tạo các loại phụ cấp linh hoạt (tên, số tiền cố định hoặc theo % lương). Gán phụ cấp cho nhân viên hoặc theo phòng ban/chức vụ trong hệ thống.

**Testable:**
- Tạo phụ cấp "Ăn trưa" = 800,000 VND/tháng, gán cho toàn công ty → mọi nhân viên có khoản này trên phiếu lương.
- Tạo phụ cấp "Trách nhiệm" = 10% lương cơ bản, gán cho chức vụ Manager → chỉ Manager nhận.

#### FR-21: Ghi nhận OT

Admin nhập giờ OT cho nhân viên, hệ thống tính tiền theo hệ số. OT là admin-only entry trong V1 — không đi qua Approval Workflow.

**Testable:**
- OT ngày thường → hệ số 1.5x lương giờ.
- OT cuối tuần → hệ số 2.0x.
- OT ngày lễ → hệ số 3.0x.
- Lương giờ = lương cơ bản / số ngày công chuẩn / 8. `[ASSUMPTION: Số ngày công chuẩn = 22 ngày/tháng, cấu hình hệ thống.]`

#### FR-22: Tạo bảng lương tháng

Admin tạo bảng lương cho tháng trong hệ thống. Hệ thống tự động tính cho từng nhân viên: lương cơ bản × (ngày công thực tế / ngày công chuẩn) + phụ cấp + OT − BHXH − BHYT − BHTN − thuế TNCN = lương net. Realizes UJ-4.

**Testable:**
- Tạo bảng lương → trạng thái "Nháp", Admin review và chỉnh sửa được.
- Xác nhận bảng lương → trạng thái "Xác nhận", không chỉnh sửa được.
- Tạo bảng lương tháng đã có bảng lương xác nhận → từ chối.

#### FR-23: Tính thuế TNCN lũy tiến

Hệ thống tính thuế TNCN theo biểu lũy tiến 7 bậc trên thu nhập chịu thuế (= thu nhập tính thuế − giảm trừ gia cảnh − bảo hiểm bắt buộc).

**Testable:**
- Thu nhập chịu thuế 10 triệu → thuế = 5M × 5% + 5M × 10% = 750,000.
- Thu nhập chịu thuế ≤ 0 → thuế = 0.
- Admin cấu hình số người phụ thuộc cho nhân viên → ảnh hưởng giảm trừ.

#### FR-24: Phiếu lương & Xuất Excel

Nhân viên xem phiếu lương chi tiết. Admin xuất bảng lương tổng hợp ra Excel. Realizes UJ-5.

**Testable:**
- Phiếu lương hiển thị: lương cơ bản, phụ cấp (liệt kê từng loại), OT, BHXH, BHYT, BHTN, thuế TNCN, lương net.
- Xác nhận bảng lương → hệ thống gửi phiếu lương qua email + in-app cho từng nhân viên.
- Xuất Excel → file chứa đầy đủ cột tương ứng phiếu lương cho tất cả nhân viên trong hệ thống.

---

### 4.8 Thông báo

**Mô tả:** Hệ thống gửi thông báo qua 2 kênh: in-app và email. Áp dụng cho các sự kiện nghiệp vụ quan trọng. Thông báo scoped theo hệ thống. Realizes UJ-3, UJ-4, UJ-5.

**Functional Requirements:**

#### FR-25: Thông báo in-app

Hệ thống hiển thị thông báo trong ứng dụng. Delivery: polling mỗi 30 giây hoặc khi user navigate. Realizes UJ-3, UJ-7. `[ASSUMPTION: V1 dùng short-polling. WebSocket/SSE cho real-time push deferred to V2.]`

**Testable:**
- Thông báo mới → badge đỏ trên icon chuông, cập nhật trong vòng 30 giây.
- Click thông báo → đánh dấu đã đọc, chuyển đến nội dung liên quan.
- Danh sách thông báo sắp xếp theo thời gian, phân trang.

#### FR-26: Thông báo email

Hệ thống gửi email cho các sự kiện: đơn nghỉ phép (gửi/duyệt/từ chối), hợp đồng sắp hết hạn, phiếu lương hàng tháng, tài khoản mới.

**Testable:**
- Gửi đơn nghỉ phép → approver bước 1 nhận email.
- Duyệt/từ chối → Employee nhận email.
- Hợp đồng còn 30 ngày → Admin nhận email.
- Xác nhận bảng lương → mỗi Employee nhận email phiếu lương.
- `[ASSUMPTION: V1 dùng một SMTP config chung.]`

---

### 4.9 Audit Log

**Mô tả:** Ghi nhận tự động mọi thao tác thay đổi dữ liệu, đặc biệt các thao tác nhạy cảm HR: sửa lương, sửa hợp đồng, vô hiệu hóa nhân viên, duyệt nghỉ phép, chỉnh sửa chấm công. Dữ liệu audit immutable — không ai có thể sửa/xóa. Realizes UJ-6.

**Functional Requirements:**

#### FR-27: Ghi nhận audit log tự động

Hệ thống tự động ghi log cho mọi thao tác CREATE, UPDATE, DELETE trên dữ liệu nghiệp vụ. Realizes UJ-6.

**Testable:**
- Sửa lương nhân viên → audit log ghi: user_id, user_id, timestamp, entity (payroll), action (UPDATE), old_value, new_value.
- Vô hiệu hóa nhân viên → audit log ghi đầy đủ.
- Duyệt đơn nghỉ phép → audit log ghi.
- Admin chỉnh sửa chấm công → audit log ghi giá trị cũ và mới.
- Audit log records không có API update/delete — chỉ CREATE và READ.

#### FR-28: Xem & Tra cứu audit log

Admin xem audit log với filter: module, user, nhân viên liên quan, khoảng thời gian, loại action. Realizes UJ-6.

**Testable:**
- Filter module "Lương" + tháng 6/2026 → hiển thị tất cả thay đổi lương trong tháng.
- Filter theo nhân viên → thấy mọi thay đổi liên quan đến nhân viên đó.
- Kết quả phân trang, sắp xếp theo thời gian mới nhất.
- Audit log chỉ hiển thị data trong hệ thống hiện tại.

#### FR-29: Login History

Hệ thống ghi nhận lịch sử đăng nhập: user, thời gian, IP, thành công/thất bại.

**Testable:**
- Đăng nhập thành công → ghi log với IP, user agent, timestamp.
- Đăng nhập thất bại → ghi log với lý do (sai password, tài khoản bị khóa).
- Admin xem login history với filter theo user, khoảng thời gian, trạng thái.

---

### 4.10 Quản lý Hồ sơ Tài liệu (Employee Documents)

**Mô tả:** Cho phép lưu trữ tài liệu đính kèm hồ sơ nhân viên: CCCD, bằng cấp, CV, hợp đồng scan, giấy tờ BHXH. Realizes UJ-1.

**Functional Requirements:**

#### FR-30: Upload tài liệu nhân viên

Admin upload file đính kèm vào hồ sơ nhân viên. Employee upload tài liệu cá nhân. Realizes UJ-1.

**Testable:**
- Upload file → hỗ trợ PDF, JPG, PNG, DOCX. `[ASSUMPTION: Giới hạn 10MB/file, tối đa 20 file/nhân viên.]`
- File lưu trữ theo cấu trúc user_id/employee_id/ trên storage.
- Upload file có tên trùng → đổi tên tự động, không ghi đè.

#### FR-31: Quản lý & Xem tài liệu

Admin xem, download, xóa tài liệu của nhân viên. Employee xem và download tài liệu cá nhân.

**Testable:**
- Admin xem danh sách tài liệu nhân viên → hiển thị tên file, loại, ngày upload, người upload.
- Employee chỉ xem/download tài liệu của mình.
- Xóa tài liệu → soft delete (file vẫn tồn tại trên storage, đánh dấu deleted). `[ASSUMPTION: Dùng local filesystem storage trong V1. Cloud storage (S3) deferred to V2.]`
- Download file → kiểm tra quyền truy cập (userRole-based).

---

### 4.11 Dashboard

**Mô tả:** Trang tổng quan hiển thị KPI HR cơ bản ngay khi đăng nhập. Giúp Admin nắm bắt tình hình nhanh mà không cần vào từng module. Realizes UJ-7.

**Functional Requirements:**

#### FR-32: HR Dashboard

Admin xem dashboard tổng quan nhân sự. Realizes UJ-7.

**Testable:**
- Hiển thị: tổng nhân viên active, nhân viên mới tháng này, nhân viên nghỉ việc tháng này, tỷ lệ biến động.
- Danh sách hợp đồng sắp hết hạn (30 ngày tới).
- Đơn nghỉ phép chờ duyệt (cho Manager: của phòng ban; cho Admin: toàn hệ thống).
- Dữ liệu chỉ trong hệ thống hiện tại.

#### FR-33: Payroll Dashboard

Admin xem tổng quan chi phí lương. Realizes UJ-7.

**Testable:**
- Hiển thị: tổng chi phí lương tháng gần nhất, so sánh với tháng trước (tăng/giảm %).
- Phân bổ chi phí theo phòng ban (bar chart hoặc table).
- Chỉ hiển thị khi có bảng lương đã xác nhận.

#### FR-34: Leave Dashboard

Admin và Manager xem tổng quan nghỉ phép. Realizes UJ-7.

**Testable:**
- Admin: tổng ngày nghỉ phép tháng này toàn hệ thống, phân bổ theo loại phép, phòng ban nghỉ nhiều nhất.
- Manager: tổng quan nghỉ phép phòng ban mình.
- Lịch nghỉ phép team (calendar view) hiển thị ai nghỉ ngày nào.

---

### 4.12 Dynamic Approval Workflow

**Mô tả:** Engine quy trình duyệt cấu hình hệ thống. Admin định nghĩa chuỗi duyệt cho từng module. V1 áp dụng cho module Nghỉ phép. OT là admin-only entry (FR-21) và không đi qua workflow trong V1. Engine mở rộng được cho module mới ở V2+ (ví dụ: OT request, expense claim). Realizes UJ-3, UJ-8.

**Functional Requirements:**

#### FR-35: Cấu hình Approval Workflow Template

Admin tạo và chỉnh sửa Approval Workflow Template hệ thống, per-module. Định nghĩa chuỗi các bước duyệt tuần tự. Realizes UJ-8.

**Testable:**
- Admin tạo workflow cho module "Nghỉ phép": Step 1 = Team Lead, Step 2 = Manager, Step 3 = HR Admin → lưu thành công.
- Mỗi step gán theo vai trò (userRole-based: Manager, HR). `[ASSUMPTION: V1 hỗ trợ userRole-based assignment. Position-based assignment (chỉ định người cụ thể cho từng step) cũng hỗ trợ nhưng optional.]`
- Chỉnh sửa workflow → thêm/xóa/sắp xếp lại step. Thay đổi chỉ áp dụng cho đơn mới, đơn đang trong pipeline giữ workflow cũ.
- Mỗi module chỉ có một workflow active tại một thời điểm hệ thống.

#### FR-36: Thực thi Approval Workflow

Khi Employee gửi request (nghỉ phép), hệ thống tự động tạo approval pipeline theo template hiện tại. Realizes UJ-3.

**Testable:**
- Gửi đơn nghỉ phép → hệ thống tạo pipeline với N step theo template → approver step 1 nhận thông báo.
- Step 1 approve → step 2 nhận thông báo. Tuần tự đến step cuối.
- Step cuối approve → request hoàn tất, trigger business logic (trừ phép, gửi thông báo cho Employee).
- Bất kỳ step reject → pipeline dừng, Employee nhận thông báo "Từ chối bởi [Tên] tại bước [N]".
- Employee xem trạng thái đơn → thấy pipeline: bước nào đã duyệt, bước nào đang chờ, bước nào chưa tới.
- `[ASSUMPTION: V1 chỉ hỗ trợ sequential approval (tuần tự). Parallel approval (nhiều người duyệt đồng thời ở cùng bước) deferred to V2.]`

#### FR-37: Mặc định và Fallback

Chưa cấu hình workflow → hệ thống dùng workflow mặc định: Employee → Manager → HR Admin.

**Testable:**
- Hệ thống mặc định có workflow (Employee → Manager → HR Admin) hoạt động mà không cần cấu hình.
- Admin xóa workflow custom → revert về mặc định (Employee → Manager → HR Admin).
- Approver trong step bị vô hiệu hóa (tài khoản disabled) → đơn escalate lên Admin với thông báo. `[ASSUMPTION: Escalation khi approver unavailable — chuyển lên Admin thay vì block pipeline.]`

---

### 4.13 Import dữ liệu

**Mô tả:** Cho phép import dữ liệu nhân viên hiện có từ file Excel khi doanh nghiệp bắt đầu sử dụng hệ thống — giải quyết bài toán migration từ quy trình thủ công.

**Functional Requirements:**

#### FR-38: Import nhân viên từ Excel

Admin upload file Excel theo template có sẵn để tạo hàng loạt hồ sơ nhân viên.

**Testable:**
- Hệ thống cung cấp file Excel template (các cột bắt buộc + optional) để download.
- Upload file đúng format → tạo nhân viên hàng loạt, báo cáo kết quả: N thành công, M lỗi.
- Dòng lỗi (thiếu trường bắt buộc, CCCD trùng, phòng ban không tồn tại) → skip và liệt kê chi tiết lỗi, không ảnh hưởng dòng khác.
- `[ASSUMPTION: V1 chỉ import nhân viên. Import chấm công, nghỉ phép lịch sử deferred.]`
- Giới hạn 500 dòng/lần upload. `[ASSUMPTION: Đủ cho quy mô 500 nhân viên.]`

---

### 4.14 Báo cáo

**Mô tả:** Ba báo cáo cơ bản phục vụ nghiệp vụ HR hàng ngày, hỗ trợ filter và xuất Excel. `[ASSUMPTION: Manager không xem báo cáo trong V1 — chỉ Admin.]`

**Functional Requirements:**

#### FR-39: Báo cáo danh sách nhân viên

Admin xem danh sách nhân viên trong hệ thống với filter theo phòng ban, trạng thái (đang làm / đã nghỉ), loại hợp đồng. Xuất Excel.

**Testable:**
- Filter phòng ban "Kế toán" + trạng thái "Đang làm" → chỉ hiển thị nhân viên khớp.
- Xuất Excel → file chứa tất cả cột hiển thị trên giao diện.

#### FR-40: Báo cáo tổng hợp công tháng

Admin xem bảng tổng hợp chấm công theo tháng: mỗi nhân viên một dòng, cột ngày công, đi muộn, về sớm, nghỉ phép, vắng. Xuất Excel.

**Testable:**
- Chọn tháng → hiển thị tổng hợp chính xác khớp với dữ liệu chấm công + nghỉ phép.
- Xuất Excel → format rõ ràng, có tổng cuối bảng.

#### FR-41: Báo cáo bảng lương tháng

Admin xem bảng lương tổng hợp theo tháng: mỗi nhân viên một dòng, các cột từ gross đến net. Xuất Excel.

**Testable:**
- Chỉ hiển thị tháng có bảng lương đã xác nhận.
- Tổng cột lương net = tổng chi phí lương tháng.
- Xuất Excel → format phù hợp kế toán.

## 5. Không làm trong V1 (Non-Goals)

- **Không phải nền tảng đánh giá hiệu suất** — KPI/OKR deferred to V2.
- **Không phải hệ thống tuyển dụng** — quản lý ứng viên, pipeline deferred to V2.
- **Không hỗ trợ mobile app** — chỉ web responsive, native app deferred to V2+.
- **Không tích hợp máy chấm công vật lý** — chỉ check-in web, tích hợp hardware deferred to V2+.
- **Không tích hợp kế toán/ngân hàng** — xuất Excel để import thủ công.
- **Không đa ngôn ngữ** — chỉ tiếng Việt.
- **Không SSO/LDAP** — chỉ username/password + JWT.
- **Không parallel approval** — V1 chỉ sequential workflow (tuần tự). Parallel approval (nhiều người duyệt đồng thời cùng bước) deferred to V2.
- **Không cấu trúc tổ chức phân cấp sâu** — V1 chỉ Department + Position. Hierarchy (Khối → Phòng ban → Team) deferred to V2.
- **Không multi-userRole per user** — mỗi user chỉ có 1 vai trò. Multi-userRole deferred to V2.

## 6. Phạm vi MVP

### 6.1 Trong phạm vi

- **Xác thực:** username/password + JWT, phân quyền 3 vai trò (Admin, Manager, Employee).
- **Hồ sơ nhân viên:** CRUD nhân viên, phòng ban, chức vụ + upload tài liệu đính kèm + import từ Excel.
- **Hợp đồng lao động:** 3 loại theo BLLĐ 2019 + cảnh báo hết hạn.
- **Chấm công:** Check-in/out web với IP Whitelist hệ thống + quy tắc đi muộn/về sớm.
- **Nghỉ phép:** 5 loại phép + dynamic approval workflow.
- **Tính lương:** Gross → net, BHXH/BHYT/BHTN, thuế TNCN lũy tiến, phụ cấp linh hoạt, OT.
- **Dynamic Approval Workflow:** Cấu hình chuỗi duyệt hệ thống, per-module (sequential). Mặc định Employee → Manager → HR Admin.
- **Audit log:** Ghi nhận tự động mọi thao tác, login history, tra cứu với filter.
- **Dashboard:** HR overview, payroll summary, leave summary.
- **Thông báo:** Email + in-app (polling).
- **Báo cáo:** 3 báo cáo cơ bản + xuất Excel.
- **Triển khai:** Docker Compose.

### 6.2 Ngoài phạm vi MVP

- Đánh giá hiệu suất (KPI/OKR) → V2
- Tuyển dụng → V2
- Parallel approval workflow → V2
- OT request qua approval workflow → V2
- Cấu trúc tổ chức phân cấp (Khối/Team) → V2
- SSO/LDAP hệ thống → V2
- Multi-userRole per user → V2
- Real-time notifications (WebSocket/SSE) → V2
- Mobile native app → V2+
- Tích hợp máy chấm công vật lý → V2+
- Cloud storage (S3) cho documents → V2
- Tích hợp kế toán/ngân hàng → V3+
- Database-hệ thống isolation → V3+

## 7. Tiêu chí thành công

**Primary**
- **SM-1**: HR Admin chạy bảng lương tháng hoàn chỉnh trong ≤ 4 giờ (từ 2–3 ngày thủ công). Validates FR-22, FR-23, FR-24.
- **SM-2**: Tính đúng 100% các khoản BHXH, BHYT, BHTN, thuế TNCN so với tính tay. Validates FR-19, FR-23.
- **SM-3**: Cảnh báo hết hạn hợp đồng 30 ngày trước — 0 hợp đồng bị bỏ sót. Validates FR-11.

**Secondary**
- **SM-5**: Nhân viên tự gửi và nhận kết quả nghỉ phép mà không cần email/giấy — 100% đơn qua hệ thống. Validates FR-16, FR-17.
- **SM-6**: Thời gian phản hồi < 2 giây cho list views và form submissions với 500 users đồng thời. Validates cross-cutting NFR.
- **SM-7**: Mọi thay đổi dữ liệu nhạy cảm (lương, hợp đồng, chấm công) đều có audit trail đầy đủ. Validates FR-27, FR-28.

**Counter-metrics**
- **SM-C1**: Thời gian onboarding nhân viên mới sử dụng hệ thống ≤ 30 phút — không hy sinh UX để thêm tính năng. Counterbalances SM-1, SM-5.
- **SM-C2**: Số lỗi tính lương phát sinh sau go-live không tăng so với tính tay — không hy sinh độ chính xác để tăng tốc. Counterbalances SM-1.

## 8. Cross-Cutting NFRs

### Bảo mật
- Password hash bằng bcrypt (cost factor ≥ 12).
- JWT access token TTL 30 phút, refresh token TTL 7 ngày.
- Tất cả API yêu cầu authentication (trừ login endpoint).
- Dữ liệu nhạy cảm (CCCD, lương) mã hóa at-rest trong database. `[ASSUMPTION: Dùng AES-256 cho encryption at-rest.]`
- HTTPS bắt buộc cho production.
- File upload: validate file type (whitelist), scan size limit, lưu ngoài webroot.

### Hiệu năng
- Thời gian phản hồi API < 2 giây cho list views và form submissions với 500 users đồng thời. Payroll calculation có budget riêng (FR-22).
- Tính bảng lương 500 nhân viên < 30 giây.
- Xuất Excel < 10 giây cho 500 dòng.

### Triển khai
- Docker Compose cho toàn bộ stack (Spring Boot + MySQL + Angular + Nginx).
- Tài liệu triển khai step-by-step cho người có kiến thức Docker cơ bản.
- Docker Compose one-command deploy (`docker compose up -d`).
- Database migration tự động khi nâng cấp version.
- Script backup/restore đi kèm. `[ASSUMPTION: Không có tính năng backup tự động trong UI — dùng script/cron bên ngoài.]`

### Tuân thủ pháp luật
- Công thức BHXH/BHYT/BHTN và thuế TNCN phải cấu hình được hệ thống để cập nhật khi quy định thay đổi (không hard-code).
- Hợp đồng lao động tuân thủ 3 loại theo Bộ luật Lao động 2019.
- OT tính theo đúng hệ số quy định: 1.5x / 2.0x / 3.0x.
- Audit log immutable — đáp ứng yêu cầu truy vết cho kiểm toán.

## 9. Ràng buộc & Giới hạn

- **Stack:** Spring Boot (Java) + MySQL + Angular — không thay đổi. `[ASSUMPTION: Java 17+, Spring Boot 3.x, MySQL 8.x, Angular 17.]`
- **Ngôn ngữ giao diện:** Chỉ tiếng Việt trong V1.
- **File storage:** Local filesystem trong V1. `[ASSUMPTION: 10GB disk tối thiểu cho 500 nhân viên.]`
- **Hạ tầng tối thiểu:** VPS 2 vCPU, 4GB RAM.
- **Rủi ro triển khai:** Doanh nghiệp nhỏ có thể thiếu nhân lực IT để tự triển khai và bảo trì Docker. Mitigation: tài liệu triển khai step-by-step, Docker Compose one-command deploy, và script backup/restore đi kèm.

## 10. Open Questions

1. Quy trình xử lý khi nhân viên quên check-in/check-out — Admin chỉnh tay hay có form request riêng?
2. Format email thông báo — plain text hay HTML template?
3. Backup strategy cụ thể — tần suất, retention period?
4. Audit log retention policy — giữ bao lâu? Có cần archive/purge không?
5. File storage limit — có cần quota không?

## 11. Assumptions Index

- **§4.1** — Một user = một vai trò (quyết định kiến trúc, không phải giả định — xem §4.1).
- **§4.2 FR-7** — Không xóa cứng nhân viên, chỉ soft delete.
- **§4.5 FR-12** — Admin có thể chỉnh sửa/bổ sung chấm công cho nhân viên.
- **§4.5 FR-14** — Giờ làm việc chuẩn cấu hình hệ thống, mặc định 8:00–17:00.
- **§4.6 FR-18** — Phép năm mặc định 12 ngày/năm, tăng theo thâm niên. Mặc định không chuyển sang năm sau, Admin có thể bật carry-over.
- **§4.7 FR-21** — Số ngày công chuẩn = 22 ngày/tháng, cấu hình hệ thống.
- **§4.8 FR-25** — V1 dùng short-polling cho in-app notifications. WebSocket/SSE deferred to V2.
- **§4.8 FR-26** — V1 dùng SMTP chung.
- **§4.9** — Audit log immutable, không có retention policy trong V1.
- **§4.10 FR-30** — Giới hạn 10MB/file, 20 file/nhân viên. Local filesystem storage.
- **§4.10 FR-31** — Local filesystem storage V1. Cloud storage (S3) deferred to V2.
- **§4.12 FR-35** — V1 hỗ trợ userRole-based assignment cho workflow steps. Position-based optional.
- **§4.12 FR-36** — V1 chỉ sequential approval. Parallel approval deferred to V2.
- **§4.12 FR-37** — Workflow mặc định Employee → Manager → HR Admin. Approver unavailable → escalate lên Admin.
- **§4.13 FR-38** — V1 chỉ import nhân viên từ Excel (500 dòng/lần). Import chấm công/nghỉ phép lịch sử deferred.
- **§4.14** — Manager không xem báo cáo trong V1, chỉ Admin.
- **§8 Bảo mật** — Dùng AES-256 cho encryption at-rest.
- **§8 Triển khai** — Không backup tự động trong UI, dùng script bên ngoài.
- **§9** — Java 17+, Spring Boot 3.x, MySQL 8.x, Angular 17.
- **§9** — VPS 2 vCPU / 4GB RAM (500 employees).
- **§9** — 10GB disk tối thiểu cho file storage.

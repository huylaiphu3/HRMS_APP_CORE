---
title: "Product Brief — HRMS"
status: final
created: 2026-06-08
updated: 2026-06-09
---

# Product Brief: HRMS

## Tóm tắt

HRMS là hệ thống quản lý nhân sự mã nguồn mở, được thiết kế cho doanh nghiệp vừa và nhỏ tại Việt Nam (50-500 nhân viên). Hệ thống thay thế quy trình quản lý nhân sự thủ công bằng Excel và giấy tờ, tập trung hóa toàn bộ nghiệp vụ từ hồ sơ nhân viên, chấm công, nghỉ phép đến tính lương, đồng thời tuân thủ đầy đủ quy định pháp luật lao động Việt Nam.

Khác với các giải pháp SaaS hiện có (Base.vn, AMIS HRM, 1Office), HRMS cho phép doanh nghiệp tự triển khai trên hạ tầng riêng, không phát sinh phí thuê bao hàng tháng, và linh hoạt tùy chỉnh theo đặc thù từng công ty.

## Vấn đề

Phần lớn doanh nghiệp nhỏ tại Việt Nam vẫn quản lý nhân sự bằng Excel hoặc giấy tờ. Thực tế này gây ra:

- **Sai sót tính lương:** Tính thủ công BHXH, BHYT, BHTN, thuế TNCN dễ nhầm, đặc biệt khi quy định thay đổi hàng năm.
- **Mất thời gian:** HR Admin dành phần lớn thời gian cho công việc hành chính lặp đi lặp lại (chấm công, duyệt phép, tổng hợp bảng lương).
- **Thiếu minh bạch:** Nhân viên không tự tra cứu được thông tin cá nhân, lịch sử chấm công hay phiếu lương.
- **Rủi ro pháp lý:** Quản lý hợp đồng thủ công dễ bỏ sót ngày hết hạn, vi phạm Bộ luật Lao động 2019.
- **Chi phí SaaS:** Các giải pháp cloud hiện tại tính phí theo đầu người/tháng — đắt đỏ khi quy mô tăng, và doanh nghiệp không kiểm soát được dữ liệu.

## Giải pháp

Hệ thống web tập trung cho phép:

- **HR Admin** quản lý toàn bộ hồ sơ, hợp đồng, chấm công, nghỉ phép, và chạy bảng lương hàng tháng — hệ thống tự động tính các khoản bảo hiểm và thuế.
- **Manager** theo dõi nhân viên phòng ban, duyệt đơn nghỉ phép trực tiếp trên hệ thống.
- **Employee** tự tra cứu thông tin cá nhân, xem lịch sử chấm công, gửi đơn nghỉ phép, và xem phiếu lương mỗi tháng.

Hệ thống được xây dựng dưới dạng REST API (Spring Boot) + Frontend (ReactJS), cho phép bổ sung ứng dụng di động sau này mà không cần viết lại phần server.

## Điểm khác biệt

- **Mã nguồn mở, tự triển khai:** Doanh nghiệp sở hữu hoàn toàn dữ liệu và hệ thống. Không phí SaaS hàng tháng — chỉ cần một server là vận hành được.
- **Tuân thủ pháp luật Việt Nam:** Tính lương theo đúng BHXH, BHYT, BHTN, thuế TNCN hiện hành. Hợp đồng lao động theo Bộ luật Lao động 2019 (thử việc, xác định thời hạn, không xác định thời hạn) với cảnh báo gia hạn tự động.
- **Tùy chỉnh linh hoạt:** Doanh nghiệp điều chỉnh quy trình theo đặc thù riêng — điều không thể với SaaS đóng gói.
- **Stack phổ biến:** Spring Boot + MySQL + ReactJS — dễ tìm developer, dễ bảo trì, cộng đồng lớn tại Việt Nam.

## Đối tượng sử dụng

| Vai trò | Nhu cầu chính |
|---------|---------------|
| **HR Admin** | Quản lý tập trung hồ sơ, hợp đồng, chấm công, lương. Giảm công việc thủ công. |
| **Manager** | Nắm tình hình nhân sự phòng ban. Duyệt nghỉ phép nhanh. |
| **Employee** | Tự phục vụ: xem thông tin, chấm công, gửi phép, xem phiếu lương. |

## Tiêu chí thành công

- HR Admin chạy được bảng lương tháng trong nửa ngày làm việc (thay vì 2-3 ngày thủ công).
- Tính đúng các khoản bảo hiểm và thuế TNCN theo biểu thuế lũy tiến hiện hành.
- Cảnh báo hợp đồng sắp hết hạn trước ít nhất 30 ngày.
- Nhân viên tự gửi đơn nghỉ phép và nhận kết quả duyệt mà không cần qua email/giấy.
- Hệ thống triển khai được trên một server đơn giản (VPS hoặc on-premise) với Docker.
- Thời gian phản hồi < 2 giây cho các thao tác thường dùng với 500 người dùng đồng thời.

## Giả định & Rủi ro

**Giả định:**
- Doanh nghiệp mục tiêu có ít nhất một nhân sự IT cơ bản để triển khai và vận hành Docker trên VPS.
- Tỷ lệ đóng BHXH, BHYT, BHTN và biểu thuế TNCN được cập nhật thủ công khi quy định thay đổi (không tự động đồng bộ từ cơ quan nhà nước).
- Dữ liệu nhân sự hiện tại của doanh nghiệp (nếu có) sẽ được import thủ công hoặc qua file Excel.

**Rủi ro:**
- Thay đổi quy định bảo hiểm/thuế hàng năm đòi hỏi cập nhật công thức kịp thời — nếu chậm sẽ tính sai lương.
- Doanh nghiệp nhỏ có thể thiếu nhân lực IT để tự triển khai và bảo trì hệ thống.
- Cạnh tranh với các giải pháp SaaS có sẵn (Base.vn, AMIS) về trải nghiệm người dùng và tốc độ triển khai.

## Phạm vi

### V1 (MVP)

| Module | Chức năng chính |
|--------|----------------|
| **Hồ sơ nhân viên** | CRUD nhân viên, phòng ban, chức vụ. Quản lý hợp đồng lao động (3 loại theo BLLĐ 2019). Cảnh báo hết hạn hợp đồng. |
| **Chấm công** | Ghi nhận check-in/check-out qua web. Tổng hợp công theo tháng. |
| **Nghỉ phép** | Gửi đơn nghỉ phép và duyệt theo quy trình Employee → Manager → HR. Quản lý số ngày phép theo quy định. Loại phép: năm, ốm, không lương, cưới, tang. |
| **Tính lương** | Tính lương gross → net. BHXH, BHYT, BHTN (người lao động + doanh nghiệp). Thuế TNCN lũy tiến. Xuất phiếu lương. |

### Ngoài phạm vi V1

- Đánh giá hiệu suất (KPI/OKR) → V2
- Tuyển dụng → V2
- Báo cáo nâng cao & dashboard → V2
- Mobile app → V2+
- Tích hợp máy chấm công vật lý → V2+
- Multi-tenant (một instance phục vụ nhiều công ty) → tầm nhìn dài hạn

## Tầm nhìn

Giai đoạn hiện tại tập trung xây sản phẩm, chưa có kế hoạch thương mại hóa cụ thể. Nếu thành công, HRMS trở thành giải pháp HRM mã nguồn mở hàng đầu cho doanh nghiệp Việt Nam — tương tự ERPNext cho ERP hay Odoo cho quản trị doanh nghiệp, nhưng chuyên sâu và tối ưu hóa cho nghiệp vụ nhân sự tại Việt Nam.

Lộ trình mở rộng:
- **V2:** Đánh giá hiệu suất, tuyển dụng, báo cáo nâng cao, mobile app.
- **V3+:** Tích hợp hệ thống bên ngoài (máy chấm công, kế toán, ngân hàng), hỗ trợ multi-tenant cho đối tác triển khai.
- **Cộng đồng:** Xây dựng cộng đồng contributor và ecosystem plugin để mở rộng tính năng.

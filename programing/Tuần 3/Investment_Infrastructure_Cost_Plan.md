# Kế Hoạch Đầu Tư Hạ Tầng & Thiết Bị - Smart Fashion ERP
**Ngày lập:** 04/02/2026
**Mục tiêu:** Ước tính chi phí vận hành hệ thống từ giai đoạn khởi nghiệp đến quy mô lớn (200 triệu bản ghi).

---

## 1. Chi Phí Hạ Tầng Server (Cloud Infrastructure)

Chi phí này trả hàng tháng (OpEx). Được chia theo 3 giai đoạn phát triển:

### Giai đoạn 1: Khởi động (Pilot / MVP)
*Dành cho phát triển, kiểm thử và chạy thực tế < 2 cửa hàng.*
*   **Server (VPS):** 1 Server (4 vCore, 8GB RAM, 100GB NVMe).
    *   *Mục đích:* Chạy cả Web App, Database và Job ngầm.
    *   *Chi phí:* ~1.000.000 VNĐ/tháng.
*   **Storage (Ảnh):** Cloudflare R2 / AWS S3 (dưới 50GB).
    *   *Chi phí:* Miễn phí hoặc rất thấp (< $5).
*   **Tổng cộng GĐ1:** **~1.200.000 VNĐ/tháng**.

### Giai đoạn 2: Tăng trưởng (Growth)
*Dữ liệu < 50 triệu bản ghi, lượng truy cập trung bình.*
*   **App Server:** 1 VPS (4 vCore, 8GB RAM) -> Chạy Java Spring Boot + Chat.
*   **DB Server:** 1 VPS Chuyên dụng (4 vCore, 16GB RAM) -> Chạy MySQL.
*   **Storage:** 500GB - 1TB ảnh.
*   **Tổng cộng GĐ2:** **~3.500.000 - 4.500.000 VNĐ/tháng**.

### Giai đoạn 3: Quy Mô Lớn (Scale Enterprise)
*Dữ liệu > 200 triệu bản ghi, hệ thống ổn định lâu dài.*
*   **Dedicated Server (Máy chủ vật lý):**
    *   CPU: 16 Cores.
    *   RAM: 128 GB (Cache toàn bộ Index vào RAM).
    *   Disk: 2x 2TB NVMe Enterprise (RAID 1).
*   **Backup Server:** 1 VPS xa để lưu backup database hàng ngày.
*   **Tổng cộng GĐ3:** **~6.000.000 - 8.000.000 VNĐ/tháng**.

---

## 2. Chi Phí Thiết Bị Tại Cửa Hàng (Store Hardware)

Đây là chi phí đầu tư một lần (CapEx) để vận hành quy trình **Offline-first & Smart Check-in**.

| Hạng mục | Số lượng (Mỗi cửa hàng) | Đơn giá ước tính | Thành tiền | Ghi chú |
| :--- | :---: | :--- | :--- | :--- |
| **Máy tính bảng (Tablet)** | 2 | ~8.000.000 đ | 16.000.000 đ | **iPad Gen 9/10** hoặc Samsung Tab S9 FE. Dùng để nhân viên check-in, tư vấn, tạo đơn di động. |
| **Máy quét mã vạch (Scanner)** | 2 | ~800.000 đ | 1.600.000 đ | Loại Bluetooth cầm tay, kết nối với Tablet. |
| **Máy in hóa đơn + Bếp** | 1 | ~2.500.000 đ | 2.500.000 đ | In LAN/Wifi để chốt đơn từ Tablet. |
| **Máy in tem mã vạch** | 1 | ~3.000.000 đ | 3.000.000 đ | Dùng để dán mã định danh cho sp (Xprinter/Godex). |
| **PC Quản lý/Thu ngân** | 1 | ~10.000.000 đ | 10.000.000 đ | Máy bàn cho Admin ngồi quầy nhập liệu lớn. |
| **Wifi chuyên dụng** | 1 | ~2.000.000 đ | 2.000.000 đ | Chịu tải >50 thiết bị (Unifi/Aruba) để App không rớt mạng. |
| **Tổng cộng (1 Store)** | | | **~35.100.000 đ** | Chi phí một lần. |

---

## 3. Chi Phí Dịch Vụ Bên Thứ 3 (SaaS/Service)

*   **Tên miền (Domain):** ~300.000 đ/năm (.com) hoặc ~750.000 đ/năm (.vn).
*   **SMS Brandname (CSKH):** ~800 đ/tin nhắn (Chỉ dùng khi cần xác thực OTP hoặc báo đơn gấp).
*   **Zalo ZNS (Thay thế SMS):** ~250 đ/tin nhắn (Rẻ hơn SMS, dùng để gửi thông báo đơn hàng).
*   **Email Marketing (Amazon SES):** Rất rẻ, ~2.500 đ cho 10.000 email.

---

## 4. Tổng Kết Ngân Sách Dự Kiến

1.  **Vốn đầu tư ban đầu (Thiết bị 1 cửa hàng):** **~35 - 40 Triệu VNĐ**.
2.  **Chi phí duy trì hàng tháng (Giai đoạn đầu):** **~1.5 - 2 Triệu VNĐ** (Server + Điện + Mạng).

---

## 5. Chi Phí Phát Triển Phần Mềm (Software Development Cost)

Đây là ước tính chi phí nhân sự (Man-months) để xây dựng toàn bộ chức năng trong ERD (Bán hàng, Kho, Chat, App Offline).

### 5.1. Quy mô Team Dự kiến
Để hoàn thành dự án trong **3 - 4 tháng**, cần tối thiểu:
*   **1 Backend Lead/Architect:** Thiết kế DB, API rường cột, Offline Sync.
*   **1 Backend Dev:** Code logic nghiệp vụ (Product, Chat, Order).
*   **1 Frontend/Mobile Dev:** Làm Web Admin và App Tablet (React/Flutter).
*   **1 Tester/QC:** Kiểm thử chức năng.

### 5.2. Ước tính Nỗ lực (Effort) theo Module
1.  **Core System (User, Auth, Settings):** 2 tuần.
2.  **Product & Inventory (Lifecycle, Cache, Variant):** 4 tuần.
3.  **Sales & POS (Guest, Order, Payment):** 4 tuần.
4.  **Offline Sync Engine (Phức tạp nhất):** 4 tuần.
5.  **Services (Chat Real-time, Repair Booking):** 3 tuần.
6.  **Store Visit & Smart Check-in:** 2 tuần.
7.  **Deployment & Testing:** 2 tuần.
*   **Tổng cộng:** ~20 - 24 tuần làm việc (khoảng 5-6 tháng/người quy đổi).

### 5.3. Bảng Giá Tham Khảo (Thị trường VN 2026)

| Phương án | Chi phí ước tính | Ưu điểm | Nhược điểm |
| :--- | :--- | :--- | :--- |
| **In-house (Tuyển team)** | **~300 - 450 Triệu** <br>*(Lương 3-4 tháng cho 3 nhân sự)* | Kiểm soát 100% source code, dễ bảo trì lâu dài. | Tốn chi phí quản lý, bảo hiểm, thưởng, chỗ ngồi. |
| **Outsourcing (Thuê ngoài)** | **~500 - 800 Triệu** | Có cam kết chất lượng, không lo quản lý nhân sự. | Giá cao hơn, có thể phát sinh chi phí khi thay đổi yêu cầu (CR). |
| **Freelancer Team** | **~200 - 300 Triệu** | Rẻ nhất. | Rủi ro cao về chất lượng và bảo hành/bảo trì. |

**Khuyến nghị:** Với tính chất phức tạp của **Offline-first**, nên chọn phương án **In-house** hoặc **Outsourcing công ty uy tín**. Tránh Freelancer lẻ tẻ vì module đồng bộ dữ liệu (Sync) rất khó.


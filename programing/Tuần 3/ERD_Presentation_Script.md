# Kịch Bản Quay Video Thuyết Trình ERD - Smart Fashion ERP
**Mục tiêu:** Giải thích toàn bộ thiết kế Cơ sở dữ liệu (ERD) một cách mạch lạc, dễ hiểu theo luồng nghiệp vụ.
**Đối tượng:** Đội ngũ lập trình viên, Nhà đầu tư, hoặc làm tài liệu hướng dẫn.

---

## 🎬 Cảnh 1: Tổng Quan & Chiến Lược Cốt Lõi (Introduction)
*   **Hình ảnh hiển thị:** Zoom vào tổng thể biểu đồ Mermaid (toàn bộ các bảng).
*   **Lời thoại gợi ý:**
    *   "Xin chào, đây là Cấu trúc dữ liệu (ERD) cho dự án Smart Fashion ERP."
    *   "Điểm đặc biệt nhất của thiết kế này là việc sử dụng **UUID** làm khóa chính cho toàn bộ hệ thống."
    *   "Lý do là gì? Chúng ta cần **Offline-first**. Nhân viên ở cửa hàng dùng iPad, Admin dùng điện thoại... tất cả đều có thể tạo đơn hàng, tạo sản phẩm ngay cả khi mất mạng internet. UUID giúp đảm bảo dữ liệu không bao giờ bị trùng lặp khi đồng bộ."

## 🎬 Cảnh 2: Quản Lý Người Dùng & Cá Nhân Hóa (User & Identity)
*   **Hình ảnh hiển thị:** Zoom vào cụm bảng `USER`, `CUSTOMER_PROFILE`, `TAILOR_PROFILE`.
*   **Lời thoại gợi ý:**
    *   "Bắt đầu với trung tâm hệ thống: bảng `USER`."
    *   "Ở đây, chúng ta không chỉ lưu tài khoản, mà còn lưu **Settings** (Ngôn ngữ, Giao diện Sáng/Tối) để cá nhân hóa trải nghiệm."
    *   "Liên kết với User là `CUSTOMER_PROFILE` - nơi lưu số đo 3 vòng quý giá để thuật toán AI gợi ý size sau này."
    *   "Và `TAILOR_PROFILE` dành cho thợ may, với hệ thống đánh giá sao riêng biệt."

## 🎬 Cảnh 3: Sản Phẩm & Vòng Đời Kinh Doanh (Product Lifecycle)
*   **Hình ảnh hiển thị:** Zoom vào cụm `CATEGORY`, `PRODUCT`, `PRODUCT_VARIANT`.
*   **Lời thoại gợi ý:**
    *   "Sang đến phần Hàng hóa. Bảng `PRODUCT` quản lý dòng đời sản phẩm thông qua trạng thái: Đang bán (Active), Xả hàng (Clearance) và Ngừng kinh doanh."
    *   "Chi tiết hơn, ở `PRODUCT_VARIANT`, chúng ta có trường `last_imported_at`. Hệ thống sẽ dựa vào đây để tự động cảnh báo... 'Món này nhập 90 ngày rồi chưa bán hết, xả hàng đi!'."
    *   "Đó là cách ERD hỗ trợ ra quyết định kinh doanh."

## 🎬 Cảnh 4: Bán Hàng Đa Kênh & Trải Nghiệm Offline (Sales & Experience)
*   **Hình ảnh hiển thị:** Zoom vào `ORDER`, `STORE_VISIT_LOG`.
*   **Lời thoại gợi ý:**
    *   "Về đơn hàng (`ORDER`), điểm mới là khả năng hỗ trợ **Guest Checkout**. Khách vãng lai không cần đăng ký tài khoản vẫn mua được, hệ thống sẽ lưu tạm Email/SĐT để marketing sau này."
    *   "Đặc biệt, bảng `STORE_VISIT_LOG` mới được thêm vào. Đây là tính năng **'Smart Check-in'**. Khi khách bước vào cửa hàng, hệ thống ghi nhận ngay lịch sử ghé thăm để nhân viên chào đón đúng điệu: 'Chào chị Lan, lâu rồi mới thấy chị ghé!'"

## 🎬 Cảnh 5: Dịch Vụ Mở Rộng - Sửa Chữa & Chat (Services)
*   **Hình ảnh hiển thị:** Zoom vào `REPAIR_BOOKING`, `CHAT_SESSION`.
*   **Lời thoại gợi ý:**
    *   "Không chỉ bán hàng, chúng ta còn làm dịch vụ."
    *   "`REPAIR_BOOKING` cho phép đặt lịch sửa đồ với Thợ may, có phân chia độ ưu tiên 'Gấp' hay 'Thường'."
    *   "Và cuối cùng, module **Live Chat** (`CHAT_SESSION`). Nó kết nối trực tiếp khách hàng và nhân viên theo thời gian thực (Real-time), giúp tư vấn size và chốt đơn nhanh chóng."

## 🎬 Cảnh 5.5: Hiệu Năng & Tối Ưu Hóa (Technical Deep Dive)
*   **Hình ảnh hiển thị:** Zoom vào `CATEGORY` (CTE) và `PRODUCT` (Cached Fields).
*   **Lời thoại gợi ý:**
    *   "Một câu hỏi quan trọng: 'Làm sao hệ thống chịu tải được 200 triệu bản ghi mà không bị chậm?'"
    *   "Bí quyết nằm ở thiết kế **Chống N+1 Query**."
    *   "Thứ nhất, với Menu `CATEGORY`, cấu trúc `parent_id` cho phép ta dùng kỹ thuật **CTE (Native Query)** để tải toàn bộ cây danh mục chỉ trong **1 tích tắc**, thay vì gọi database hàng trăm lần."
    *   "Thứ hai, ở bảng `PRODUCT`, các bạn thấy các trường `cached_min_price`, `total_stock`. Đây là kỹ thuật **Denormalization**. Thay vì mỗi lần khách xem hàng chúng ta phải cộng trừ nhân chia hàng triệu biến thể, chúng ta đã tính sẵn và lưu ở đây. Tốc độ hiển thị là tức thì."

## 🎬 Cảnh 6: Tổng Kết (Conclusion)
*   **Hình ảnh hiển thị:** Quay lại toàn cảnh ERD.
*   **Lời thoại gợi ý:**
    *   "Tổng kết lại, bản thiết kế ERD này không chỉ là các bảng dữ liệu khô khan."
    *   "Nó phản ánh một quy trình kinh doanh hiện đại: Từ lúc khách Check-in, được gợi ý size, Chat tư vấn, cho đến khi mua hàng và chăm sóc sau bán."
    *   "Cảm ơn mọi người đã theo dõi."

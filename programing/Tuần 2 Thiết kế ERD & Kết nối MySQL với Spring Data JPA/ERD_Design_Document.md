# Tài liệu Thiết Kế Cơ Sở Dữ Liệu (ERD) - Tuần 2

**Dự án:** Smart Fashion ERP
**Ngày tạo:** 28/01/2026
**Mục tiêu:** Thiết kế mô hình dữ liệu cho ứng dụng quản lý kho, bán hàng thời trang và dịch vụ sửa chữa, hỗ trợ tính năng Offline-first và AI phân tích.

## 1. Tổng Quan Mô Hình

Hệ thống được thiết kế xoay quanh các thực thể chính:

1. **Sản phẩm & Kho hàng (Product & Inventory):** Quản lý biến thể (Size, Màu), giá nhập/bán, và metadata từ AI.
2. **Khách hàng (Customer):** Hồ sơ số đo 3 vòng, lịch sử mua hàng, tích điểm.
3. **Đơn hàng (Order):** Xử lý giỏ hàng, thanh toán QR, và đồng bộ trạng thái kho.
4. **Dịch vụ Sửa chữa (Tailor Service):** Mạng lưới thợ may phi tập trung.

## 2. Diagram (Mermaid)

```mermaid
erDiagram
    %% User Management
    USER {
        uuid id PK
        string username
        string password_hash
        string email
        string role "ADMIN, CUSTOMER, TAILOR"
        string full_name
        string preferred_language "VI, EN"
        string theme_mode "LIGHT, DARK"
        datetime created_at
    }

    CUSTOMER_PROFILE {
        uuid id PK
        uuid user_id FK
        text preferences
        int points_balance
        %% Body Measurements
        float bust_cm "Vòng 1"
        float waist_cm "Vòng 2"
        float hip_cm "Vòng 3"
    }

    TAILOR_PROFILE {
        uuid id PK
        uuid user_id FK
        string shop_name
        float rating_avg
        int jobs_completed
        string primary_skill
    }

    STORE_VISIT_LOG {
        uuid id PK
        uuid customer_id FK
        datetime checkin_time
        string store_location
    }

    %% Product Catalog
    CATEGORY {
        uuid id PK
        string name "Áo Dài, Áo Bà Ba..."
        string slug
        uuid parent_id FK
    }

    PRODUCT {
        uuid id PK
        string name
        string slug
        text description
        uuid category_id FK
        string status "ACTIVE, CLEARANCE, DISCONTINUED"
        datetime created_at
    }

    MATERIAL {
        uuid id PK
        string name "Gấm, Lụa, Voan..."
        string description
    }

    PRODUCT_VARIANT {
        uuid id PK
        uuid product_id FK
        uuid material_id FK
        string sku
        string size "S, M, L, XL, 5X..."
        string color
        decimal price_import
        decimal price_sell
        int stock_quantity
        datetime last_imported_at
        %% AI Generated Fields
        string ai_era_prediction "2024, 2025..."
    }

    PRODUCT_IMAGE {
        uuid id PK
        uuid product_id FK
        string url
        string file_path "Local/NAS path"
        boolean is_thumbnail
        text ai_metadata "JSON: detected objects, colors"
    }

    %% Sales & Orders
    ORDER {
        uuid id PK
        uuid user_id FK "Nullable for Guest"
        string guest_email
        string guest_phone
        text guest_shipping_address
        string order_number
        string status "PENDING, PAID, SHIPPED"
        decimal total_amount
        string payment_method "QR_CODE, CASH"
        datetime ordered_at
    }

    ORDER_ITEM {
        uuid id PK
        uuid order_id FK
        uuid variant_id FK
        int quantity
        decimal unit_price
    }

    %% Reviews
    REVIEW {
        uuid id PK
        uuid user_id FK
        uuid product_id FK
        uuid variant_id FK "Optional: for size-specific reviews"
        int rating_stars
        text comment
        datetime created_at
    }

    %% Repair Service
    REPAIR_BOOKING {
        uuid id PK
        uuid customer_id FK
        uuid tailor_id FK
        string description
        string status "REQUESTED, QUOTED, IN_PROGRESS, COMPLETED"
        string priority "STANDARD, URGENT"
        decimal quoted_price
        datetime booking_date
        datetime estimated_completion_at
    }

    %% Live Chat & Support
    CHAT_SESSION {
        uuid id PK
        uuid customer_id FK "Nullable (Guest)"
        uuid staff_id FK "Nullable (Waiting)"
        string status "OPEN, ACTIVE, CLOSED"
        string topic "Product Inquiry, Support, Complaint"
        datetime created_at
        datetime closed_at
    }

    CHAT_MESSAGE {
        uuid id PK
        uuid session_id FK
        uuid sender_id FK "Nullable"
        string sender_type "CUSTOMER, STAFF, BOT"
        text content
        string image_url "Optional"
        boolean is_read
        datetime sent_at
    }

    %% Relationships
    USER ||--o| CUSTOMER_PROFILE : "has"
    USER ||--o| TAILOR_PROFILE : "has"
    USER ||--o{ ORDER : "places"
    USER ||--o{ REVIEW : "writes"
    USER ||--o{ REPAIR_BOOKING : "requests"
    USER ||--o{ CHAT_SESSION : "participates"
    CUSTOMER_PROFILE ||--o{ STORE_VISIT_LOG : "checks in"
    
    TAILOR_PROFILE ||--o{ REPAIR_BOOKING : "handles"

    CHAT_SESSION ||--o{ CHAT_MESSAGE : "contains"

    CATEGORY ||--o{ PRODUCT : "contains"
    PRODUCT ||--o{ PRODUCT_VARIANT : "has variants"
    PRODUCT ||--o{ PRODUCT_IMAGE : "has images"
    PRODUCT ||--o{ REVIEW : "has reviews"
    
    MATERIAL ||--o{ PRODUCT_VARIANT : "used in"

    ORDER ||--o{ ORDER_ITEM : "contains"
    PRODUCT_VARIANT ||--o{ ORDER_ITEM : "ordered in"
    PRODUCT_VARIANT ||--o{ REVIEW : "reviewed specific variant"
```

## 3. Chi Tiết Các Thực Thể Chính

### 3.1. User & Profiles
*   **Users**: Bảng trung tâm quản lý xác thực.
    *   *Settings*: Lưu `preferred_language` và `theme_mode` để cá nhân hóa trải nghiệm.
*   **CustomerProfile**: Lưu trữ số đo 3 vòng (Bust, Waist, Hip) để phục vụ thuật toán gợi ý size.
*   **StoreVisitLog**: Ghi nhận lịch sử check-in của khách hàng tại cửa hàng, hỗ trợ tính năng "Smart Welcome" (nhận diện khách cũ và gợi ý nhanh).

### 3.2. Product Catalog (Sản phẩm)
*   **Product**: Quản lý vòng đời sản phẩm thông qua trường `status`.
    *   `ACTIVE`: Đang kinh doanh.
    *   `CLEARANCE`: Xả hàng (thường đi kèm giảm giá).
    *   `DISCONTINUED`: Ngừng kinh doanh (không hiển thị cho khách, nhưng giữ data báo cáo).
*   **ProductVariant**: Quản lý kho chi tiết.
    *   `last_imported_at`: Theo dõi tuổi tồn kho. Nếu > 90 ngày sẽ cảnh báo để chuyển sang chế độ Xả hàng.

### 3.3. Orders (Đơn hàng)
*   **Guest Checkout**: Hỗ trợ khách vãng lai mua hàng không cần account.
    *   Nếu `user_id` là null, hệ thống sẽ sử dụng thông tin từ `guest_email`, `guest_phone`.
*   **Payments**: Hỗ trợ QR Code và tiền mặt.

### 3.4. Dịch vụ Sửa chữa (Tailor Service)
*   **Phân cấp độ ưu tiên**: Trường `priority` (URGENT/STANDARD) giúp thợ may sắp xếp công việc.
*   **Tiến độ**: Theo dõi trạng thái từ lúc Yêu cầu -> Báo giá -> Đang sửa -> Hoàn tất.

### 3.5. Live Chat & Support (CSKH)
*   **ChatSession**: Quản lý phiên hội thoại giữa Khách hàng (User/Guest) và Nhân viên (Staff).
    *   *Trạng thái*: `OPEN` (Chờ nhân viên), `ACTIVE` (Đang chat), `CLOSED` (Kết thúc).
*   **ChatMessage**: Lưu nội dung tin nhắn, hỗ trợ gửi ảnh (`image_url`) để khách hàng gửi mẫu áo cần tư vấn.
    *   *Real-time*: Thiết kế này hỗ trợ WebSocket để chat thời gian thực.

## 4. Ghi Chú Kỹ Thuật

1.  **UUID Primary Keys**: Toàn bộ hệ thống sử dụng **UUID** làm khóa chính thay vì BigInt tự tăng. Điều này bắt buộc để hỗ trợ tính năng **Offline-first**, giúp các thiết bị (iPad tại cửa hàng, điện thoại Admin) có thể sinh ID đơn hàng/sản phẩm ngay cả khi mất mạng mà không lo trùng lặp khi đồng bộ lại.
2.  **Naming Convention**: Sử dụng `snake_case` cho tên bảng và cột trong MySQL.
3.  **Indexing**: Index các cột tìm kiếm: SKU, Slug, Email, và `checkin_time` (để lọc khách mới đến).
4.  **Offline Sync**: Cần cơ chế Change Data Capture (CDC) hoặc tabl `SyncLog` để đồng bộ dữ liệu hai chiều (Client <-> Server).

---
*Tài liệu này được biên soạn dựa trên yêu cầu từ Tuần 1 và cập nhật tính năng Offline/Lifecycle vào Tuần 2.*

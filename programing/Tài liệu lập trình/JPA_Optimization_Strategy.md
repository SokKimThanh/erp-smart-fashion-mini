# Chiến lược Tối ưu hóa JPA: Menu Đệ quy & Dữ liệu Sản phẩm Lớn
**Dự án:** Smart Fashion ERP (Áo Dài, Áo Bà Ba, Thời trang sự kiện)
**Dựa trên:** Tài liệu nghiên cứu "Tối ưu hóa JPA - Tuần 2" & Yêu cầu "Offline-first - Tuần 1"
**Ngày cập nhật:** 29/01/2026

## 1. Bối cảnh Dự án & Thách thức
Hệ thống Smart Fashion ERP có các đặc thù:
*   **Sản phẩm đa dạng:** Áo dài (Học sinh, Cách tân, Bà xui), Áo bà ba, Gấm (Hoa mai, Thọ...).
*   **Dữ liệu lớn:** Cần hỗ trợ > 200 triệu bản ghi (Scale dài hạn) cho lịch sử nhập/xuất kho và biến thể sản phẩm (Size S-8X x Màu x Chất liệu).
*   **Offline-first:** App chạy trên thiết bị di động/PC có thể mất mạng, yêu cầu đồng bộ (Sync) khi có mạng.
*   **Menu động:** Danh mục sản phẩm phân cấp (Trẻ em -> Bé trai/Bé gái -> Áo dài cách tân...).

---

## 2. Chiến lược 1: Menu Danh mục (Category Tree)
**Vấn đề:** N+1 Query khi load danh mục "Áo Dài" -> "Học Sinh" -> "Lớp 10".

### Giải pháp: JOIN FETCH & EntityGraph
Do yêu cầu nghiên cứu đề xuất `JOIN FETCH`, và số lượng danh mục thời trang (Categories) thường không quá lớn (vài nghìn), giải pháp này cân bằng giữa hiệu năng và độ phức tạp.

#### Thiết kế Entity `Category`
```java
@Entity
public class Category {
    @Id @GeneratedValue
    private Long id;
    private String name; // Ví dụ: "Áo Dài"
    private String slug; // "ao-dai"
    
    @ManyToOne(fetch = FetchType.LAZY)
    private Category parent;
    
    @OneToMany(mappedBy = "parent")
    private Set<Category> children; // Dùng Set để tránh lỗi MultipleBagFetch khi fetch
}
```

#### Repository Optimization
```java
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    // Tải 1 cấp con (Eager Load) để hiển thị ngay menu cấp 1 & 2
    @Query("SELECT DISTINCT c FROM Category c LEFT JOIN FETCH c.children WHERE c.parent IS NULL")
    List<Category> findAllRoots();

    // Hỗ trợ CTE (Native Query) nếu menu quá sâu (để dự phòng cho tương lai)
    @Query(value = "WITH RECURSIVE cat_tree ...", nativeQuery = true)
    List<Category> findAllRecursive();
}
```

---

## 3. Chiến lược 2: Sản phẩm & Biến thể (Product & Variants)
**Vấn đề:**
*   Bảng `Product` chứa `description` (Mô tả dài), `ai_metadata` (JSON phân tích ảnh).
*   Bảng `ProductVariant` chứa Size (S-8X), Màu, Giá.
*   Load danh sách sản phẩm (Grid View) bị chậm nếu load hết các trường trên.

### Giải pháp: Projections & Keyset Pagination

#### 3.1. DTO Projection cho Grid View/Mobile App
Chỉ lấy dữ liệu cần thiết để hiển thị card sản phẩm trên điện thoại.

```java
public record ProductGridDTO(
    Long id, 
    String name, 
    Double minPrice, 
    String thumbnailUrl,
    String categoryName
) {}

@Query("""
    SELECT new com.smartfashion.erp.dto.ProductGridDTO(
        p.id, p.name, min(v.priceSell), img.url, c.name
    )
    FROM Product p
    JOIN p.category c
    LEFT JOIN p.variants v
    LEFT JOIN p.images img
    WHERE img.isThumbnail = true
    GROUP BY p.id
""")
Page<ProductGridDTO> findAllForGrid(Pageable pageable);
```

#### 3.2. Keyset Pagination (Cho Infinite Scroll trên Mobile)
Thay vì dùng `offset` (chậm khi data > 1 triệu dòng), dùng `id` hoặc `created_at` làm mốc (cursor).

```java
// Lấy 20 sản phẩm tiếp theo có ID nhỏ hơn lastId (Sắp xếp mới nhất trước)
@Query("SELECT new ... FROM Product p WHERE p.id < :lastId ORDER BY p.id DESC")
List<ProductGridDTO> findNextPage(@Param("lastId") Long lastId, Pageable pageable);
```

---

## 4. Chiến lược 3: Offline Sync & Batching
**Yêu cầu Tuần 1:** Ứng dụng chạy Offline.

### Vấn đề
Khi Sync dữ liệu từ Local lên Server (hoặc ngược lại), nếu insert từng dòng sẽ rất chậm.

### Giải pháp: JDBC Batch & UUID
1.  **ID Strategy:** Sử dụng `UUID` (hoặc `TSID`) thay vì `Identity` (Auto Increment) để có thể sinh ID ngay tại Offline App mà không lo trùng khi sync lên Server.
2.  **Batch Insert:** Cấu hình Hibernate Batch size.

```properties
# application.properties
spring.jpa.properties.hibernate.jdbc.batch_size=50
spring.jpa.properties.hibernate.order_inserts=true
```

---

## 5. Tổng kết
| Tính năng | Chiến lược JPA | Lợi ích cho Smart Fashion ERP |
| :--- | :--- | :--- |
| **Menu Danh mục** | `JOIN FETCH` (Set) | Load nhanh menu Áo Dài/Bà Ba đa cấp, tránh N+1. |
| **Danh sách SP** | `DTO Projection` | Grid view trên Mobile mượt mà, không load thừa description/AI JSON. |
| **Cuộn vô tận** | `Keyset Pagination` | Hỗ trợ danh sách 200M+ bản ghi không bị chậm ở trang cuối. |
| **Offline Sync** | `Batch Insert` | Đồng bộ hàng nghìn mã hàng/đơn hàng nhanh chóng khi có mạng lại. |

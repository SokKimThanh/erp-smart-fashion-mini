package com.smartfashion.erp.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Order - Đơn hàng
 * Theo ERD: Quản lý đơn hàng với user_id, thanh toán QR, trạng thái
 */
@Entity
@Table(name = "orders") // Tránh conflict với từ khóa SQL
@Data
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Liên kết với User (theo ERD)
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String orderNumber;          // Mã đơn hàng duy nhất
    
    @Enumerated(EnumType.STRING)
    private OrderStatus status;          // PENDING, PAID, SHIPPED
    
    private BigDecimal totalAmount;      // Tổng tiền
    
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod; // QR_CODE, CASH
    
    private LocalDateTime orderedAt;     // Thời gian đặt hàng
    
    // Danh sách các item trong đơn hàng
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();
    
    public enum OrderStatus {
        PENDING,  // Chờ xử lý
        PAID,     // Đã thanh toán
        SHIPPED,  // Đã giao hàng
        CANCELLED // Đã hủy
    }
    
    public enum PaymentMethod {
        QR_CODE,  // Thanh toán QR
        CASH,     // Tiền mặt
        BANK_TRANSFER // Chuyển khoản
    }
}

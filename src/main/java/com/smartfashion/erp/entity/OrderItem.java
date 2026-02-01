package com.smartfashion.erp.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

/**
 * OrderItem - Chi tiết đơn hàng
 * Theo ERD: Lưu variant_id, quantity, unit_price
 */
@Entity
@Data
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "variant_id")
    private ProductVariant variant;

    private Integer quantity;           // Số lượng
    private BigDecimal unitPrice;       // Đơn giá (lưu giá tại thời điểm bán)
}

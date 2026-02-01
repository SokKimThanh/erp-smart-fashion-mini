package com.smartfashion.erp.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

/**
 * ProductVariant - Biến thể sản phẩm
 * Theo ERD: Quản lý SKU, Size, Màu, Chất liệu, Giá nhập/bán, Tồn kho
 */
@Entity
@Data
public class ProductVariant {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36)
    private String id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "material_id")
    private Material material;

    private String sku;           // Mã SKU duy nhất
    private String size;          // S, M, L, XL, 5X...
    private String color;         // Màu sắc
    
    // Giá theo ERD gốc
    private BigDecimal priceImport;  // Giá nhập
    private BigDecimal priceSell;    // Giá bán
    
    private Integer stockQuantity;   // Tồn kho
    
    // AI Features
    private String aiEraPrediction;  // Dự đoán niên đại: "2024", "2025"...

    @Version
    private Long version;            // Optimistic locking cho Offline-First
}
package com.smartfashion.erp.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;

@Entity
@Data
public class ProductVariant {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private String size;      // S, M, L... 8X
    private String material;  // Gấm, Lụa, Voan...
    
    // Tách giá thành 2 phần để minh bạch
    private Double basePrice;       // Giá niêm yết của sản phẩm gốc
    private Double priceAdjustment; // Phụ phí cho Size lớn hoặc vải quý
    
    private Integer stock;

    @Version
    private Long version;     // Rất tốt, giữ nguyên để làm Offline-First

    // Hàm tiện ích (Helper method) - Không lưu xuống DB
    @Transient 
    public Double getTotalPrice() {
        return (basePrice != null ? basePrice : 0) + (priceAdjustment != null ? priceAdjustment : 0);
    }
}
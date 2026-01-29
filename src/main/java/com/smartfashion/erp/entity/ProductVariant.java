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

    private String size;      // Chỗ này giải thích cho Size 8X
    private String material;  // Chỗ này giải thích cho vải Gấm/Lụa
    private Double price;
    private Integer stock;

    @Version
    private Long version;     // Chỗ này giải thích cho Offline-First
}
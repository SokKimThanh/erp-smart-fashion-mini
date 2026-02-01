package com.smartfashion.erp.entity;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Material - Chất liệu vải
 * Theo ERD: Quản lý các loại vải (Gấm, Lụa, Voan...)
 */
@Entity
@Data
public class Material {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;        // Gấm, Lụa, Voan...
    private String description; // Mô tả chi tiết
}

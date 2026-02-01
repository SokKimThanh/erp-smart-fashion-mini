package com.smartfashion.erp.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;

@Entity
@Data
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36)
    private String id;

    private String name;
    
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
}
package com.smartfashion.erp.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * User - Người dùng hệ thống
 * Theo ERD: Quản lý ADMIN, CUSTOMER, TAILOR
 */
@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String passwordHash;
    private String email;
    
    @Enumerated(EnumType.STRING)
    private Role role;
    
    private String fullName;
    private LocalDateTime createdAt;
    
    public enum Role {
        ADMIN,
        CUSTOMER,
        TAILOR
    }
}

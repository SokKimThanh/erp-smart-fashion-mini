package com.smartfashion.erp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.smartfashion.erp.entity.User;

/**
 * UserRepository - Quản lý người dùng
 */
public interface UserRepository extends JpaRepository<User, String> {
    
}

package com.smartfashion.erp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.smartfashion.erp.entity.Order;

/**
 * OrderRepository - Quản lý đơn hàng
 */
public interface OrderRepository extends JpaRepository<Order, String> {
    
}

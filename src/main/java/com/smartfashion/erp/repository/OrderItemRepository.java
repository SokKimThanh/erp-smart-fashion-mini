package com.smartfashion.erp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.smartfashion.erp.entity.OrderItem;

/**
 * OrderItemRepository - Quản lý chi tiết đơn hàng
 */
public interface OrderItemRepository extends JpaRepository<OrderItem, String> {
    
}

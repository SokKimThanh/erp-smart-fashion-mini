package com.smartfashion.erp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.smartfashion.erp.entity.ProductVariant;

/**
 * ProductVariantRepository - Tầng truy xuất dữ liệu
 * 
 * JpaRepository<ProductVariant, Long>:
 * - ProductVariant: Entity quản lý (bảng product_variant)
 * - Long: Kiểu dữ liệu của khóa chính (@Id)
 * 
 * Tự động có sẵn: save(), findAll(), findById(), deleteById(), count()...
 */
public interface ProductVariantRepository extends JpaRepository<ProductVariant, String> {
    
}

package com.smartfashion.erp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.smartfashion.erp.entity.Material;

/**
 * MaterialRepository - Quản lý chất liệu vải
 */
public interface MaterialRepository extends JpaRepository<Material, String> {
    
}

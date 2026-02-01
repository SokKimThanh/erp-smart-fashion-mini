package com.smartfashion.erp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.smartfashion.erp.entity.Category;

/**
 * CategoryRepository - Quản lý danh mục sản phẩm
 */
public interface CategoryRepository extends JpaRepository<Category, String> {

}

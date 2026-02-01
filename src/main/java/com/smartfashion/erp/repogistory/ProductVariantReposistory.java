package com.smartfashion.erp.repogistory;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smartfashion.erp.entity.ProductVariant;

public interface ProductVariantReposistory extends JpaRepository<ProductVariant, String> {

}

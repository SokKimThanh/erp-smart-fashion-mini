package com.smartfashion.erp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartfashion.erp.entity.ProductVariant;
import com.smartfashion.erp.repository.ProductVariantRepository;

@RestController
@RequestMapping("/api/variants")
public class ProductVariantController {
    @Autowired
    private ProductVariantRepository repository;

    @PostMapping // Lệnh để thêm mới dữ liệu
    public ProductVariant create(@RequestBody ProductVariant variant) {
        // Tạm thời lưu trực tiếp, tuần 3 chúng ta sẽ thêm logic tính giá ở đây
        return repository.save(variant);
    }

    @GetMapping // Lệnh để lấy danh sách dữ liệu
    public List<ProductVariant> getAll() {
        return repository.findAll();
    }
}
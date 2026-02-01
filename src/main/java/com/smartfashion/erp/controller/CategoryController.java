package com.smartfashion.erp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartfashion.erp.entity.Category;
import com.smartfashion.erp.entity.ProductVariant;
import com.smartfashion.erp.repogistory.CategoryRepository;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    @Autowired
    private CategoryRepository repository;

    @PostMapping // Lệnh để thêm mới dữ liệu
    public ProductVariant create(@RequestBody ProductVariant variant) {
        // Tạm thời lưu trực tiếp, tuần 3 chúng ta sẽ thêm logic tính giá ở đây
        return repository.save(variant);
    }

    @GetMapping
    public List<Category> getAll() {
        return repository.findAll();
    }
}
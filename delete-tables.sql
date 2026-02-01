-- Script xóa dữ liệu từ 3 bảng: product_variant, product, category
-- Chạy script này trong MySQL Workbench hoặc command line

-- Tắt kiểm tra foreign key để tránh lỗi ràng buộc
SET FOREIGN_KEY_CHECKS = 0;

-- Xóa dữ liệu từ bảng product_variant (bảng con)
TRUNCATE TABLE product_variant;

-- Xóa dữ liệu từ bảng product
TRUNCATE TABLE product;

-- Xóa dữ liệu từ bảng category
TRUNCATE TABLE category;

-- Bật lại kiểm tra foreign key
SET FOREIGN_KEY_CHECKS = 1;

-- Kiểm tra kết quả
SELECT 'product_variant' AS table_name, COUNT(*) AS row_count FROM product_variant
UNION ALL
SELECT 'product' AS table_name, COUNT(*) AS row_count FROM product
UNION ALL
SELECT 'category' AS table_name, COUNT(*) AS row_count FROM category;

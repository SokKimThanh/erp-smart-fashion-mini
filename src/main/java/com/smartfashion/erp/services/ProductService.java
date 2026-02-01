package com.smartfashion.erp.services;

import com.smartfashion.erp.entity.Product;
import com.smartfashion.erp.entity.ProductVariant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;

/**
 * Service Layer - Logic Nghiệp vụ
 * Xử lý các quy tắc kinh doanh đặc thù cho sản phẩm thời trang
 * 
 * Configuration-driven: Tất cả các tham số có thể cấu hình qua application.properties
 */
@Service
public class ProductService {

    // Đọc tỷ lệ phụ phí từ application.properties
    @Value("${product.pricing.oversize.surcharge-rate:0.15}")
    private double oversizeSurchargeRate;

    // Đọc danh sách size lớn từ application.properties
    @Value("${product.pricing.oversize.sizes:3XL,3X,4XL,4X,5XL,5X,6XL,6X,7XL,7X,8XL,8X}")
    private String oversizeSizesConfig;

    /**
     * Tính giá sản phẩm dựa theo size
     * 
     * Quy tắc: Size lớn từ 3X đến 8X sẽ cộng thêm 15% phụ phí
     * - Size S, M, L, XL, 2XL: Giá gốc
     * - Size 3XL, 4XL, 5XL, 6XL, 7XL, 8XL: Giá gốc + 15%
     * 
     * @param basePrice Giá gốc của sản phẩm
     * @param size Kích thước sản phẩm
     * @return Giá cuối cùng sau khi tính toán
     */
    public BigDecimal calculatePrice(BigDecimal basePrice, String size) {
        if (basePrice == null || size == null) {
            throw new IllegalArgumentException("Giá và size không được null");
        }

        // ============== LOGIC TÍNH PHỤ PHÍ SIZE LớN ==============
        // Nếu size > 2X thì cộng thêm % phụ phí (cấu hình động)
        if (isOversizeProduct(size)) {
            // Tính phụ phí theo tỷ lệ cấu hình
            BigDecimal surcharge = basePrice.multiply(new BigDecimal(String.valueOf(oversizeSurchargeRate)));
            // Giá cuối = Giá gốc + Phụ phí
            BigDecimal finalPrice = basePrice.add(surcharge);
            
            return finalPrice.setScale(2, RoundingMode.HALF_UP);
        }
        // =========================================================

        // Size thông thường: trả về giá gốc
        return basePrice.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Kiểm tra xem size có phải là size lớn (> 2X) không
     * 
     * @param size Kích thước cần kiểm tra
     * @return true nếu size >= 3X, false nếu ngược lại
     */
    private boolean isOversizeProduct(String size) {
        if (size == null) {
            return false;
        }

        // Chuẩn hóa size (loại bỏ khoảng trắng, chuyển chữ hoa)
        String normalizedSize = size.trim().toUpperCase();

        // Đọc danh sách size lớn từ cấu hình (có thể thay đổi trong application.properties)
        List<String> oversizes = Arrays.asList(oversizeSizesConfig.split(","));

        for (String oversize : oversizes) {
            if (normalizedSize.equals(oversize.trim().toUpperCase())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Tính PHỤ PHÍ cho một size cụ thể
     * 
     * @param basePrice Giá gốc
     * @param size Kích thước
     * @return Số tiền phụ phí (0 nếu size thông thường)
     */
    public BigDecimal calculateSurcharge(BigDecimal basePrice, String size) {
        if (basePrice == null || size == null) {
            return BigDecimal.ZERO;
        }

        if (isOversizeProduct(size)) {
            return basePrice.multiply(new BigDecimal(String.valueOf(oversizeSurchargeRate)))
                           .setScale(2, RoundingMode.HALF_UP);
        }

        return BigDecimal.ZERO;
    }

    /**
     * Tính giá cho một biến thể sản phẩm
     * 
     * @param variant Biến thể sản phẩm (chứa thông tin size, màu sắc)
     * @param basePrice Giá gốc
     * @return Giá cuối cùng
     */
    public BigDecimal calculateVariantPrice(ProductVariant variant, BigDecimal basePrice) {
        if (variant == null) {
            throw new IllegalArgumentException("ProductVariant không được null");
        }

        return calculatePrice(basePrice, variant.getSize());
    }

    /**
     * ⭐ ÁP DỤNG QUY TẮC TÍNH GIÁ VÀ CẬP NHẬT VÀO ENTITY ⭐
     * 
     * Method này sẽ:
     * 1. Tính phụ phí dựa trên size
     * 2. Cập nhật vào variant.priceAdjustment
     * 3. Đảm bảo dữ liệu đồng bộ với DB
     * 
     * Gọi method này TRƯỚC KHI lưu ProductVariant vào database!
     * 
     * @param variant Biến thể sản phẩm cần áp dụng quy tắc giá
     */
    public void applyPricingRules(ProductVariant variant) {
        if (variant == null) {
            throw new IllegalArgumentException("ProductVariant không được null");
        }

        // Lấy giá gốc từ variant
        Double basePrice = variant.getBasePrice();
        if (basePrice == null) {
            basePrice = 0.0;
        }

        // Tính phụ phí dựa trên size
        BigDecimal surcharge = calculateSurcharge(
            new BigDecimal(String.valueOf(basePrice)), 
            variant.getSize()
        );

        // ⭐ CẬP NHẬT PHỤ PHÍ VÀO ENTITY
        variant.setPriceAdjustment(surcharge.doubleValue());

        // Log để debug (có thể xóa sau)
        System.out.printf("[Pricing] Size: %s | Base: %.2f | Surcharge: %.2f | Total: %.2f%n",
            variant.getSize(), basePrice, surcharge.doubleValue(), variant.getTotalPrice());
    }

    /**
     * Kiểm tra tồn kho trước khi đặt hàng
     * 
     * @param variant Biến thể sản phẩm
     * @param requestedQuantity Số lượng yêu cầu
     * @return true nếu đủ hàng, false nếu không đủ
     */
    public boolean checkInventoryAvailability(ProductVariant variant, int requestedQuantity) {
        if (variant == null) {
            throw new IllegalArgumentException("ProductVariant không được null");
        }

        // Giả sử ProductVariant có field stockQuantity
        // return variant.getStockQuantity() >= requestedQuantity;
        
        // TODO: Implement khi đã có field stockQuantity trong entity
        return true;
    }
}

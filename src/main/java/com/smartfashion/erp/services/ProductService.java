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
     * Tính giá bán (price_sell) dựa theo size
     * 
     * Quy tắc: Size lớn từ 3X đến 8X sẽ cộng thêm 15% phụ phí
     * - Size S, M, L, XL, 2XL: Giá nhập + Lợi nhuận cơ bản
     * - Size 3XL, 4XL, 5XL, 6XL, 7XL, 8XL: (Giá nhập + Lợi nhuận) * 1.15
     * 
     * @param priceImport Giá nhập của sản phẩm
     * @param size Kích thước sản phẩm
     * @return Giá bán cuối cùng
     */
    public BigDecimal calculatePriceSell(BigDecimal priceImport, String size) {
        if (priceImport == null || size == null) {
            throw new IllegalArgumentException("Giá nhập và size không được null");
        }

        // Giả sử lợi nhuận cơ bản 30%
        BigDecimal baseProfit = priceImport.multiply(new BigDecimal("0.30"));
        BigDecimal basePriceSell = priceImport.add(baseProfit);

        // ============== LOGIC TÍNH PHỤ PHÍ SIZE LớN ==============
        // Nếu size > 2X thì cộng thêm % phụ phí (cấu hình động)
        if (isOversizeProduct(size)) {
            // Tính phụ phí theo tỷ lệ cấu hình
            BigDecimal surcharge = basePriceSell.multiply(new BigDecimal(String.valueOf(oversizeSurchargeRate)));
            // Giá cuối = Giá bán cơ bản + Phụ phí
            BigDecimal finalPrice = basePriceSell.add(surcharge);
            
            return finalPrice.setScale(2, RoundingMode.HALF_UP);
        }
        // =========================================================

        // Size thông thường: trả về giá bán cơ bản
        return basePriceSell.setScale(2, RoundingMode.HALF_UP);
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
     * ⭐ ÁP DỤNG QUY TẮC TÍNH GIÁ BÁN VÀ CẬP NHẬT VÀO ENTITY ⭐
     * 
     * Theo ERD: Tự động tính price_sell dựa trên price_import và size
     * 
     * @param variant Biến thể sản phẩm cần áp dụng quy tắc giá
     */
    public void applyPricingRules(ProductVariant variant) {
        if (variant == null) {
            throw new IllegalArgumentException("ProductVariant không được null");
        }

        // Lấy giá nhập từ variant
        BigDecimal priceImport = variant.getPriceImport();
        if (priceImport == null) {
            throw new IllegalArgumentException("price_import không được null");
        }

        // ⭐ TÍNH GIÁ BÁN dựa trên size
        BigDecimal priceSell = calculatePriceSell(priceImport, variant.getSize());
        
        // Cập nhật vào entity
        variant.setPriceSell(priceSell);

        // Log để debug
        System.out.printf("[Pricing] Size: %s | Import: %s | Sell: %s | Markup: %.1f%%%n",
            variant.getSize(), 
            priceImport, 
            priceSell,
            priceSell.subtract(priceImport).divide(priceImport, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100")).doubleValue());
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

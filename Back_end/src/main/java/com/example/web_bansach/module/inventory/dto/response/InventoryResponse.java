package com.example.web_bansach.module.inventory.dto.response;

import lombok.Data;

@Data
// Trả thông tin sách và số lượng tồn kho cho frontend.
public class InventoryResponse {
    private Long inventoryId;
    private Long bookId;
    private String bookTitle;
    private String coverImage;
    private Integer quantity;
    private Boolean inStock;
    private Boolean isLowStock;
    private Integer lowStockThreshold;
}

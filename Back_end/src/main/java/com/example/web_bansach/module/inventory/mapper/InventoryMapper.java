package com.example.web_bansach.module.inventory.mapper;

import org.springframework.stereotype.Component;

import com.example.web_bansach.module.inventory.dto.response.InventoryResponse;
import com.example.web_bansach.module.inventory.entity.Inventory;

/**
 * Mapper xử lý mapping Inventory entity sang InventoryResponse
 */
@Component
public class InventoryMapper {

    private static final int DEFAULT_LOW_STOCK_THRESHOLD = 5;

    /**
     * Map Inventory entity sang InventoryResponse
     */
    public InventoryResponse mapToResponse(Inventory inventory) {
        if (inventory == null) {
            return null;
        }

        InventoryResponse response = new InventoryResponse();
        response.setInventoryId(inventory.getId());

        if (inventory.getBook() != null) {
            response.setBookId(inventory.getBook().getId());
            response.setBookTitle(inventory.getBook().getTitle());
            response.setCoverImage(inventory.getBook().getCoverImage());
        }

        Integer quantity = inventory.getQuantity();
        response.setQuantity(quantity);
        response.setInStock(quantity != null && quantity > 0);
        response.setLowStockThreshold(DEFAULT_LOW_STOCK_THRESHOLD);
        response.setIsLowStock(quantity != null && quantity <= DEFAULT_LOW_STOCK_THRESHOLD);

        return response;
    }
}

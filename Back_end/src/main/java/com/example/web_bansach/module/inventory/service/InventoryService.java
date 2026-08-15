package com.example.web_bansach.module.inventory.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.web_bansach.common.exception.BusinessException;
import com.example.web_bansach.common.exception.ResourceNotFoundException;
import com.example.web_bansach.module.inventory.dto.response.InventoryResponse;
import com.example.web_bansach.module.inventory.entity.Inventory;
import com.example.web_bansach.module.inventory.mapper.InventoryMapper;
import com.example.web_bansach.module.inventory.repository.InventoryRepository;

@Service
// Quản lý việc đọc, thiết lập, điều chỉnh và đối soát tồn kho.
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryMapper inventoryMapper;

    public InventoryService(InventoryRepository inventoryRepository, InventoryMapper inventoryMapper) {
        this.inventoryRepository = inventoryRepository;
        this.inventoryMapper = inventoryMapper;
    }

    @Transactional(readOnly = true)
    public InventoryResponse getByBookId(Long bookId) {
        Inventory inv = inventoryRepository.findByBookId(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bản ghi tồn kho cho sách"));

        return inventoryMapper.mapToResponse(inv);
    }

    @Transactional(readOnly = true)
    public java.util.List<Inventory> getAll() {
        return inventoryRepository.findAll();
    }

    @Transactional
    public Inventory setQuantity(Long inventoryId, Integer quantity) {
        if (quantity == null || quantity < 0) {
            throw new BusinessException("Số lượng tồn kho không được âm");
        }

        Inventory inv = inventoryRepository.findByIdForUpdate(inventoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy inventory"));
        inv.setQuantity(quantity);
        return inventoryRepository.save(inv);
    }

    @Transactional
    public Inventory adjustQuantity(Long inventoryId, int delta) {
        Inventory inv = inventoryRepository.findByIdForUpdate(inventoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy inventory"));
        int newQty = (inv.getQuantity() == null ? 0 : inv.getQuantity()) + delta;
        if (newQty < 0) {
            throw new BusinessException("Số lượng tồn kho không thể âm");
        }
        inv.setQuantity(newQty);
        return inventoryRepository.save(inv);
    }

    @Transactional
    public Inventory reconcileQuantity(Long inventoryId, Integer actualQuantity) {
        if (actualQuantity == null || actualQuantity < 0) {
            throw new BusinessException("Số lượng đối soát không được âm");
        }

        Inventory inv = inventoryRepository.findByIdForUpdate(inventoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy inventory"));
        inv.setQuantity(actualQuantity);
        return inventoryRepository.save(inv);
    }
}

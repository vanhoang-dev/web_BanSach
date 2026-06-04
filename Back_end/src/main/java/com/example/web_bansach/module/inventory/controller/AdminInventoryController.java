package com.example.web_bansach.module.inventory.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.web_bansach.common.response.ApiResponse;
import com.example.web_bansach.module.inventory.entity.Inventory;
import com.example.web_bansach.module.inventory.service.InventoryService;

@RestController
@RequestMapping("/admin/inventory")
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminInventoryController {

    @Autowired
    private InventoryService inventoryService;

    @GetMapping
    public ResponseEntity<ApiResponse<?>> listAll() {
        return ResponseEntity.ok(ApiResponse.success(inventoryService.getAll()));
    }

    @PutMapping("/{id}/set/{quantity}")
    public ResponseEntity<ApiResponse<Inventory>> setQuantity(@PathVariable Long id, @PathVariable Integer quantity) {
        Inventory updated = inventoryService.setQuantity(id, quantity);
        return ResponseEntity.ok(ApiResponse.success(updated));
    }

    @PostMapping("/{id}/adjust/{delta}")
    public ResponseEntity<ApiResponse<Inventory>> adjustQuantity(@PathVariable Long id, @PathVariable int delta) {
        Inventory updated = inventoryService.adjustQuantity(id, delta);
        return ResponseEntity.ok(ApiResponse.success(updated));
    }

    @PutMapping("/{id}/reconcile/{quantity}")
    public ResponseEntity<ApiResponse<Inventory>> reconcileQuantity(@PathVariable Long id, @PathVariable Integer quantity) {
        Inventory updated = inventoryService.reconcileQuantity(id, quantity);
        return ResponseEntity.ok(ApiResponse.success(updated));
    }
}

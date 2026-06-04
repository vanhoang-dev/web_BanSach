package com.example.web_bansach.module.inventory.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.web_bansach.common.response.ApiResponse;
import com.example.web_bansach.module.inventory.dto.response.InventoryResponse;
import com.example.web_bansach.module.inventory.service.InventoryService;

@RestController
@RequestMapping("/user/inventory")
@PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
public class UserInventoryController {

    @Autowired
    private InventoryService inventoryService;

    @GetMapping("/book/{bookId}")
    public ResponseEntity<ApiResponse<InventoryResponse>> getInventoryByBook(@PathVariable Long bookId) {
        return ResponseEntity.ok(ApiResponse.success(inventoryService.getByBookId(bookId)));
    }
}

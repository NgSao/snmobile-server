package com.snd.server.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.snd.server.constant.ApiPathConstant;
import com.snd.server.dto.request.InventoryRequest;
import com.snd.server.dto.response.InventoryResponse;
import com.snd.server.service.InventoryService;

@RestController
@RequestMapping(ApiPathConstant.API_PREFIX)
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping("/warehouse/inventories/import")
    public ResponseEntity<InventoryResponse> importInventory(@RequestBody InventoryRequest request) {
        return ResponseEntity.ok(inventoryService.importInventory(request));
    }

    @PostMapping("/warehouse/inventories/export")
    public ResponseEntity<InventoryResponse> exportInventory(@RequestBody InventoryRequest request) {
        return ResponseEntity.ok(inventoryService.exportInventory(request));
    }

    @GetMapping("/warehouse/inventories")
    public ResponseEntity<List<InventoryResponse>> getAllInventories(
            @RequestParam(defaultValue = "all") String sortByStock) {
        return ResponseEntity.ok(inventoryService.getAllInventories(sortByStock));
    }
}
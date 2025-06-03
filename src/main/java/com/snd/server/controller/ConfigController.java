package com.snd.server.controller;

import com.snd.server.dto.request.ConfigDetailRequest;
import com.snd.server.dto.request.ConfigRequest;
import com.snd.server.dto.response.ConfigDetailResponse;
import com.snd.server.dto.response.ConfigResponse;
import com.snd.server.service.ConfigService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/admin/configs")
public class ConfigController {

    private final ConfigService configService;

    public ConfigController(ConfigService configService) {
        this.configService = configService;
    }

    @GetMapping
    public ResponseEntity<List<ConfigResponse>> getAllConfigs() {
        return ResponseEntity.ok().body(configService.getAllConfigs());
    }

    @GetMapping("/by-ids")
    public ResponseEntity<List<ConfigResponse>> getConfigsByIds(@RequestParam List<Long> ids) {
        return ResponseEntity.ok().body(configService.getConfigsByIds(ids));
    }

    @PostMapping
    public ResponseEntity<ConfigResponse> createConfig(@RequestBody ConfigRequest request) {
        return ResponseEntity.ok().body(configService.createConfig(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConfigResponse> updateConfig(@PathVariable Long id, @RequestBody ConfigRequest request) {
        return ResponseEntity.ok().body(configService.updateConfig(id, request));
    }

    @PutMapping("/{id}/hide")
    public ResponseEntity<Boolean> hiddenConfig(@PathVariable Long id) {
        configService.hiddenConfig(id);
        return ResponseEntity.ok().body(true);
    }

    @PutMapping("/{id}/restore")
    public ResponseEntity<Boolean> restoreConfig(@PathVariable Long id) {
        configService.restoreConfig(id);
        return ResponseEntity.ok().body(true);
    }

    @PutMapping("/{id}/trash")
    public ResponseEntity<Boolean> trashConfig(@PathVariable Long id) {
        configService.trashConfig(id);
        return ResponseEntity.ok().body(true);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteConfig(@PathVariable Long id) {
        configService.deleteConfig(id);
        return ResponseEntity.ok().body(true);
    }

    @GetMapping("/branches")
    public ResponseEntity<ConfigDetailResponse> getBranches(@PathVariable Long id) {
        return ResponseEntity.ok().body(configService.getBranch(id));
    }

    @PostMapping("/{configId}/branches")
    public ResponseEntity<ConfigDetailResponse> addBranch(@PathVariable Long configId,
            @RequestBody ConfigDetailRequest request) {
        return ResponseEntity.ok().body(configService.addBranch(configId, request));
    }

    @PutMapping("/branches/{branchId}")
    public ResponseEntity<ConfigDetailResponse> updateBranch(@PathVariable Long branchId,
            @RequestBody ConfigDetailRequest request) {
        return ResponseEntity.ok().body(configService.updateBranch(branchId, request));
    }

    @PutMapping("/branches/hide")
    public ResponseEntity<Boolean> hiddenBranches(@RequestBody Set<Long> branchIds) {
        configService.hiddenBranches(branchIds);
        return ResponseEntity.ok().body(true);
    }

    @PutMapping("/branches/trash")
    public ResponseEntity<Boolean> trashBranches(@RequestBody Set<Long> branchIds) {
        configService.trashBranches(branchIds);
        return ResponseEntity.ok().body(true);
    }

    @PutMapping("/branches/restore")
    public ResponseEntity<Boolean> restoreBranches(@RequestBody Set<Long> branchIds) {
        configService.restoreBranches(branchIds);
        return ResponseEntity.ok().body(true);
    }

    @DeleteMapping("/branches")
    public ResponseEntity<Boolean> deleteBranches(@RequestBody Set<Long> branchIds) {
        configService.deleteBranches(branchIds);
        return ResponseEntity.ok().body(true);
    }

}

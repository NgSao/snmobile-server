package com.snd.server.controller;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.snd.server.constant.ApiPathConstant;
import com.snd.server.dto.request.BrandRequest;
import com.snd.server.dto.response.AdminBrandReponse;
import com.snd.server.dto.response.CustomerBrandResponse;
import com.snd.server.dto.response.DeleteResponse;
import com.snd.server.dto.response.SimplifiedPageResponse;
import com.snd.server.enums.EntityStatusEnumRequest;
import com.snd.server.service.BrandService;

@RestController
@RequestMapping(ApiPathConstant.API_PREFIX)
public class BrandController {
    private final BrandService brandService;

    public BrandController(BrandService brandService) {
        this.brandService = brandService;
    }

    // Customer
    @GetMapping("/public/brands")
    public ResponseEntity<List<CustomerBrandResponse>> getAllBrandsForCustomer() {
        return ResponseEntity.ok().body(brandService.customerGetAllBrands());
    }

    @GetMapping("/store-management/brands")
    public ResponseEntity<SimplifiedPageResponse<AdminBrandReponse>> getAllAdminBrands(
            @RequestParam(required = false) EntityStatusEnumRequest status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        Pageable pageable = PageRequest.of(page - 1, limit);
        return ResponseEntity.ok().body(brandService.adminGetAllBrands(status,
                pageable));
    }

    @GetMapping("/store-management/brands/{id}")
    public ResponseEntity<AdminBrandReponse> getBrandById(@PathVariable Long id) {
        return ResponseEntity.ok().body(brandService.getByIdBrand(id));
    }

    @PostMapping("/store-management/brands/created")
    public ResponseEntity<AdminBrandReponse> createBrand(@RequestBody BrandRequest request) {
        return ResponseEntity.ok().body(brandService.createBrand(request));
    }

    @PutMapping("/store-management/brands/updated/{id}")
    public ResponseEntity<AdminBrandReponse> updateBrand(@PathVariable Long id, @RequestBody BrandRequest request) {
        return ResponseEntity.ok().body(brandService.updateBrand(id, request));
    }

    @PutMapping("/store-management/brands/hide")
    public ResponseEntity<Void> hide(@RequestBody Set<Long> ids) {
        brandService.hidden(ids);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/store-management/brands/restore")
    public ResponseEntity<Void> restore(@RequestBody Set<Long> ids) {
        brandService.restore(ids);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/store-management/brands/delete")
    public ResponseEntity<DeleteResponse> delete(@RequestBody Set<Long> ids) {
        return ResponseEntity.ok().body(brandService.delete(ids));
    }

}

package com.snd.server.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.snd.server.constant.ApiPathConstant;
import com.snd.server.dto.request.ProductRequest;
import com.snd.server.dto.request.VariantRequest;
import com.snd.server.dto.response.AdminProductResponse;
import com.snd.server.dto.response.CustomerProductResponse;
import com.snd.server.dto.response.ProductColorResponse;
import com.snd.server.dto.response.SimplifiedPageResponse;
import com.snd.server.dto.response.VariantResponse;
import com.snd.server.enums.EntityStatusEnumRequest;
import com.snd.server.service.ProductService;

@RestController
@RequestMapping(ApiPathConstant.API_PREFIX)
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/public/products")
    public ResponseEntity<List<CustomerProductResponse>> getAllProducts() {
        return ResponseEntity.ok().body(productService.getAllProducts());
    }

    @GetMapping("/public/products/hot")
    public ResponseEntity<List<CustomerProductResponse>> getAllProductshHot() {
        return ResponseEntity.ok().body(productService.getAllProductsHot());
    }

    @GetMapping("/public/products/new")
    public ResponseEntity<List<CustomerProductResponse>> getAllProductsNew() {
        return ResponseEntity.ok().body(productService.getAllProductsNew());
    }

    @GetMapping("/public/products/colors")
    public ResponseEntity<SimplifiedPageResponse<ProductColorResponse>> getAllPageProducts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("id"));
        SimplifiedPageResponse<ProductColorResponse> productPage = productService.getAllPageProducts(pageable);
        return ResponseEntity.ok().body(productPage);
    }

    @GetMapping("/public/products/{id}")
    public ResponseEntity<CustomerProductResponse> getProduct(@PathVariable Long id) {
        return ResponseEntity.ok().body(productService.getByIdProduct(id));
    }

    @GetMapping("/public/products/search")
    public ResponseEntity<List<CustomerProductResponse>> searchProducts(
            @RequestParam(value = "query", required = false) String query) {
        return ResponseEntity.ok().body(productService.searchProducts(query));
    }

    @GetMapping("/store-management/products")
    public ResponseEntity<SimplifiedPageResponse<AdminProductResponse>> adminGetAll(
            @RequestParam(required = false) EntityStatusEnumRequest status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        Pageable pageable = PageRequest.of(page - 1, limit);
        return ResponseEntity.ok().body(productService.adminGetAll(status, pageable));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_NHAN_VIEN_BAN_HANG')")
    @GetMapping("/store-management/products/{id}")
    public ResponseEntity<AdminProductResponse> getProductAdmin(@PathVariable Long id) {
        return ResponseEntity.ok().body(productService.adminGetById(id));
    }

    @PostMapping("/store-management/products/created")
    public ResponseEntity<AdminProductResponse> createProduct(@RequestBody ProductRequest request) {
        return ResponseEntity.ok().body(productService.createProduct(request));
    }

    @PutMapping("/store-management/products/updated/{id}")
    public ResponseEntity<AdminProductResponse> updateProduct(@PathVariable Long id,
            @RequestBody ProductRequest request) {
        return ResponseEntity.ok().body(productService.updateProduct(id, request));
    }

    @PostMapping("/store-management/variants/created")
    public ResponseEntity<List<VariantResponse>> addVariants(@RequestBody List<VariantRequest> requests) {
        List<VariantResponse> createdVariants = requests.stream()
                .map(productService::addVariant)
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(createdVariants);
    }
}

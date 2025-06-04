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
import com.snd.server.dto.request.CategoryRequest;
import com.snd.server.dto.response.AdminCategoryReponse;
import com.snd.server.dto.response.CategoryResponse;
import com.snd.server.dto.response.DeleteResponse;
import com.snd.server.dto.response.SimplifiedPageResponse;
import com.snd.server.enums.EntityStatusEnumRequest;
import com.snd.server.service.CategoryService;

@RestController
@RequestMapping(ApiPathConstant.API_PREFIX)
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // Customer
    @GetMapping("/public/categories")
    public ResponseEntity<List<CategoryResponse>> getAllCategorysForCustomer() {
        return ResponseEntity.ok().body(categoryService.customerGetAll());
    }

    @GetMapping("/store-management/categories")
    public ResponseEntity<SimplifiedPageResponse<AdminCategoryReponse>> getAllAdminCategorys(
            @RequestParam(required = false) EntityStatusEnumRequest status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        Pageable pageable = PageRequest.of(page - 1, limit);
        return ResponseEntity.ok().body(categoryService.adminGetAll(status,
                pageable));
    }

    @GetMapping("/store-management/categories/{id}")
    public ResponseEntity<AdminCategoryReponse> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok().body(categoryService.getById(id));
    }

    @PostMapping("/store-management/categories/created")
    public ResponseEntity<AdminCategoryReponse> createCategory(@RequestBody CategoryRequest request) {
        return ResponseEntity.ok().body(categoryService.create(request));
    }

    @PutMapping("/store-management/categories/updated/{id}")
    public ResponseEntity<AdminCategoryReponse> updateCategory(@PathVariable Long id,
            @RequestBody CategoryRequest request) {
        return ResponseEntity.ok().body(categoryService.update(id, request));
    }

    @PutMapping("/store-management/categories/hide")
    public ResponseEntity<Void> hide(@RequestBody Set<Long> ids) {
        categoryService.hidden(ids);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/store-management/categories/restore")
    public ResponseEntity<Void> restore(@RequestBody Set<Long> ids) {
        categoryService.restore(ids);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/store-management/categories/delete")
    public ResponseEntity<DeleteResponse> delete(@RequestBody Set<Long> ids) {
        return ResponseEntity.ok().body(categoryService.delete(ids));
    }

}

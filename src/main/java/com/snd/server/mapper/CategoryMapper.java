package com.snd.server.mapper;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.snd.server.dto.request.CategoryRequest;
import com.snd.server.dto.response.AdminCategoryReponse;
import com.snd.server.dto.response.CategoryResponse;
import com.snd.server.exception.AppException;
import com.snd.server.model.Category;
import com.snd.server.repository.CategoryRepository;
import com.snd.server.utils.SlugUtil;

@Component
public class CategoryMapper {
    private final CategoryRepository categoryRepository;

    public CategoryMapper(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public CategoryResponse categoryToDto(Category category) {
        CategoryResponse dto = new CategoryResponse();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setSlug(category.getSlug());
        dto.setImageUrl(category.getImageUrl());
        dto.setParentId(category.getParent() != null ? category.getParent().getId() : null);
        dto.setDisplayOrder(category.getDisplayOrder());
        dto.setChildren(category.getChildren().stream()
                .map(this::categoryToDto)
                .collect(Collectors.toSet()));
        return dto;
    }

    public AdminCategoryReponse adminCategoryToDto(Category category) {
        AdminCategoryReponse dto = new AdminCategoryReponse();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setImageUrl(category.getImageUrl());
        dto.setProductCount(category.getProducts().size());
        dto.setParentId(category.getParent() != null ? category.getParent().getId() : null);
        dto.setParentName(category.getParent() != null ? category.getParent().getName() : null);
        dto.setCreatedAt(category.getCreatedAt());
        dto.setUpdatedAt(category.getUpdatedAt());
        dto.setCreatedBy(category.getCreatedBy());
        dto.setUpdatedBy(category.getUpdatedBy());
        dto.setStatus(category.getStatus());
        dto.setStatusName(category.getStatus().getStatusName());
        dto.setChildren(category.getChildren().stream()
                .map(this::categoryToDto)
                .collect(Collectors.toSet()));
        return dto;
    }

    public Category categoryToEntity(CategoryRequest request) {
        Category category = new Category();
        category.setName(request.getName());
        category.setSlug(SlugUtil.toSlug(request.getName()));
        category.setImageUrl(request.getImageUrl());
        category.setDisplayOrder(request.getDisplayOrder());
        if (request.getParentId() != null) {
            category.setParent(categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new AppException("Parent category not found")));
        }
        if (request.getStatus() != null) {
            category.setStatus(request.getStatus());
        }
        return category;
    }

    public Category categoryToUpdated(Category category, CategoryRequest request) {
        if (request.getName() != null) {
            category.setName(request.getName());
            category.setSlug(SlugUtil.toSlug(request.getName()));
        }
        if (request.getImageUrl() != null) {
            category.setImageUrl(request.getImageUrl());
        }
        if (request.getDisplayOrder() != null) {
            category.setDisplayOrder(request.getDisplayOrder());
        }
        if (request.getParentId() != null) {
            category.setParent(categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new AppException("Parent category not found")));
        }
        if (request.getStatus() != null) {
            category.setStatus(request.getStatus());
        }
        return category;
    }

}

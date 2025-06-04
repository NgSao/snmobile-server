package com.snd.server.mapper;

import org.springframework.stereotype.Component;

import com.snd.server.dto.request.BrandRequest;
import com.snd.server.dto.response.AdminBrandReponse;
import com.snd.server.dto.response.BrandResponse;
import com.snd.server.dto.response.CustomerBrandResponse;
import com.snd.server.model.Brand;
import com.snd.server.utils.SlugUtil;

@Component
public class BrandMapper {

    public CustomerBrandResponse customerBrandToDto(Brand brand) {
        CustomerBrandResponse dto = new CustomerBrandResponse();
        dto.setId(brand.getId());
        dto.setName(brand.getName());
        dto.setImageUrl(brand.getImageUrl());
        return dto;
    }

    public AdminBrandReponse adminBrandToDto(Brand brand) {
        AdminBrandReponse dto = new AdminBrandReponse();
        dto.setId(brand.getId());
        dto.setName(brand.getName());
        dto.setSlug(brand.getName());
        dto.setImageUrl(brand.getImageUrl());
        dto.setDisplayOrder(brand.getDisplayOrder());
        dto.setProductCount(brand.getProducts().size());
        dto.setCreatedAt(brand.getCreatedAt());
        dto.setUpdatedAt(brand.getUpdatedAt());
        dto.setCreatedBy(brand.getCreatedBy());
        dto.setUpdatedBy(brand.getUpdatedBy());
        dto.setStatus(brand.getStatus());
        dto.setStatusName(brand.getStatus().getStatusName());

        return dto;
    }

    public BrandResponse brandToDto(Brand brand) {
        BrandResponse dto = new BrandResponse();
        dto.setId(brand.getId());
        dto.setName(brand.getName());
        dto.setSlug(brand.getSlug());
        dto.setImageUrl(brand.getImageUrl());
        dto.setDisplayOrder(brand.getDisplayOrder());
        return dto;
    }

    public Brand brandToEntity(BrandRequest request) {
        Brand brand = new Brand();
        brand.setName(request.getName());
        brand.setSlug(SlugUtil.toSlug(request.getName()));
        brand.setImageUrl(request.getImageUrl());
        brand.setDisplayOrder(request.getDisplayOrder());
        if (request.getStatus() != null) {
            brand.setStatus(request.getStatus());
        }
        return brand;
    }

    public Brand brandToUpdated(Brand brand, BrandRequest request) {
        if (request.getName() != null) {
            brand.setName(request.getName());
            brand.setSlug(SlugUtil.toSlug(request.getName()));
        }
        if (request.getImageUrl() != null) {
            brand.setImageUrl(request.getImageUrl());
        }
        if (request.getDisplayOrder() != null) {
            brand.setDisplayOrder(request.getDisplayOrder());
        }
        if (request.getStatus() != null) {
            brand.setStatus(request.getStatus());
        }
        return brand;
    }

}

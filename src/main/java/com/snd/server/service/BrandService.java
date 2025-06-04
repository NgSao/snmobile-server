package com.snd.server.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.snd.server.dto.request.BrandRequest;
import com.snd.server.dto.response.AdminBrandReponse;
import com.snd.server.dto.response.CustomerBrandResponse;
import com.snd.server.dto.response.DeleteResponse;
import com.snd.server.dto.response.SimplifiedPageResponse;
import com.snd.server.enums.EntityStatusEnum;
import com.snd.server.enums.EntityStatusEnumRequest;
import com.snd.server.exception.AppException;
import com.snd.server.mapper.BrandMapper;
import com.snd.server.model.Brand;
import com.snd.server.repository.BrandRepository;
import com.snd.server.repository.ProductRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BrandService {

    private final BrandRepository brandRepository;
    private final BrandMapper mapper;
    private final ProductRepository productRepository;

    public BrandService(BrandRepository brandRepository, BrandMapper mapper, ProductRepository productRepository) {
        this.brandRepository = brandRepository;
        this.mapper = mapper;
        this.productRepository = productRepository;
    }

    // Customer
    public List<CustomerBrandResponse> customerGetAllBrands() {
        return brandRepository.findByStatusOrderByDisplayOrderAsc(EntityStatusEnum.ACTIVE).stream()
                .map(mapper::customerBrandToDto)
                .collect(Collectors.toList());
    }

    // Admin
    public SimplifiedPageResponse<AdminBrandReponse> adminGetAllBrands(EntityStatusEnumRequest statusRequest,
            Pageable pageable) {
        Page<Brand> page;
        if (statusRequest != null) {
            EntityStatusEnum status = EntityStatusEnum.valueOf(statusRequest.name());
            page = brandRepository.findAllByStatusOrderByIdDesc(status, pageable);
        } else {
            List<EntityStatusEnum> defaultStatuses = List.of(EntityStatusEnum.ACTIVE,
                    EntityStatusEnum.HIDDEN);
            page = brandRepository.findAllByStatusInOrderByIdDesc(defaultStatuses,
                    pageable);
        }

        List<AdminBrandReponse> list = page.getContent().stream()
                .map(mapper::adminBrandToDto)
                .collect(Collectors.toList());

        Page<AdminBrandReponse> mappedPage = new PageImpl<>(list, pageable,
                page.getTotalElements());

        return new SimplifiedPageResponse<>(mappedPage);
    }

    public AdminBrandReponse getByIdBrand(Long id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new AppException("Brand not found"));
        return mapper.adminBrandToDto(brand);
    }

    public AdminBrandReponse createBrand(BrandRequest request) {
        if (brandRepository.existsByNameIgnoreCase(request.getName())) {
            throw new AppException("Brand name already exists");
        }
        Brand brand = brandRepository.save(mapper.brandToEntity(request));
        return mapper.adminBrandToDto(brand);
    }

    public AdminBrandReponse updateBrand(Long id, BrandRequest request) {
        Brand existing = brandRepository.findById(id)
                .orElseThrow(() -> new AppException("Brand not found"));
        if (request.getName() != null && brandRepository.existsByNameIgnoreCaseAndIdNot(request.getName(), id)) {
            throw new AppException("Brand name already exists");
        }
        Brand brand = brandRepository.save(mapper.brandToUpdated(existing, request));
        return mapper.adminBrandToDto(brand);
    }

    public void hidden(Set<Long> ids) {
        List<Brand> brands = brandRepository.findAllById(ids);
        if (brands.isEmpty()) {
            throw new AppException("Không tìm thấy thương hiệu nào với các ID đã cho.");
        }

        for (Brand brand : brands) {
            if (brand.getStatus() == EntityStatusEnum.DELETED || brand.getStatus() == EntityStatusEnum.TRASH) {
                throw new AppException("Không thể thay đổi trạng thái thương hiệu đã bị xóa hoặc trong thùng rác.");
            }
        }
        for (Brand brand : brands) {
            if (brand.getStatus() == EntityStatusEnum.HIDDEN) {
                brand.setStatus(EntityStatusEnum.ACTIVE);
            } else if (brand.getStatus() == EntityStatusEnum.ACTIVE) {
                long count = productRepository.countByBrandId(brand.getId());
                if (count > 0) {
                    throw new AppException("Không thể ẩn. Có sản phẩm đang sử dụng thương hiệu " + brand.getName());
                }
                brand.setStatus(EntityStatusEnum.HIDDEN);
            }
        }

        brandRepository.saveAll(brands);
    }

    public void restore(Set<Long> ids) {
        List<Brand> brands = brandRepository.findAllById(ids);
        if (brands.isEmpty()) {
            throw new AppException("Không tìm thấy thương hiệu nào với các ID đã cho.");
        }
        brands.forEach(brand -> {
            if (brand.getStatus() == EntityStatusEnum.TRASH) {
                brand.setStatus(EntityStatusEnum.ACTIVE);
            }
        });
        brandRepository.saveAll(brands);
    }

    public DeleteResponse delete(Set<Long> ids) {
        List<Brand> brands = brandRepository.findAllById(ids);
        if (brands.isEmpty()) {
            throw new AppException("Không tìm thấy thương hiệu nào với các ID đã cho.");
        }
        long count = productRepository.countByBrandIdIn(ids);
        if (count > 0) {
            throw new AppException("Không thể xóa. Có sản phẩm đang sử dụng các thương hiệu này.");
        }
        boolean changedToTrash = false;
        boolean changedToDeleted = false;
        boolean alreadyDeleted = false;
        for (Brand brand : brands) {
            switch (brand.getStatus()) {
                case ACTIVE:
                case HIDDEN:
                case TRIAL:
                    brand.setStatus(EntityStatusEnum.TRASH);
                    changedToTrash = true;
                    break;
                case TRASH:
                    brand.setStatus(EntityStatusEnum.DELETED);
                    changedToDeleted = true;
                    break;
                case DELETED:
                    alreadyDeleted = true;
                    break;
            }
        }
        brandRepository.saveAll(brands);
        if (changedToTrash) {
            return new DeleteResponse("Đã đưa vào thùng rác.");
        } else if (changedToDeleted) {
            return new DeleteResponse("Đã xóa thành công.");
        } else if (alreadyDeleted) {
            return new DeleteResponse("Đã bị xóa trước đó.");
        } else {
            return new DeleteResponse("Không có thương hiệu nào được xử lý.");
        }
    }

}

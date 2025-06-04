package com.snd.server.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.snd.server.dto.request.CategoryRequest;
import com.snd.server.dto.response.AdminCategoryReponse;
import com.snd.server.dto.response.CategoryResponse;
import com.snd.server.dto.response.DeleteResponse;
import com.snd.server.dto.response.SimplifiedPageResponse;
import com.snd.server.enums.EntityStatusEnum;
import com.snd.server.enums.EntityStatusEnumRequest;
import com.snd.server.exception.AppException;
import com.snd.server.mapper.CategoryMapper;
import com.snd.server.model.Category;
import com.snd.server.repository.CategoryRepository;
import com.snd.server.repository.ProductRepository;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper mapper;
    private final ProductRepository productRepository;

    public CategoryService(CategoryRepository categoryRepository, CategoryMapper mapper,
            ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.mapper = mapper;
        this.productRepository = productRepository;
    }

    // Customer
    public List<CategoryResponse> customerGetAll() {
        return categoryRepository.findByStatusOrderByDisplayOrderAsc(EntityStatusEnum.ACTIVE)
                .stream()
                .map(mapper::categoryToDto)
                .collect(Collectors.toList());
    }

    // Admin
    public SimplifiedPageResponse<AdminCategoryReponse> adminGetAll(EntityStatusEnumRequest statusRequest,
            Pageable pageable) {
        Page<Category> page;
        if (statusRequest != null) {
            EntityStatusEnum status = EntityStatusEnum.valueOf(statusRequest.name());
            page = categoryRepository.findAllByStatusOrderByIdDesc(status, pageable);
        } else {
            List<EntityStatusEnum> defaultStatuses = List.of(EntityStatusEnum.ACTIVE,
                    EntityStatusEnum.HIDDEN);
            page = categoryRepository.findAllByStatusInOrderByIdDesc(defaultStatuses,
                    pageable);
        }

        List<AdminCategoryReponse> list = page.getContent().stream()
                .map(mapper::adminCategoryToDto)
                .collect(Collectors.toList());
        Page<AdminCategoryReponse> mappedPage = new PageImpl<>(list, pageable,
                page.getTotalElements());

        return new SimplifiedPageResponse<>(mappedPage);
    }

    public AdminCategoryReponse getById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException("Category not found"));
        return mapper.adminCategoryToDto(category);
    }

    public AdminCategoryReponse create(CategoryRequest request) {
        if (categoryRepository.existsByNameIgnoreCase(request.getName())) {
            throw new AppException("Category name already exists");
        }
        Category category = categoryRepository.save(mapper.categoryToEntity(request));
        return mapper.adminCategoryToDto(category);
    }

    public AdminCategoryReponse update(Long id, CategoryRequest request) {
        Category existing = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException("Category not found"));
        if (request.getName() != null && categoryRepository.existsByNameIgnoreCaseAndIdNot(request.getName(), id)) {
            throw new AppException("Category name already exists");
        }
        Category category = categoryRepository.save(mapper.categoryToUpdated(existing, request));
        return mapper.adminCategoryToDto(category);
    }

    public void hidden(Set<Long> ids) {
        List<Category> categories = categoryRepository.findAllById(ids);
        if (categories.isEmpty()) {
            throw new AppException("Không tìm thấy với các ID đã cho.");
        }

        for (Category category : categories) {
            if (category.getStatus() == EntityStatusEnum.DELETED || category.getStatus() == EntityStatusEnum.TRASH) {
                throw new AppException("Không thể thay đổi trạng thái đã bị xóa hoặc trong thùng rác.");
            }
        }
        for (Category category : categories) {
            if (category.getStatus() == EntityStatusEnum.HIDDEN) {
                category.setStatus(EntityStatusEnum.ACTIVE);
            } else if (category.getStatus() == EntityStatusEnum.ACTIVE) {
                long count = productRepository.countByCategories_Id(category.getId());
                if (count > 0) {
                    throw new AppException("Không thể ẩn. Có sản phẩm " + category.getName());
                }
                category.setStatus(EntityStatusEnum.HIDDEN);
            }
        }

        categoryRepository.saveAll(categories);
    }

    public void restore(Set<Long> ids) {
        List<Category> categories = categoryRepository.findAllById(ids);
        if (categories.isEmpty()) {
            throw new AppException("Không tìm thấy  nào với các ID đã cho.");
        }
        categories.forEach(category -> {
            if (category.getStatus() == EntityStatusEnum.TRASH) {
                category.setStatus(EntityStatusEnum.ACTIVE);
            }
        });
        categoryRepository.saveAll(categories);
    }

    public DeleteResponse delete(Set<Long> ids) {
        List<Category> categories = categoryRepository.findAllById(ids);
        if (categories.isEmpty()) {
            throw new AppException("Không tìm thấy  nào với các ID đã cho.");
        }
        long count = productRepository.countByCategories_IdIn(ids);
        if (count > 0) {
            throw new AppException("Không thể xóa. Có sản phẩm đang sử dụng các thương hiệu này.");
        }
        boolean changedToTrash = false;
        boolean changedToDeleted = false;
        boolean alreadyDeleted = false;
        for (Category category : categories) {
            switch (category.getStatus()) {
                case ACTIVE:
                case HIDDEN:
                case TRIAL:
                    category.setStatus(EntityStatusEnum.TRASH);
                    changedToTrash = true;
                    break;
                case TRASH:
                    category.setStatus(EntityStatusEnum.DELETED);
                    changedToDeleted = true;
                    break;
                case DELETED:
                    alreadyDeleted = true;
                    break;
            }
        }
        categoryRepository.saveAll(categories);
        if (changedToTrash) {
            return new DeleteResponse("Đã đưa vào thùng rác.");
        } else if (changedToDeleted) {
            return new DeleteResponse("Đã xóa thành công.");
        } else if (alreadyDeleted) {
            return new DeleteResponse("Đã bị xóa trước đó.");
        } else {
            return new DeleteResponse("Không có đối tượng nào được xử lý.");
        }
    }
}

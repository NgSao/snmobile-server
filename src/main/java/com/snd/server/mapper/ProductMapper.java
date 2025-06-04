package com.snd.server.mapper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.snd.server.dto.request.MediaRequest;
import com.snd.server.dto.request.ProductRequest;
import com.snd.server.dto.request.VariantRequest;
import com.snd.server.dto.response.AdminProductResponse;
import com.snd.server.dto.response.BrandResponse;
import com.snd.server.dto.response.CustomerBrandResponse;
import com.snd.server.dto.response.CustomerCategoryResponse;
import com.snd.server.dto.response.CustomerProductResponse;
import com.snd.server.dto.response.CustomerVariantResponse;
import com.snd.server.dto.response.MediaResponse;
import com.snd.server.dto.response.ProductColorResponse;
import com.snd.server.dto.response.VariantResponse;
import com.snd.server.exception.AppException;
import com.snd.server.model.Brand;
import com.snd.server.model.Category;
import com.snd.server.model.Media;
import com.snd.server.model.Product;
import com.snd.server.model.Variant;
import com.snd.server.repository.BrandRepository;
import com.snd.server.repository.CategoryRepository;
import com.snd.server.repository.VariantRepository;
import com.snd.server.utils.SkuUtil;
import com.snd.server.utils.SlugUtil;

@Component
public class ProductMapper {

    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final BrandMapper brandMapper;
    private final VariantRepository variantRepository;

    public ProductMapper(BrandRepository brandRepository, CategoryRepository categoryRepository,
            CategoryMapper categoryMapper,
            BrandMapper brandMapper,
            VariantRepository variantRepository) {
        this.brandRepository = brandRepository;
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
        this.brandMapper = brandMapper;
        this.variantRepository = variantRepository;
    }

    public CustomerProductResponse customerProductToDto(Product product) {
        CustomerProductResponse response = new CustomerProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setSlug(product.getSlug());
        response.setSku(product.getSku());
        response.setDescription(product.getDescription());
        response.setSpecification(product.getSpecification());
        response.setPromotions(product.getPromotions());
        response.setOriginalPrice(product.getOriginalPrice());
        response.setSalePrice(product.getSalePrice());
        response.setStock(product.getStock());
        response.setSold(product.getSold());
        response.setRating(product.getRating());
        response.setRatingCount(product.getRatingCount());
        BigDecimal originalPrice = product.getOriginalPrice();
        BigDecimal salePrice = product.getSalePrice();
        if (originalPrice != null && salePrice != null && originalPrice.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal discount = originalPrice.subtract(salePrice)
                    .divide(originalPrice, 2, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
            response.setDiscount(discount.intValue());
        } else {
            response.setDiscount(0);
        }

        if (product.getImages() != null) {
            response.setImages(product.getImages().stream()
                    .map(Media::getImageUrl)
                    .collect(Collectors.toSet()));
        }
        if (product.getCategories() != null) {
            response.setCategories(product.getCategories().stream()
                    .map(this::customerCategoryToDto)
                    .collect(Collectors.toSet()));
        }

        if (product.getBrand() != null) {
            CustomerBrandResponse brandDto = new CustomerBrandResponse();
            brandDto.setId(product.getBrand().getId());
            brandDto.setName(product.getBrand().getName());
            response.setBrand(brandDto);
        }

        if (product.getVariants() != null) {
            response.setVariants(product.getVariants().stream()
                    .map(this::customerVariantToDto)
                    .collect(Collectors.toSet()));
        }

        return response;
    }

    private CustomerCategoryResponse customerCategoryToDto(Category category) {
        CustomerCategoryResponse categoryResponse = new CustomerCategoryResponse();
        categoryResponse.setId(category.getId());
        categoryResponse.setName(category.getName());
        return categoryResponse;
    }

    public CustomerVariantResponse customerVariantToDto(Variant variant) {
        CustomerVariantResponse variantResponse = new CustomerVariantResponse();
        variantResponse.setId(variant.getId());
        variantResponse.setSku(variant.getSku());
        variantResponse.setColor(variant.getColor());
        variantResponse.setStorage(variant.getSize());
        variantResponse.setPrice(variant.getSalePrice());
        variantResponse.setOriginalPrice(variant.getOriginalPrice());
        variantResponse.setStock(variant.getStockQuantity());
        variantResponse.setImage(variant.getImageUrl());
        BigDecimal originalPrice = variant.getOriginalPrice();
        BigDecimal salePrice = variant.getSalePrice();
        if (originalPrice != null && salePrice != null && originalPrice.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal discount = originalPrice.subtract(salePrice)
                    .divide(originalPrice, 2, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
            variantResponse.setDiscount(discount.intValue());
        } else {
            variantResponse.setDiscount(0);
        }

        return variantResponse;
    }

    public Product adminProductToEntity(ProductRequest request) {
        request.validatePrices();
        Product product = new Product();
        product.setName(request.getName());
        product.setSku(SkuUtil.generateSku(request.getName()));
        product.setSlug(SlugUtil.toSlug(request.getName()));
        product.setDescription(request.getDescription());
        product.setSpecification(request.getSpecification());
        product.setOriginalPrice(request.getOriginalPrice());
        product.setSalePrice(request.getSalePrice());
        product.setPromotions(request.getPromotions());
        product.setStock(request.getStock() != null ? request.getStock() : 0);
        if (request.getVariants() != null && !request.getVariants().isEmpty()) {
            product.setOriginalPrice(BigDecimal.ZERO);
            product.setSalePrice(BigDecimal.ZERO);
            product.setStock(request.getStock() != null ? request.getStock() : 0);

        } else {
            product.setOriginalPrice(request.getOriginalPrice());
            product.setSalePrice(request.getSalePrice());
        }

        product.setStock(request.getStock() != null ? request.getStock() : 0);
        if (request.getBrandId() != null) {
            product.setBrand(brandToId(request.getBrandId()));
        }

        if (request.getCategoryId() != null && !request.getCategoryId().isEmpty()) {
            product.setCategories(categoryToId(request.getCategoryId()));
        }

        if (request.getImages() != null && !request.getImages().isEmpty()) {
            product.setImages(productImageToEntity(request.getImages(), product));
        }

        if (request.getVariants() != null && !request.getVariants().isEmpty()) {
            product.setVariants(request.getVariants().stream()
                    .map(variantRequest -> variantToEntity(variantRequest, product))
                    .collect(Collectors.toSet()));
        }
        return product;
    }

    public Set<Category> categoryToId(Set<Long> categoryIds) {
        return categoryIds.stream()
                .map(catId -> categoryRepository.findById(catId)
                        .orElseThrow(() -> new AppException("Category not found")))
                .collect(Collectors.toSet());
    }

    public Brand brandToId(Long brandId) {
        return brandRepository.findById(brandId).orElseThrow(() -> new AppException("Brand not found"));
    }

    public Set<Media> productImageToEntity(Set<MediaRequest> imageRequests, Product product) {
        return imageRequests.stream()
                .map(imageReq -> createMedia(imageReq, product))
                .collect(Collectors.toSet());
    }

    private Media createMedia(MediaRequest imageReq, Product product) {
        Media image = new Media();
        image.setImageUrl(imageReq.getImageUrl());
        image.setProduct(product);
        return image;
    }

    public Variant variantToEntity(VariantRequest request, Product product) {
        request.validatePrices();
        if (request.getStock() != null && request.getStock() <= 0) {
            throw new AppException("Số lượng tồn kho phải lớn hơn 0");
        }
        Variant variant = new Variant();
        variant.setColor(request.getColor());
        variant.setSize(request.getSize());
        String variantIdentifier = request.getColor();
        if (request.getSize() != null && !request.getSize().isEmpty()) {
            variantIdentifier += "-" + request.getSize();
        }
        variant.setSlug(SlugUtil.toSlug(variantIdentifier));
        variant.setSku(SkuUtil.generateSku(variantIdentifier));
        variant.setOriginalPrice(request.getOriginalPrice());
        variant.setSalePrice(request.getSalePrice());
        variant.setStockQuantity(request.getStock());
        variant.setDisplayOrder(request.getDisplayOrder());
        variant.setProduct(product);
        return variant;
    }

    public AdminProductResponse adminProductToDto(Product product) {
        AdminProductResponse dto = new AdminProductResponse();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setSku(product.getSku());
        dto.setSlug(product.getSlug());
        dto.setDescription(product.getDescription());
        dto.setSpecification(product.getSpecification());
        dto.setOriginalPrice(product.getOriginalPrice());
        dto.setSalePrice(product.getSalePrice());
        dto.setPromotions(product.getPromotions());
        dto.setStock(product.getStock());
        dto.setSold(product.getSold());
        dto.setBrand(brandMapper.brandToDto(product.getBrand()));
        dto.setCategories(product.getCategories().stream()
                .map(category -> categoryMapper.categoryToDto(category))
                .collect(Collectors.toSet()));
        dto.setImages(product.getImages().stream()
                .map(this::produtImageToDto)
                .collect(Collectors.toSet()));
        dto.setVariants(product.getVariants().stream()
                .map(varinat -> variantToDto(varinat))
                .collect(Collectors.toSet()));
        dto.setCreatedAt(product.getCreatedAt());
        dto.setCreatedBy(product.getCreatedBy());
        dto.setUpdatedAt(product.getUpdatedAt());
        dto.setUpdatedBy(product.getUpdatedBy());
        dto.setStatus(product.getStatus());
        dto.setStatusName(product.getStatus().getStatusName());
        return dto;
    }

    public Product productToUpdated(Product product, ProductRequest request) {

        if (request.getVariants() == null || request.getVariants().isEmpty()) {
            request.validatePrices();
        }

        if (request.getName() != null) {
            product.setName(request.getName());
            product.setSku(SkuUtil.generateSku(request.getName()));
            product.setSlug(SlugUtil.toSlug(request.getName()));
        }

        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }

        if (request.getSpecification() != null) {
            product.setSpecification(request.getSpecification());
        }

        if (request.getPromotions() != null) {
            product.setPromotions(request.getPromotions());
        }

        if (request.getOriginalPrice() != null) {
            product.setOriginalPrice(request.getOriginalPrice());
        }

        if (request.getSalePrice() != null) {
            product.setSalePrice(request.getSalePrice());
        }

        if (request.getStock() != null) {
            product.setStock(request.getStock());
        }

        if (request.getBrandId() != null) {
            product.setBrand(brandToId(request.getBrandId()));
        }

        if (request.getCategoryId() != null && !request.getCategoryId().isEmpty()) {
            product.setCategories(categoryToId(request.getCategoryId()));
        }

        if (request.getImages() != null && !request.getImages().isEmpty()) {
            product.setImages(productImageToEntity(request.getImages(), product));
        }

        if (request.getVariants() != null && !request.getVariants().isEmpty()) {
            Set<Variant> updatedVariants = request.getVariants().stream()
                    .map(variantRequest -> {
                        Variant variant = variantRepository.findById(variantRequest.getProductId())
                                .orElseThrow(() -> new AppException("Variant not found"));
                        return variantToUpdated(variant, variantRequest, product);
                    })
                    .collect(Collectors.toSet());
            product.setVariants(updatedVariants);
        }

        return product;
    }

    public Variant variantToUpdated(Variant variant, VariantRequest request, Product product) {
        if (request.getOriginalPrice() == null || request.getSalePrice() == null) {
            request.validatePrices();
        }
        if (request.getColor() != null && !request.getColor().isEmpty()) {
            variant.setColor(request.getColor());
        }

        if (request.getSize() != null && !request.getSize().isEmpty()) {
            variant.setSize(request.getSize());
        }

        String color = request.getColor() != null && !request.getColor().isEmpty() ? request.getColor()
                : variant.getColor();
        String size = request.getSize() != null && !request.getSize().isEmpty() ? request.getSize() : variant.getSize();

        String variantIdentifier = color;
        if (size != null && !size.isEmpty()) {
            variantIdentifier += "-" + size;
        }

        variant.setSlug(SlugUtil.toSlug(variantIdentifier));
        variant.setSku(SkuUtil.generateSku(variantIdentifier));

        if (request.getOriginalPrice() != null) {
            variant.setOriginalPrice(request.getOriginalPrice());
        }

        if (request.getSalePrice() != null) {
            variant.setSalePrice(request.getSalePrice());
        }

        if (request.getStock() != null) {
            variant.setStockQuantity(request.getStock());
        }

        if (request.getDisplayOrder() != null) {
            variant.setDisplayOrder(request.getDisplayOrder());
        }

        if (product != null) {
            variant.setProduct(product);
        }

        return variant;
    }

    private MediaResponse produtImageToDto(Media image) {
        MediaResponse dto = new MediaResponse();
        dto.setId(image.getId());
        dto.setImageUrl(image.getImageUrl());
        dto.setDisplayOrder(image.getDisplayOrder());
        dto.setIsPublished(dto.getIsPublished());
        return dto;
    }

    public VariantResponse variantToDto(Variant variant) {
        VariantResponse dto = new VariantResponse();
        dto.setId(variant.getId());
        dto.setSlug(variant.getSlug());
        dto.setSku(variant.getSku());
        dto.setColor(variant.getColor());
        dto.setSize(variant.getSize());
        dto.setOriginalPrice(variant.getOriginalPrice());
        dto.setSalePrice(variant.getSalePrice());
        dto.setStockQuantity(variant.getStockQuantity());
        dto.setDisplayOrder(variant.getDisplayOrder());
        dto.setCreatedAt(variant.getCreatedAt());
        dto.setCreatedBy(variant.getCreatedBy());
        dto.setUpdatedAt(variant.getUpdatedAt());
        dto.setUpdatedBy(variant.getUpdatedBy());
        dto.setStatus(variant.getStatus());
        dto.setStatusName(variant.getStatus().getStatusName());
        return dto;
    }

    public ProductColorResponse productColorToDto(Product product, Variant variant) {
        ProductColorResponse response = new ProductColorResponse();
        response.setDescription(product.getDescription());
        response.setPromotions(product.getPromotions());
        response.setProductId(product.getId());
        if (product.getImages() != null) {
            response.setImages(product.getImages().stream()
                    .map(Media::getImageUrl)
                    .collect(Collectors.toSet()));
        }
        if (product.getCategories() != null) {
            response.setCategories(product.getCategories().stream()
                    .map(this::customerCategoryToDto)
                    .collect(Collectors.toSet()));
        }
        if (product.getBrand() != null) {
            BrandResponse brandResponse = new BrandResponse();
            brandResponse.setId(product.getBrand().getId());
            brandResponse.setName(product.getBrand().getName());
            response.setBrand(brandResponse);
        }
        response.setRating(product.getRating());
        response.setRatingCount(product.getRatingCount());
        response.setSold(product.getSold());

        if (variant != null) {
            response.setId(variant.getId());
            response.setColorId(variant.getId());
            response.setName(String.format("%s %s %s", product.getName(), variant.getSize(), variant.getColor()));
            response.setColor(variant.getColor());
            response.setStorage(variant.getSize());
            response.setSku(variant.getSku());
            response.setPrice(variant.getSalePrice());
            response.setOriginalPrice(variant.getOriginalPrice());
            response.setStock(variant.getStockQuantity());
            response.setImage(variant.getImageUrl());
            BigDecimal originalPrice = variant.getOriginalPrice();
            BigDecimal salePrice = variant.getSalePrice();
            if (originalPrice != null && salePrice != null && originalPrice.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal discount = originalPrice.subtract(salePrice)
                        .divide(originalPrice, 2, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100));
                response.setDiscount(discount.intValue());
            } else {
                response.setDiscount(0);
            }

        } else {
            response.setId(product.getId());
            response.setName(product.getName());
            response.setSku(product.getSku());
            response.setPrice(product.getSalePrice());
            response.setOriginalPrice(product.getOriginalPrice());
            response.setStock(product.getStock());
            BigDecimal originalPrice = product.getOriginalPrice();
            BigDecimal salePrice = product.getSalePrice();
            if (originalPrice != null && salePrice != null && originalPrice.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal discount = originalPrice.subtract(salePrice)
                        .divide(originalPrice, 2, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100));
                response.setDiscount(discount.intValue());
            } else {
                response.setDiscount(0);
            }
        }

        return response;
    }

}

package com.snd.server.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.snd.server.dto.request.ProductRequest;
import com.snd.server.dto.request.VariantRequest;
import com.snd.server.dto.response.AdminProductResponse;
import com.snd.server.dto.response.CustomerProductResponse;
import com.snd.server.dto.response.ProductColorResponse;
import com.snd.server.dto.response.SimplifiedPageResponse;
import com.snd.server.dto.response.VariantResponse;
import com.snd.server.enums.EntityStatusEnum;
import com.snd.server.enums.EntityStatusEnumRequest;
import com.snd.server.event.EventType;
import com.snd.server.event.domain.InventoryEvent;
import com.snd.server.event.publisher.InventoryPublisher;
import com.snd.server.exception.AppException;
import com.snd.server.mapper.ProductMapper;
import com.snd.server.model.Product;
import com.snd.server.model.Variant;
import com.snd.server.repository.ProductRepository;
import com.snd.server.repository.VariantRepository;

import jakarta.transaction.Transactional;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final VariantRepository variantRepository;
    private final InventoryPublisher inventoryPublisher;
    private final ProductMapper productMapper;

    public ProductService(ProductRepository productRepository, VariantRepository variantRepository,
            ProductMapper productMapper, InventoryPublisher inventoryPublisher,
            FileService fileService) {
        this.productRepository = productRepository;
        this.variantRepository = variantRepository;
        this.productMapper = productMapper;
        this.inventoryPublisher = inventoryPublisher;
    }

    // Customer
    public List<CustomerProductResponse> getAllProducts() {
        return productRepository.findAllByStatus(EntityStatusEnum.ACTIVE).stream()
                .map(productMapper::customerProductToDto)
                .toList();
    }

    public List<CustomerProductResponse> getAllProductsHot() {
        return productRepository.findAllByStatusOrderBySoldDesc(EntityStatusEnum.ACTIVE).stream()
                .map(productMapper::customerProductToDto)
                .toList();
    }

    public List<CustomerProductResponse> getAllProductsNew() {
        return productRepository.findAllByStatusOrderByCreatedAtDesc(EntityStatusEnum.ACTIVE).stream()
                .map(productMapper::customerProductToDto)
                .toList();
    }

    public CustomerProductResponse getByIdProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException("Product not found"));
        return productMapper.customerProductToDto(product);
    }

    public List<CustomerProductResponse> searchProducts(String query) {
        if (query == null || query.trim().isEmpty()) {
            return productRepository.findAllByStatus(EntityStatusEnum.ACTIVE).stream()
                    .map(productMapper::customerProductToDto)
                    .collect(Collectors.toList());
        }
        return productRepository.searchProducts(query, EntityStatusEnum.ACTIVE).stream()
                .map(productMapper::customerProductToDto)
                .collect(Collectors.toList());
    }

    // List Product
    public SimplifiedPageResponse<ProductColorResponse> getAllPageProducts(Pageable pageable) {
        Page<Variant> variantPage = variantRepository.findAllByProductStatus(EntityStatusEnum.ACTIVE, pageable);
        List<ProductColorResponse> productColorResponses = variantPage.getContent().stream()
                .map(variant -> productMapper.productColorToDto(variant.getProduct(), variant))
                .collect(Collectors.toList());
        Page<ProductColorResponse> page = new PageImpl<>(
                productColorResponses,
                pageable,
                variantPage.getTotalElements());
        return new SimplifiedPageResponse<>(page);
    }

    // Admin
    public SimplifiedPageResponse<AdminProductResponse> adminGetAll(EntityStatusEnumRequest statusRequest,
            Pageable pageable) {
        Page<Product> page;
        if (statusRequest != null) {
            EntityStatusEnum status = EntityStatusEnum.valueOf(statusRequest.name());
            page = productRepository.findAllByStatusOrderByIdDesc(status, pageable);
        } else {
            List<EntityStatusEnum> defaultStatuses = List.of(EntityStatusEnum.ACTIVE,
                    EntityStatusEnum.HIDDEN);
            page = productRepository.findAllByStatusInOrderByIdDesc(defaultStatuses,
                    pageable);
        }

        List<AdminProductResponse> list = page.getContent().stream()
                .map(productMapper::adminProductToDto)
                .collect(Collectors.toList());

        Page<AdminProductResponse> mappedPage = new PageImpl<>(list, pageable,
                page.getTotalElements());
        return new SimplifiedPageResponse<>(mappedPage);
    }

    public AdminProductResponse adminGetById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException("Product not found"));
        return productMapper.adminProductToDto(product);
    }

    public AdminProductResponse createProduct(ProductRequest request) {
        Product product = productMapper.adminProductToEntity(request);
        productRepository.save(product);
        if (product.getVariants() == null || product.getVariants().isEmpty()) {
            if (request.getStock() != null) {
                InventoryEvent inventoryEvent = InventoryEvent.builder()
                        .eventType(EventType.INVENTORY_CREATE)
                        .skuProduct(product.getSku())
                        .skuVariant(null)
                        .quantity(product.getStock())
                        .price(product.getImportPrice())
                        .build();
                inventoryPublisher.sendInventory(inventoryEvent);
            }
        } else {
            for (Variant variant : product.getVariants()) {
                if (variant.getStockQuantity() != null && variant.getStockQuantity() >= 0) {
                    InventoryEvent inventoryEvent = InventoryEvent.builder()
                            .eventType(EventType.INVENTORY_CREATE)
                            .skuProduct(product.getSku())
                            .skuVariant(variant.getSku())
                            .quantity(variant.getStockQuantity())
                            .price(variant.getImportPrice())
                            .build();
                    inventoryPublisher.sendInventory(inventoryEvent);
                } else {
                    throw new AppException("Số lượng biến thể không hợp lệ");
                }
            }
        }
        return productMapper.adminProductToDto(product);
    }

    public AdminProductResponse updateProduct(Long id, ProductRequest request) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new AppException("Product not found"));
        productMapper.productToUpdated(existingProduct, request);
        Product updatedProduct = productRepository.save(existingProduct);
        if (updatedProduct.getVariants() == null || updatedProduct.getVariants().isEmpty()) {
            if (request.getStock() != null) {
                InventoryEvent inventoryEvent = InventoryEvent.builder()
                        .eventType(EventType.INVENTORY_UPDATE)
                        .skuProduct(updatedProduct.getSku())
                        .skuVariant(null)
                        .quantity(request.getStock())
                        .price(updatedProduct.getImportPrice())
                        .build();
                inventoryPublisher.sendInventory(inventoryEvent);
            }
        } else {
            for (Variant variant : updatedProduct.getVariants()) {
                if (variant.getStockQuantity() != null) {
                    InventoryEvent inventoryEvent = InventoryEvent.builder()
                            .eventType(EventType.INVENTORY_UPDATE)
                            .skuProduct(updatedProduct.getSku())
                            .skuVariant(variant.getSku())
                            .quantity(variant.getStockQuantity())
                            .price(variant.getImportPrice())
                            .build();
                    inventoryPublisher.sendInventory(inventoryEvent);
                }
            }
        }
        return productMapper.adminProductToDto(updatedProduct);
    }

    public VariantResponse addVariant(VariantRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new AppException("Product not found"));
        Variant variant = productMapper.variantToEntity(request, product);
        Variant savedVariant = variantRepository.save(variant);
        InventoryEvent inventoryEvent = InventoryEvent.builder()
                .eventType(EventType.INVENTORY_CREATE)
                .skuProduct(product.getSku())
                .skuVariant(variant.getSku())
                .quantity(variant.getStockQuantity())
                .price(variant.getImportPrice())
                .build();
        inventoryPublisher.sendInventory(inventoryEvent);
        return productMapper.variantToDto(savedVariant);
    }

    @Transactional
    public void updateProductInventory(InventoryEvent inventoryEvent) {
        if (inventoryEvent.getSkuVariant() == null) {
            long skuProduct = 0;
            try {
                skuProduct = Long.parseLong(inventoryEvent.getSkuProduct());
            } catch (NumberFormatException e) {
                throw new AppException("Invalid SKU format");
            }
            Product product = productRepository.findById(skuProduct)
                    .orElseThrow(() -> new AppException("Sản phẩm không tồn tại"));
            int newStock = product.getStock() - inventoryEvent.getQuantity();
            product.setStock(newStock);
            int newSold = product.getSold() + inventoryEvent.getQuantity();

            product.setSold(newSold);

            productRepository.save(product);
        } else {
            Variant variant = variantRepository.findByIdAndSize(
                    Long.parseLong(inventoryEvent.getSkuProduct()),
                    inventoryEvent.getSkuVariant())
                    .orElseThrow(() -> new AppException(
                            "Biến thể sản phẩm không tồn tại với id=" + inventoryEvent.getSkuProduct() +
                                    " và size=" + inventoryEvent.getSkuVariant()));

            int newVariantStock = variant.getStockQuantity() - inventoryEvent.getQuantity();

            variant.setStockQuantity(newVariantStock);
            Product product = variant.getProduct();
            int newSold = product.getSold() + inventoryEvent.getQuantity();
            product.setSold(newSold);
            productRepository.save(product);
            variantRepository.save(variant);
        }

    }

}

package com.snd.server.service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.snd.server.dto.request.InventoryRequest;
import com.snd.server.dto.response.InventoryResponse;
import com.snd.server.event.EventType;
import com.snd.server.event.domain.InventoryEvent;
import com.snd.server.event.publisher.InventoryPublisher;
import com.snd.server.exception.AppException;
import com.snd.server.model.Inventory;
import com.snd.server.model.Product;
import com.snd.server.model.Variant;
import com.snd.server.repository.InventoryRepository;
import com.snd.server.repository.ProductRepository;
import com.snd.server.repository.VariantRepository;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final VariantRepository variantRepository;
    private final ProductRepository productRepository;
    private final InventoryPublisher inventoryPublisher;

    public InventoryService(InventoryRepository inventoryRepository, VariantRepository variantRepository,
            ProductRepository productRepository, InventoryPublisher inventoryPublisher) {
        this.inventoryRepository = inventoryRepository;
        this.variantRepository = variantRepository;
        this.productRepository = productRepository;
        this.inventoryPublisher = inventoryPublisher;
    }

    public void createInventory(InventoryEvent event) {
        if (event.getQuantity() == null || event.getQuantity() < 0) {
            throw new AppException("Số lượng tồn kho không hợp lệ");
        }
        Inventory inventory = new Inventory();
        inventory.setSkuProduct(event.getSkuProduct());
        inventory.setSkuVariant(event.getSkuVariant());
        inventory.setQuantity(event.getQuantity());
        inventory.setImportPrice(event.getPrice());
        inventory.setLastUpdated(Instant.now());
        inventoryRepository.save(inventory);
    }

    public void updateInventory(InventoryEvent event) {
        if (event.getQuantity() == null || event.getQuantity() < 0) {
            throw new AppException("Số lượng tồn kho không hợp lệ");
        }
        Inventory inventory = (event.getSkuVariant() != null)
                ? inventoryRepository.findBySkuVariant(event.getSkuVariant())
                        .orElseThrow(() -> new AppException(
                                "Không tìm thấy tồn kho cho biến thể"))
                : inventoryRepository.findBySkuProductAndSkuVariantIsNull(event.getSkuProduct())
                        .orElseThrow(() -> new AppException(
                                "Không tìm thấy tồn kho cho sản phẩm"));
        inventory.setQuantity(event.getQuantity());
        inventory.setImportPrice(event.getPrice());
        inventory.setLastUpdated(Instant.now());
        inventoryRepository.save(inventory);
    }

    public void deleteInventory(InventoryEvent event) {
        if (event.getSkuVariant() != null) {
            Inventory inventory = inventoryRepository.findBySkuVariant(event.getSkuVariant())
                    .orElseThrow(() -> new AppException(
                            "Không tìm thấy tồn kho cho biến thể"));
            inventoryRepository.delete(inventory);
        } else {
            Inventory inventory = inventoryRepository.findBySkuProductAndSkuVariantIsNull(event.getSkuProduct())
                    .orElseThrow(() -> new AppException(
                            "Không tìm thấy tồn kho cho sản phẩm"));
            inventoryRepository.delete(inventory);
        }
    }

    public void deductInventory(InventoryEvent event) {
        if (event.getQuantity() == null || event.getQuantity() < 0) {
            throw new AppException("Số lượng trừ tồn kho không hợp lệ");
        }
        Inventory inventory;
        Product product = null;
        if (event.getSkuVariant() != null) {
            Variant variant = variantRepository.findByIdAndSize(
                    Long.parseLong(event.getSkuProduct()),
                    event.getSkuVariant())
                    .orElseThrow(() -> new AppException(
                            "Biến thể sản phẩm không tồn tại với id=" + event.getSkuProduct() +
                                    " và size=" + event.getSkuVariant()));

            inventory = inventoryRepository.findBySkuVariant(variant.getSku())
                    .orElseThrow(
                            () -> new AppException("Số lượng nhập kho không hợp lệ"));
        } else {
            long skuProduct = 0;
            try {
                skuProduct = Long.parseLong(event.getSkuProduct());
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            product = productRepository.findById(skuProduct)
                    .orElseThrow(() -> new AppException(
                            "Không tìm thấy tồn kho cho sản phẩm"));
            inventory = inventoryRepository.findBySkuProductAndSkuVariantIsNull(product.getSku())
                    .orElseThrow(
                            () -> new AppException("Không tìm thấy tồn kho cho biến thể"));
        }

        int newQuantity = inventory.getQuantity() - event.getQuantity();
        if (newQuantity < 0) {
            throw new AppException("Tồn kho không đủ");
        }
        inventory.setQuantity(newQuantity);
        inventory.setLastUpdated(Instant.now());
        inventoryRepository.save(inventory);

    }

    public InventoryResponse importInventory(InventoryRequest request) {
        if (request.getQuantity() == null || request.getQuantity() < 0) {
            throw new AppException("Số lượng nhập kho không hợp lệ");
        }

        if (request.getSkuProduct() == null) {
            throw new AppException("Thiếu mã sản phẩm (skuProduct)");
        }

        Inventory inventory;

        if (request.getSkuVariant() != null) {
            inventory = inventoryRepository.findBySkuVariant(request.getSkuVariant())
                    .orElseGet(() -> {
                        Inventory newInventory = new Inventory();
                        newInventory.setSkuProduct(request.getSkuProduct());
                        newInventory.setSkuVariant(request.getSkuVariant());
                        newInventory.setQuantity(request.getQuantity());
                        newInventory.setImportPrice(request.getImportPrice());

                        InventoryEvent inventoryEvent = InventoryEvent.builder()
                                .eventType(EventType.PRODUCT_INVENTORY)
                                .skuProduct(request.getSkuProduct())
                                .skuVariant(null)
                                .quantity(request.getQuantity())
                                .price(request.getImportPrice())

                                .build();
                        inventoryPublisher.sendInventory(inventoryEvent);
                        return newInventory;
                    });
        } else {
            inventory = inventoryRepository.findBySkuProductAndSkuVariantIsNull(request.getSkuProduct())
                    .orElseGet(() -> {
                        Inventory newInventory = new Inventory();
                        newInventory.setSkuProduct(request.getSkuProduct());
                        newInventory.setSkuVariant(null);
                        newInventory.setQuantity(request.getQuantity());
                        newInventory.setImportPrice(request.getImportPrice());

                        InventoryEvent inventoryEvent = InventoryEvent.builder()
                                .eventType(EventType.PRODUCT_INVENTORY)
                                .skuProduct(request.getSkuProduct())
                                .skuVariant(null)
                                .quantity(request.getQuantity())
                                .price(request.getImportPrice())

                                .build();
                        inventoryPublisher.sendInventory(inventoryEvent);
                        return newInventory;
                    });
        }

        inventory.setQuantity(inventory.getQuantity() + request.getQuantity());
        inventory.setLastUpdated(Instant.now());

        Inventory savedInventory = inventoryRepository.save(inventory);
        return mapToDto(savedInventory);
    }

    public InventoryResponse exportInventory(InventoryRequest request) {
        if (request.getQuantity() == null || request.getQuantity() < 0) {
            throw new AppException("Số lượng xuất kho không hợp lệ");
        }
        Inventory inventory;
        if (request.getSkuVariant() != null) {
            inventory = inventoryRepository.findBySkuVariant(request.getSkuVariant())
                    .orElseThrow(() -> new AppException(
                            "Không tìm thấy tồn kho cho biến thể"));
        } else {
            inventory = inventoryRepository.findBySkuProductAndSkuVariantIsNull(request.getSkuProduct())
                    .orElseThrow(() -> new AppException(
                            "Không tìm thấy tồn kho cho sản phẩm"));
        }
        int newQuantity = inventory.getQuantity() - request.getQuantity();
        if (newQuantity < 0) {
            throw new AppException("Tồn kho không đủ");
        }
        inventory.setQuantity(newQuantity);
        inventory.setLastUpdated(Instant.now());
        Inventory savedInventory = inventoryRepository.save(inventory);
        return mapToDto(savedInventory);
    }

    public List<InventoryResponse> getAllInventories(String sortByStock) {
        List<Inventory> inventories;
        switch (sortByStock.toLowerCase()) {
            case "instock":
                inventories = inventoryRepository.findAllByQuantityGreaterThan(0);
                break;
            case "outofstock":
                inventories = inventoryRepository.findAllByQuantityEquals(0);
                break;
            default:
                inventories = inventoryRepository.findAll();
                break;
        }
        return inventories.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private InventoryResponse mapToDto(Inventory inventory) {
        InventoryResponse dto = new InventoryResponse();
        dto.setId(inventory.getId());
        dto.setSkuProduct(inventory.getSkuProduct());
        dto.setSkuVariant(inventory.getSkuVariant());
        dto.setQuantity(inventory.getQuantity());
        dto.setImportPrice(inventory.getImportPrice());
        dto.setLastUpdated(inventory.getLastUpdated());
        dto.setCreatedAt(inventory.getCreatedAt());
        dto.setCreatedBy(inventory.getCreatedBy());
        dto.setUpdatedAt(inventory.getUpdatedAt());
        dto.setUpdatedBy(inventory.getUpdatedBy());
        dto.setStatus(inventory.getStatus());
        dto.setStatusName(inventory.getStatus().getStatusName());
        return dto;
    }
}

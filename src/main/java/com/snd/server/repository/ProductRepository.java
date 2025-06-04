package com.snd.server.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.snd.server.enums.EntityStatusEnum;
import com.snd.server.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findAllByStatus(EntityStatusEnum status);

    List<Product> findAllByStatusOrderBySoldDesc(EntityStatusEnum status);

    List<Product> findAllByStatusOrderByCreatedAtDesc(EntityStatusEnum status);

    @Query("SELECT DISTINCT p FROM Product p " +
            "LEFT JOIN p.variants v " +
            "WHERE p.status = :status AND (" +
            "LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(p.sku) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(v.color) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(v.size) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(v.sku) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Product> searchProducts(@Param("query") String query, @Param("status") EntityStatusEnum status);

    @Query("SELECT p FROM Product p JOIN p.categories c WHERE c.id = :categoryId")
    List<Product> findByCategoryId(@Param("categoryId") Long categoryId);

    List<Product> findByNameContainingIgnoreCase(String query);

    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.variants WHERE p.salePrice < p.originalPrice")
    List<Product> findBySalePriceLessThanOriginalPriceWithVariants();

    long countByBrandIdIn(Set<Long> brandIds);

    long countByBrandId(Long brandId);

    long countByCategories_Id(Long categoryId);

    long countByCategories_IdIn(Set<Long> categoryIds);

    Page<Product> findAllByStatusOrderByIdDesc(EntityStatusEnum status, Pageable pageable);

    Page<Product> findAllByStatusInOrderByIdDesc(List<EntityStatusEnum> statuses, Pageable pageable);
}
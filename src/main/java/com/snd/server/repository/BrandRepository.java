package com.snd.server.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.snd.server.enums.EntityStatusEnum;
import com.snd.server.model.Brand;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {
    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    // Customer
    List<Brand> findByStatusOrderByDisplayOrderAsc(EntityStatusEnum status);

    // Admin
    Page<Brand> findAllByStatusOrderByIdDesc(EntityStatusEnum status, Pageable pageable);

    Page<Brand> findAllByStatusInOrderByIdDesc(List<EntityStatusEnum> statuses, Pageable pageable);

}
package com.snd.server.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.snd.server.enums.EntityStatusEnum;
import com.snd.server.model.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    List<Category> findByParentIsNull();

    // Customer
    List<Category> findByStatusOrderByDisplayOrderAsc(EntityStatusEnum status);

    // Admin
    Page<Category> findAllByStatusOrderByIdDesc(EntityStatusEnum status, Pageable pageable);

    Page<Category> findAllByStatusInOrderByIdDesc(List<EntityStatusEnum> statuses, Pageable pageable);

}

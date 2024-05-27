package io.nomard.spoty_api_v1.repositories;

import io.nomard.spoty_api_v1.entities.ProductCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductCategoryRepository extends PagingAndSortingRepository<ProductCategory, Long>, JpaRepository<ProductCategory, Long> {
    List<ProductCategory> searchAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String code);

    @Query("select p from ProductCategory p where p.tenant.id = :id")
    Page<ProductCategory> findAllByTenantId(@Param("id") Long id, Pageable pageable);
}

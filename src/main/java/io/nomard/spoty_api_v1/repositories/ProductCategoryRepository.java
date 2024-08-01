package io.nomard.spoty_api_v1.repositories;

import io.nomard.spoty_api_v1.entities.ProductCategory;
import io.nomard.spoty_api_v1.entities.accounting.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public interface ProductCategoryRepository extends PagingAndSortingRepository<ProductCategory, Long>, JpaRepository<ProductCategory, Long> {
    @Query("SELECT pc FROM ProductCategory pc WHERE pc.tenant.id = :tenantId " +
            "AND TRIM(LOWER(pc.name)) LIKE %:search%")
    ArrayList<ProductCategory> searchAll(@Param("tenantId") Long tenantId, @Param("search") String search);

    @Query("select p from ProductCategory p where p.tenant.id = :id")
    Page<ProductCategory> findAllByTenantId(@Param("id") Long id, Pageable pageable);
}

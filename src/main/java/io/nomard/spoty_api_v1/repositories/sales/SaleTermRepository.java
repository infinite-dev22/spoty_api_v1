package io.nomard.spoty_api_v1.repositories.sales;

import io.nomard.spoty_api_v1.entities.sales.SaleTerm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SaleTermRepository extends PagingAndSortingRepository<SaleTerm, Long>, JpaRepository<SaleTerm, Long> {
    List<SaleTerm> searchAllByNameContainingIgnoreCase(String ref);

    @Query("select p from SaleTerm p where p.tenant.id = :id")
    Page<SaleTerm> findAllByTenantId(@Param("id") Long id, Pageable pageable);
}

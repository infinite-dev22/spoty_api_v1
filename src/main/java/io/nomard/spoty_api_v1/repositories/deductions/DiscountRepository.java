package io.nomard.spoty_api_v1.repositories.deductions;

import io.nomard.spoty_api_v1.entities.deductions.Discount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscountRepository extends PagingAndSortingRepository<Discount, Long>, JpaRepository<Discount, Long> {
    @Query("select p from Discount p where p.tenant.id = :id")
    Page<Discount> findAllByTenantId(@Param("id") Long id, Pageable pageable);
}

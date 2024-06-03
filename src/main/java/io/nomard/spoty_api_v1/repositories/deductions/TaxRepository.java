package io.nomard.spoty_api_v1.repositories.deductions;

import io.nomard.spoty_api_v1.entities.deductions.Tax;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TaxRepository extends PagingAndSortingRepository<Tax, Long>, JpaRepository<Tax, Long> {
    @Query("select p from Tax p where p.tenant.id = :id")
    Page<Tax> findAllByTenantId(@Param("id") Long id, Pageable pageable);
}

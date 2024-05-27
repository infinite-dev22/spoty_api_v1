package io.nomard.spoty_api_v1.repositories;

import io.nomard.spoty_api_v1.entities.StockReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StockReportRepository extends PagingAndSortingRepository<StockReport, Long>, JpaRepository<StockReport, Long> {
    @Query("select p from StockReport p where p.tenant.id = :id")
    Page<StockReport> findAllByTenantId(@Param("id") Long id, Pageable pageable);
}

package io.nomard.spoty_api_v1.repositories.stock_ins;

import io.nomard.spoty_api_v1.entities.stock_ins.StockInMaster;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockInMasterRepository extends PagingAndSortingRepository<StockInMaster, Long>, JpaRepository<StockInMaster, Long> {
    List<StockInMaster> searchAllByRefContainingIgnoreCase(String ref);

    @Query("select p from StockInMaster p where p.tenant.id = :id")
    Page<StockInMaster> findAllByTenantId(@Param("id") Long id, Pageable pageable);
}

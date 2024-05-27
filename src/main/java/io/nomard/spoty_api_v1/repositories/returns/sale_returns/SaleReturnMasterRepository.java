package io.nomard.spoty_api_v1.repositories.returns.sale_returns;

import io.nomard.spoty_api_v1.entities.returns.sale_returns.SaleReturnMaster;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SaleReturnMasterRepository extends PagingAndSortingRepository<SaleReturnMaster, Long>, JpaRepository<SaleReturnMaster, Long> {
    List<SaleReturnMaster> searchAllByRefContainingIgnoreCase(String ref);

    @Query("select p from SaleReturnMaster p where p.tenant.id = :id")
    Page<SaleReturnMaster> findAllByTenantId(@Param("id") Long id, Pageable pageable);
}

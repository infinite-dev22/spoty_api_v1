package io.nomard.spoty_api_v1.repositories.transfers;

import io.nomard.spoty_api_v1.entities.accounting.Account;
import io.nomard.spoty_api_v1.entities.transfers.TransferMaster;
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
public interface TransferMasterRepository extends PagingAndSortingRepository<TransferMaster, Long>, JpaRepository<TransferMaster, Long> {
    @Query("SELECT tm FROM TransferMaster tm WHERE tm.tenant.id = :tenantId " +
            "AND TRIM(LOWER(tm.ref)) LIKE %:search%")
    ArrayList<TransferMaster> searchAll(@Param("tenantId") Long tenantId, @Param("search") String search);

    @Query("select p from TransferMaster p where p.tenant.id = :id")
    Page<TransferMaster> findAllByTenantId(@Param("id") Long id, Pageable pageable);
}

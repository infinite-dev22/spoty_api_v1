package io.nomard.spoty_api_v1.repositories.transfers;

import io.nomard.spoty_api_v1.entities.transfers.TransferMaster;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface TransferMasterRepository extends PagingAndSortingRepository<TransferMaster, Long>, JpaRepository<TransferMaster, Long> {
    @Query("SELECT tm FROM TransferMaster tm WHERE tm.tenant.id = :tenantId " +
            "AND TRIM(LOWER(tm.ref)) LIKE %:search% AND (tm.approved = true OR tm.createdBy.id = :userId OR (SELECT COUNT(a) FROM Reviewer a WHERE a.employee.id = :userId AND a.level = tm.nextApprovedLevel) > 0)")
    ArrayList<TransferMaster> searchAll(@Param("tenantId") Long tenantId,
                                        @Param("userId") Long userId,
                                        @Param("search") String search);

    @Query("select tm from TransferMaster tm where tm.tenant.id = :tenantId AND (tm.approved = true OR tm.createdBy.id = :userId OR (SELECT COUNT(a) FROM Reviewer a WHERE a.employee.id = :userId AND a.level = tm.nextApprovedLevel) > 0)")
    Page<TransferMaster> findAllByTenantId(@Param("tenantId") Long tenantId, @Param("userId") Long userId, Pageable pageable);
}

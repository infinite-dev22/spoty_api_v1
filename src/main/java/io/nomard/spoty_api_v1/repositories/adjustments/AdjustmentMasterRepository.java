package io.nomard.spoty_api_v1.repositories.adjustments;

import io.nomard.spoty_api_v1.entities.adjustments.AdjustmentMaster;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdjustmentMasterRepository extends PagingAndSortingRepository<AdjustmentMaster, Long>, JpaRepository<AdjustmentMaster, Long> {
    List<AdjustmentMaster> searchAllByRefContainingIgnoreCase(String ref);

    @Query("select p from AdjustmentMaster p where p.tenant.id = :id")
    Page<AdjustmentMaster> findAllByTenantId(@Param("id") Long id, Pageable pageable);
}

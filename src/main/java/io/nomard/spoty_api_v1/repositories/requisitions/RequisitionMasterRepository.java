package io.nomard.spoty_api_v1.repositories.requisitions;

import io.nomard.spoty_api_v1.entities.requisitions.RequisitionMaster;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequisitionMasterRepository extends PagingAndSortingRepository<RequisitionMaster, Long>, JpaRepository<RequisitionMaster, Long> {
    List<RequisitionMaster> searchAllByRefContainingIgnoreCaseOrStatusContainingIgnoreCase(String ref, String status);

    @Query("select p from RequisitionMaster p where p.tenant.id = :id")
    Page<RequisitionMaster> findAllByTenantId(@Param("id") Long id, Pageable pageable);
}

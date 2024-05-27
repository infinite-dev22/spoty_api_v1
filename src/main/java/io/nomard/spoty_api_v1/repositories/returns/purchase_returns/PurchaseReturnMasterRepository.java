package io.nomard.spoty_api_v1.repositories.returns.purchase_returns;

import io.nomard.spoty_api_v1.entities.returns.purchase_returns.PurchaseReturnMaster;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseReturnMasterRepository extends PagingAndSortingRepository<PurchaseReturnMaster, Long>, JpaRepository<PurchaseReturnMaster, Long> {
    List<PurchaseReturnMaster> searchAllByRefContainingIgnoreCaseOrShippingContainingIgnoreCaseOrStatusContainingIgnoreCaseOrPaymentStatusContainsIgnoreCase(String ref, String shipping, String status, String paymentStatus);

    @Query("select p from PurchaseReturnMaster p where p.tenant.id = :id")
    Page<PurchaseReturnMaster> findAllByTenantId(@Param("id") Long id, Pageable pageable);
}

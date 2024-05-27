package io.nomard.spoty_api_v1.repositories.purchases;

import io.nomard.spoty_api_v1.entities.purchases.PurchaseMaster;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseMasterRepository extends PagingAndSortingRepository<PurchaseMaster, Long>, JpaRepository<PurchaseMaster, Long> {
    List<PurchaseMaster> searchAllByRefContainingIgnoreCaseOrPurchaseStatusContainingIgnoreCaseOrPaymentStatusContainsIgnoreCase(String ref, String purchaseStatus, String paymentStatus);

    @Query("select p from PurchaseMaster p where p.tenant.id = :id")
    Page<PurchaseMaster> findAllByTenantId(@Param("id") Long id, Pageable pageable);
}

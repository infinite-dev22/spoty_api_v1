package io.nomard.spoty_api_v1.repositories.returns;

import io.nomard.spoty_api_v1.entities.returns.purchase_returns.PurchaseReturnMaster;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface PurchaseReturnRepository extends ReactiveSortingRepository<PurchaseReturnMaster, Long>, ReactiveCrudRepository<PurchaseReturnMaster, Long> {
    @Query("SELECT prm " +
            "FROM PurchaseReturnMaster prm " +
            "WHERE prm.tenant.id = :id " +
            "AND CONCAT(LOWER(prm.ref)," +
            "LOWER(prm.supplier.name)," +
            "LOWER(prm.supplier.email)," +
            "LOWER(prm.supplier.phone)," +
            "LOWER(prm.supplier.taxNumber)," +
            "LOWER(prm.supplier.address)," +
            "LOWER(prm.createdBy.userProfile.firstName)," +
            "LOWER(prm.createdBy.userProfile.lastName)," +
            "LOWER(prm.createdBy.email)," +
            "LOWER(prm.updatedBy.userProfile.firstName)," +
            "LOWER(prm.updatedBy.userProfile.lastName)," +
            "LOWER(prm.updatedBy.email)," +
            "LOWER(prm.tax.name)," +
            "LOWER(prm.discount.name)," +
            "LOWER(prm.shippingFee)," +
            "LOWER(prm.total)," +
            "LOWER(prm.subTotal)," +
            "LOWER(prm.amountPaid)," +
            "LOWER(prm.amountDue)," +
            "LOWER(prm.purchaseStatus)," +
            "LOWER(prm.paymentStatus)) " +
            "LIKE %:search%")
    Flux<PurchaseReturnMaster> search(@Param("id") Long id, @Param("search") String search);

    @Query("SELECT prm FROM PurchaseReturnMaster prm WHERE prm.tenant.id = :id")
    Flux<PurchaseReturnMaster> findAllByTenantId(@Param("id") Long id, Pageable pageable);
}

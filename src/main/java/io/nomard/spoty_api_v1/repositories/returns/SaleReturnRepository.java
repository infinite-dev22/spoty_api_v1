package io.nomard.spoty_api_v1.repositories.returns;

import io.nomard.spoty_api_v1.entities.returns.sale_returns.SaleReturnMaster;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface SaleReturnRepository extends ReactiveSortingRepository<SaleReturnMaster, Long>, ReactiveCrudRepository<SaleReturnMaster, Long> {
    @Query("SELECT srm " +
            "FROM SaleReturnMaster srm " +
            "WHERE srm.tenant.id = :id " +
            "AND CONCAT(LOWER(srm.ref)," +
            "LOWER(srm.customer.name)," +
            "LOWER(srm.customer.email)," +
            "LOWER(srm.customer.phone)," +
            "LOWER(srm.customer.taxNumber)," +
            "LOWER(srm.customer.address)," +
            "LOWER(srm.createdBy.userProfile.firstName)," +
            "LOWER(srm.createdBy.userProfile.lastName)," +
            "LOWER(srm.createdBy.email)," +
            "LOWER(srm.updatedBy.userProfile.firstName)," +
            "LOWER(srm.updatedBy.userProfile.lastName)," +
            "LOWER(srm.updatedBy.email)," +
            "LOWER(srm.tax.name)," +
            "LOWER(srm.discount.name)," +
            "LOWER(srm.shippingFee)," +
            "LOWER(srm.total)," +
            "LOWER(srm.subTotal)," +
            "LOWER(srm.amountPaid)," +
            "LOWER(srm.amountDue)," +
            "LOWER(srm.changeAmount)," +
            "LOWER(srm.saleStatus)," +
            "LOWER(srm.paymentStatus)) " +
            "LIKE %:search%")
    Flux<SaleReturnMaster> search(@Param("id") Long id, @Param("search") String search);

    @Query("SELECT srm FROM SaleReturnMaster srm WHERE srm.tenant.id = :id")
    Flux<SaleReturnMaster> findAllByTenantId(@Param("id") Long id, Pageable pageable);
}

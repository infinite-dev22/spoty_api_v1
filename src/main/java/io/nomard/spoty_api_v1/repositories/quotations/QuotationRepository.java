package io.nomard.spoty_api_v1.repositories.quotations;

import io.nomard.spoty_api_v1.entities.quotations.QuotationMaster;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface QuotationRepository extends ReactiveSortingRepository<QuotationMaster, Long>, ReactiveCrudRepository<QuotationMaster, Long> {
    @Query("SELECT qm " +
            "FROM QuotationMaster qm " +
            "WHERE qm.tenant.id = :id " +
            "AND CONCAT(LOWER(qm.ref)," +
            "LOWER(qm.customer.name)," +
            "LOWER(qm.customer.email)," +
            "LOWER(qm.customer.phone)," +
            "LOWER(qm.customer.taxNumber)," +
            "LOWER(qm.customer.address)," +
            "LOWER(qm.createdBy.userProfile.firstName)," +
            "LOWER(qm.createdBy.userProfile.lastName)," +
            "LOWER(qm.createdBy.email)," +
            "LOWER(qm.updatedBy.userProfile.firstName)," +
            "LOWER(qm.updatedBy.userProfile.lastName)," +
            "LOWER(qm.updatedBy.email)," +
            "LOWER(qm.tax.name)," +
            "LOWER(qm.discount.name)," +
            "LOWER(qm.shippingFee)," +
            "LOWER(qm.total)," +
            "LOWER(qm.subTotal)," +
            "LOWER(qm.status)) " +
            "LIKE %:search%")
    Flux<QuotationMaster> search(@Param("id") Long id, @Param("search") String search);

    @Query("SELECT qm FROM QuotationMaster qm WHERE qm.tenant.id = :id")
    Flux<QuotationMaster> findAllByTenantId(@Param("id") Long id, Pageable pageable);
}

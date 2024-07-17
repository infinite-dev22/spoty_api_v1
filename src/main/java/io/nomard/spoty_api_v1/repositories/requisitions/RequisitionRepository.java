package io.nomard.spoty_api_v1.repositories.requisitions;

import io.nomard.spoty_api_v1.entities.requisitions.RequisitionMaster;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface RequisitionRepository extends ReactiveSortingRepository<RequisitionMaster, Long>, ReactiveCrudRepository<RequisitionMaster, Long> {
    @Query("SELECT rm " +
            "FROM RequisitionMaster rm " +
            "WHERE rm.tenant.id = :id " +
            "AND CONCAT(LOWER(rm.ref)," +
            "LOWER(rm.customer.name)," +
            "LOWER(rm.customer.email)," +
            "LOWER(rm.customer.phone)," +
            "LOWER(rm.customer.taxNumber)," +
            "LOWER(rm.customer.address)," +
            "LOWER(rm.createdBy.userProfile.firstName)," +
            "LOWER(rm.createdBy.userProfile.lastName)," +
            "LOWER(rm.createdBy.email)," +
            "LOWER(rm.updatedBy.userProfile.firstName)," +
            "LOWER(rm.updatedBy.userProfile.lastName)," +
            "LOWER(rm.updatedBy.email)," +
            "LOWER(rm.tax.name)," +
            "LOWER(rm.discount.name)," +
            "LOWER(rm.shippingFee)," +
            "LOWER(rm.total)," +
            "LOWER(rm.subTotal)," +
            "LOWER(rm.status)) " +
            "LIKE %:search%")
    Flux<RequisitionMaster> search(@Param("id") Long id, @Param("search") String search);

    @Query("SELECT rm FROM RequisitionMaster rm WHERE rm.tenant.id = :id")
    Flux<RequisitionMaster> findAllByTenantId(@Param("id") Long id, Pageable pageable);
}

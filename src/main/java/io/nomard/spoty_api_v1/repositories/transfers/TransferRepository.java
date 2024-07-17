package io.nomard.spoty_api_v1.repositories.transfers;

import io.nomard.spoty_api_v1.entities.UnitOfMeasure;
import io.nomard.spoty_api_v1.entities.transfers.TransferMaster;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface TransferRepository extends ReactiveSortingRepository<TransferMaster, Long>, ReactiveCrudRepository<TransferMaster, Long> {
    @Query("SELECT tm " +
            "FROM TransferMaster tm " +
            "WHERE tm.tenant.id = :id " +
            "AND CONCAT(LOWER(tm.ref)," +
            "LOWER(tm.fromBranch.name)," +
            "LOWER(tm.toBranch.name)," +
            "LOWER(tm.createdBy.userProfile.firstName)," +
            "LOWER(tm.createdBy.userProfile.lastName)," +
            "LOWER(tm.createdBy.email)," +
            "LOWER(tm.updatedBy.userProfile.firstName)," +
            "LOWER(tm.updatedBy.userProfile.lastName)," +
            "LOWER(tm.updatedBy.email)," +
            "LOWER(tm.shipping)," +
            "LOWER(tm.total)," +
            "LOWER(tm.status)) " +
            "LIKE %:search%")
    Flux<UnitOfMeasure> search(@Param("id") Long id, @Param("search") String search);

    @Query("SELECT p FROM TransferMaster p WHERE p.tenant.id = :id")
    Flux<TransferMaster> findAllByTenantId(@Param("id") Long id, Pageable pageable);
}

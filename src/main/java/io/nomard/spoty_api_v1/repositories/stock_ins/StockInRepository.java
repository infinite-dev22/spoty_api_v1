package io.nomard.spoty_api_v1.repositories.stock_ins;

import io.nomard.spoty_api_v1.entities.stock_ins.StockInMaster;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface StockInRepository extends ReactiveSortingRepository<StockInMaster, Long>, ReactiveCrudRepository<StockInMaster, Long> {
    @Query("SELECT si " +
            "FROM StockInMaster si " +
            "WHERE si.tenant.id = :id " +
            "AND CONCAT(LOWER(tm.ref)," +
            "LOWER(tm.createdBy.userProfile.firstName)," +
            "LOWER(tm.createdBy.userProfile.lastName)," +
            "LOWER(tm.createdBy.email)," +
            "LOWER(tm.updatedBy.userProfile.firstName)," +
            "LOWER(tm.updatedBy.userProfile.lastName)," +
            "LOWER(tm.updatedBy.email)) " +
            "LIKE %:search%")
    Flux<StockInMaster> search(@Param("id") Long id, @Param("search") String search);

    @Query("SELECT p FROM StockInMaster p WHERE p.tenant.id = :id")
    Flux<StockInMaster> findAllByTenantId(@Param("id") Long id, Pageable pageable);
}

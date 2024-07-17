package io.nomard.spoty_api_v1.repositories;

import io.nomard.spoty_api_v1.entities.Supplier;
import io.nomard.spoty_api_v1.models.DashboardKPIModel;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface SupplierRepository extends ReactiveSortingRepository<Supplier, Long>, ReactiveCrudRepository<Supplier, Long> {
    @Query("SELECT s " +
            "FROM Supplier s " +
            "WHERE p.tenant.id = :id " +
            "AND concat(" +
            "trim(lower(s.name))," +
            "trim(lower(s.code))," +
            "trim(lower(s.city))," +
            "trim(lower(s.phone))," +
            "trim(lower(s.email))," +
            "trim(lower(s.address))," +
            "trim(lower(s.country))) LIKE %:search%")
    Flux<Supplier> search(@Param("id") Long id, @Param("search") String search);

    @Query("SELECT p FROM Supplier p WHERE p.tenant.id = :id")
    Flux<Supplier> findAllByTenantId(@Param("id") Long id, Pageable pageable);

    @Query("SELECT new io.nomard.spoty_api_v1.models.DashboardKPIModel('Total Suppliers', COUNT(s)) " +
            "FROM Supplier s " +
            "WHERE s.tenant.id = :id")
    Mono<DashboardKPIModel> countSuppliers(@Param("id") Long id);
}

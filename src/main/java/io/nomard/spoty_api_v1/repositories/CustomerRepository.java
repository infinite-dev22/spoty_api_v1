package io.nomard.spoty_api_v1.repositories;

import io.nomard.spoty_api_v1.entities.Customer;
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
public interface CustomerRepository extends ReactiveSortingRepository<Customer, Long>, ReactiveCrudRepository<Customer, Long> {
    @Query("SELECT c " +
            "FROM Customer c " +
            "WHERE c.tenant.id = :id " +
            "AND CONCAT(LOWER(c.name), LOWER(c.code), LOWER(c.city), LOWER(c.phone), LOWER(c.address), LOWER(c.country)) " +
            "LIKE %:search%")
    Flux<Customer> search(@Param("id") Long id, @Param("search") String search);

    @Query("SELECT c " +
            "FROM Customer c " +
            "WHERE c.tenant.id = :id")
    Flux<Customer> findAllByTenantId(@Param("id") Long id, Pageable pageable);

    @Query("SELECT new io.nomard.spoty_api_v1.models.DashboardKPIModel('Total Customers', COUNT(c)) " +
            "FROM Customer c " +
            "WHERE c.tenant.id = :id")
    Mono<DashboardKPIModel> countCustomers(@Param("id") Long tenantId);
}

package io.nomard.spoty_api_v1.repositories;

import io.nomard.spoty_api_v1.entities.PaymentTransaction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface PaymentTransactionRepository extends ReactiveSortingRepository<PaymentTransaction, Long>, ReactiveCrudRepository<PaymentTransaction, Long> {
    @Query("SELECT p " +
            "FROM PaymentTransaction p " +
            "WHERE p.tenant.id = :id")
    Flux<PaymentTransaction> findAllByTenantId(@Param("id") Long id, Pageable pageable);
}

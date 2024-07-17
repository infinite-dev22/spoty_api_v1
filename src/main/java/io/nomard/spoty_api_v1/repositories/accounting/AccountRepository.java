package io.nomard.spoty_api_v1.repositories.accounting;

import io.nomard.spoty_api_v1.entities.Tenant;
import io.nomard.spoty_api_v1.entities.accounting.Account;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface AccountRepository extends ReactiveSortingRepository<Account, Long>, ReactiveCrudRepository<Account, Long> {
    @Query("SELECT p FROM Account p WHERE p.tenant.id = :id AND CONCAT(TRIM(LOWER(p.accountName)), TRIM(LOWER(p.accountNumber))) LIKE %:search%")
    Flux<Account> search(@Param("id") Long id, @Param("search") String search);

    @Query("SELECT p FROM Account p WHERE p.tenant.id = :id")
    Flux<Account> findAllByTenantId(@Param("id") Long id, Pageable pageable);

    Mono<Account> findByTenantAndAccountNameContainingIgnoreCase(Tenant tenant, String accountName);
}

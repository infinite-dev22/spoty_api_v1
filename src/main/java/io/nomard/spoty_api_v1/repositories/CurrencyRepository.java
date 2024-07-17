package io.nomard.spoty_api_v1.repositories;

import io.nomard.spoty_api_v1.entities.Currency;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface CurrencyRepository extends ReactiveSortingRepository<Currency, Long>, ReactiveCrudRepository<Currency, Long> {
    @Query("SELECT c FROM Currency c WHERE c.tenant.id = :id AND CONCAT(LOWER(c.name), LOWER(c.code),LOWER(c.symbol)) LIKE %:search%")
    Flux<Currency> search(@Param("id") Long id, @Param("search") String search);

    @Query("SELECT c FROM Currency c WHERE c.tenant.id = :id")
    Flux<Currency> findAllByTenantId(@Param("id") Long id, Pageable pageable);
}

package io.nomard.spoty_api_v1.repositories.accounting;

import io.nomard.spoty_api_v1.entities.accounting.Expense;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ExpenseRepository extends ReactiveSortingRepository<Expense, Long>, ReactiveCrudRepository<Expense, Long> {
    @Query("SELECT e FROM Expense e WHERE e.tenant.id = :id AND TRIM(LOWER(e.name)) LIKE %:search%")
    Flux<Expense> search(@Param("id") Long id, @Param("search") String search);

    @Query("SELECT p FROM Expense p WHERE p.tenant.id = :id")
    Flux<Expense> findAllByTenantId(@Param("id") Long id, Pageable pageable);
}

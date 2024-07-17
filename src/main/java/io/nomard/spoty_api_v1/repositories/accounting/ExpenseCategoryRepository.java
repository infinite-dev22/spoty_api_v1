package io.nomard.spoty_api_v1.repositories.accounting;

import io.nomard.spoty_api_v1.entities.accounting.ExpenseCategory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ExpenseCategoryRepository extends ReactiveSortingRepository<ExpenseCategory, Long>, ReactiveCrudRepository<ExpenseCategory, Long> {
    @Query("SELECT e FROM ExpenseCategory e WHERE e.tenant.id = :id AND TRIM(LOWER(e.name)) LIKE %:name%")
    Flux<ExpenseCategory> search(
            @Param("id") Long id,
            @Param("name") String name);

    @Query("SELECT e FROM ExpenseCategory e WHERE e.tenant.id = :id")
    Flux<ExpenseCategory> findAllByTenantId(@Param("id") Long id, Pageable pageable);
}

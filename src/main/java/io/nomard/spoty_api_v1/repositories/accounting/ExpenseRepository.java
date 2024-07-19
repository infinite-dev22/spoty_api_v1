package io.nomard.spoty_api_v1.repositories.accounting;

import io.nomard.spoty_api_v1.entities.accounting.Expense;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseRepository extends PagingAndSortingRepository<Expense, Long>, JpaRepository<Expense, Long> {
    @Query("select e from Expense e where trim(lower(e.name)) like %:search")
    List<Expense> searchAllByNameContainingIgnoreCase(String search);

    @Query("select p from Expense p where p.tenant.id = :id")
    Page<Expense> findAllByTenantId(@Param("id") Long id, Pageable pageable);
}

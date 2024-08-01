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
    @Query("SELECT e FROM Expense e WHERE e.tenant.id = :tenantId " +
            "AND CONCAT(" +
            "TRIM(LOWER(e.name))," +
            "TRIM(LOWER(e.note))," +
            "TRIM(LOWER(e.ref))) LIKE %:search%")
    List<Expense> searchAll(@Param("tenantId") Long tenantId, @Param("search") String search);

    @Query("select p from Expense p where p.tenant.id = :id")
    Page<Expense> findAllByTenantId(@Param("id") Long id, Pageable pageable);
}

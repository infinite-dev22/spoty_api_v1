package io.nomard.spoty_api_v1.repositories;

import io.nomard.spoty_api_v1.entities.ExpenseCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseCategoryRepository extends PagingAndSortingRepository<ExpenseCategory, Long>, JpaRepository<ExpenseCategory, Long> {
    List<ExpenseCategory> searchAllByNameContainingIgnoreCase(String search);

    @Query("select p from ExpenseCategory p where p.tenant.id = :id")
    Page<ExpenseCategory> findAllByTenantId(@Param("id") Long id, Pageable pageable);
}

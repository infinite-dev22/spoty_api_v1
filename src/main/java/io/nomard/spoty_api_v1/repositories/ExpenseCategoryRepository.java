package io.nomard.spoty_api_v1.repositories;

import io.nomard.spoty_api_v1.entities.ExpenseCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseCategoryRepository extends PagingAndSortingRepository<ExpenseCategory, Long>, JpaRepository<ExpenseCategory, Long> {
    List<ExpenseCategory> searchAllByNameContainingIgnoreCase(String search);
}

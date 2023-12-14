package io.nomard.spoty_api_v1.repositories;

import io.nomard.spoty_api_v1.entities.ExpenseCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseCategoryRepository extends JpaRepository<ExpenseCategory, Long> {
    @Query("select ec from ExpenseCategory ec where trim(lower(ec.name)) like %:search%")
    List<ExpenseCategory> searchAll(String search);
}

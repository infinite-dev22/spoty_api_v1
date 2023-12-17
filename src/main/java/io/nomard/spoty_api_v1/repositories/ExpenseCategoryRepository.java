package io.nomard.spoty_api_v1.repositories;

import io.nomard.spoty_api_v1.entities.ExpenseCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseCategoryRepository extends JpaRepository<ExpenseCategory, Long> {
    List<ExpenseCategory> searchAllByNameContainingIgnoreCase(String search);
}

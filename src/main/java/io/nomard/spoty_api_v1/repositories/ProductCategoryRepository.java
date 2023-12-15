package io.nomard.spoty_api_v1.repositories;

import io.nomard.spoty_api_v1.entities.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {
    List<ProductCategory> searchAllByNameContainingIgnoreCaseOrCodeContainingIgnoreCase(String name, String code);
}

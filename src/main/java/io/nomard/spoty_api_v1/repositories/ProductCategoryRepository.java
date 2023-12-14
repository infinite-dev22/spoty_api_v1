package io.nomard.spoty_api_v1.repositories;

import io.nomard.spoty_api_v1.entities.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {
    @Query("select pc from ProductCategory pc where concat(" +
            "trim(lower(pc.name))," +
            "trim(lower(pc.code))) like %:search%")
    List<ProductCategory> searchAll(String search);
}

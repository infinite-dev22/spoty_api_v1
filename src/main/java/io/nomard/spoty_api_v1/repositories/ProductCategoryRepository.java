package io.nomard.spoty_api_v1.repositories;

import io.nomard.spoty_api_v1.entities.ProductCategory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ProductCategoryRepository extends ReactiveSortingRepository<ProductCategory, Long>, ReactiveCrudRepository<ProductCategory, Long> {
    @Query("SELECT pc " +
            "FROM ProductCategory pc " +
            "WHERE pc.tenant.id = :id " +
            "AND CONCAT(LOWER(pc.name), LOWER(pc.description)) " +
            "LIKE %:search%")
    Flux<ProductCategory> search(@Param("id") Long id, @Param("search") String search);

    @Query("SELECT pc FROM ProductCategory pc WHERE pc.tenant.id = :id")
    Flux<ProductCategory> findAllByTenantId(@Param("id") Long id, Pageable pageable);
}

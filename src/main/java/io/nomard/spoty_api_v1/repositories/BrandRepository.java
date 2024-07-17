package io.nomard.spoty_api_v1.repositories;

import io.nomard.spoty_api_v1.entities.Brand;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface BrandRepository extends ReactiveSortingRepository<Brand, Long>, ReactiveCrudRepository<Brand, Long> {
    @Query("SELECT b FROM Brand b WHERE b.tenant.id = :id AND CONCAT(LOWER(b.name), LOWER(b.description)) LIKE %:search%")
    Flux<Brand> search(@Param("id") Long id, @Param("search") String search);

    @Query("SELECT p FROM Brand p WHERE p.tenant.id = :id")
    Flux<Brand> findAllByTenantId(@Param("id") Long id, Pageable pageable);
}

package io.nomard.spoty_api_v1.repositories;

import io.nomard.spoty_api_v1.entities.Branch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.List;

@Repository
public interface BranchRepository extends ReactiveSortingRepository<Branch, Long>, ReactiveCrudRepository<Branch, Long> {
    @Query("SELECT b " +
            "FROM Branch b " +
            "WHERE b.tenant.id = :id " +
            "AND CONCAT(LOWER(b.email), LOWER(b.name), LOWER(b.city),LOWER(b.town),LOWER(b.phone)) " +
            "LIKE %:search%")
    Flux<Branch> search(@Param("id") Long id, @Param("search") String search);

    @Query("SELECT p " +
            "FROM Branch p " +
            "WHERE p.tenant.id = :id")
    Flux<Branch> findAllByTenantId(@Param("id") Long id, Pageable pageable);
}

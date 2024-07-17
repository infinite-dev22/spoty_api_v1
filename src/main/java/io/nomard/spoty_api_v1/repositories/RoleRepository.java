package io.nomard.spoty_api_v1.repositories;

import io.nomard.spoty_api_v1.entities.Role;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface RoleRepository extends ReactiveSortingRepository<Role, Long>, ReactiveCrudRepository<Role, Long> {
    @Query("SELECT b " +
            "FROM Role b " +
            "WHERE LOWER(b.name) " +
            "LIKE %:search%")
    Flux<Role> search(@Param("search") String search);

    @Query("SELECT p FROM Role p WHERE p.tenant.id = :id")
    Flux<Role> findAllByTenantId(@Param("id") Long id, Pageable pageable);
}

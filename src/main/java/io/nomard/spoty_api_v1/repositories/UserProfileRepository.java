package io.nomard.spoty_api_v1.repositories;

import io.nomard.spoty_api_v1.entities.UserProfile;
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
public interface UserProfileRepository extends ReactiveSortingRepository<UserProfile, Long>, ReactiveCrudRepository<UserProfile, Long> {
    @Query("SELECT p FROM UserProfile p WHERE p.tenant.id = :id")
    Flux<UserProfile> findAllByTenantId(@Param("id") Long id, Pageable pageable);
}

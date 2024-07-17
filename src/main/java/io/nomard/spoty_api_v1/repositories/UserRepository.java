package io.nomard.spoty_api_v1.repositories;

import io.nomard.spoty_api_v1.entities.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
public interface UserRepository extends ReactiveSortingRepository<User, Long>, ReactiveCrudRepository<User, Long> {
    @Query("SELECT u " +
            "FROM User u " +
            "WHERE u.tenant.id = :id " +
            "AND CONCAT(TRIM(LOWER(u.email)), " +
            "TRIM(LOWER(u.userProfile.firstName)), " +
            "TRIM(LOWER(u.userProfile.lastName)), " +
            "TRIM(LOWER(u.userProfile.otherName)), " +
            "TRIM(LOWER(u.userProfile.phone)), " +
            "TRIM(LOWER(u.role.name)), " +
            "TRIM(LOWER(u.role.label))) " +
            "LIKE %:search%")
    Flux<List<User>> search(@Param("id") Long id, @Param("search") String search);

    @Query("SELECT u " +
            "FROM User u " +
            "WHERE u.tenant.id = :id " +
            "AND CONCAT(TRIM(LOWER(u.email))) " +
            "LIKE %:search%")
    Flux<List<User>> searchAllByEmail(@Param("id") Long id, @Param("search") String search);

    Mono<User> findUserByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.tenant.id = :id")
    Flux<User> findAllByTenantId(@Param("id") Long id, Pageable pageable);
}

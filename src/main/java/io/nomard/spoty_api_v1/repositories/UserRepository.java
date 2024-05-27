package io.nomard.spoty_api_v1.repositories;

import io.nomard.spoty_api_v1.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends PagingAndSortingRepository<User, Long>, JpaRepository<User, Long> {
    List<User> searchAllByEmailContainingIgnoreCase(String email);

    User findUserByEmail(String email);

    @Query("select p from User p where p.tenant.id = :id")
    Page<User> findAllByTenantId(@Param("id") Long id, Pageable pageable);
}

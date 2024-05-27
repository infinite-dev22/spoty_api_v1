package io.nomard.spoty_api_v1.repositories;

import io.nomard.spoty_api_v1.entities.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends PagingAndSortingRepository<Role, Long>, JpaRepository<Role, Long> {
    List<Role> searchAllByNameContainingIgnoreCase(String name);

    @Query("select p from Role p where p.tenant.id = :id")
    Page<Role> findAllByTenantId(@Param("id") Long id, Pageable pageable);
}

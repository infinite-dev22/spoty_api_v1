package io.nomard.spoty_api_v1.repositories;

import io.nomard.spoty_api_v1.entities.Branch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface BranchRepository extends PagingAndSortingRepository<Branch, Long>, JpaRepository<Branch, Long> {
    @Query("SELECT b FROM Branch b WHERE b.tenant.id = :tenantId " +
            "AND CONCAT(" +
            "TRIM(LOWER(b.name))," +
            "TRIM(LOWER(b.email))," +
            "TRIM(LOWER(b.city))," +
            "TRIM(LOWER(b.town))," +
            "TRIM(LOWER(b.phone))) LIKE %:search%")
    ArrayList<Branch> searchAll(@Param("tenantId") Long tenantId, @Param("search") String search);

    @Query("select p from Branch p where p.tenant.id = :id")
    Page<Branch> findAllByTenantId(@Param("id") Long id, Pageable pageable);
}

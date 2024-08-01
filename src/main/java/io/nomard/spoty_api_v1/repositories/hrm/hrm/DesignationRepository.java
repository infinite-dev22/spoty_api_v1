package io.nomard.spoty_api_v1.repositories.hrm.hrm;

import io.nomard.spoty_api_v1.entities.hrm.hrm.Designation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface DesignationRepository extends PagingAndSortingRepository<Designation, Long>, JpaRepository<Designation, Long> {
    @Query("SELECT d FROM Designation d WHERE d.tenant.id = :tenantId " +
            "AND TRIM(LOWER(d.name)) LIKE %:search%")
    ArrayList<Designation> searchAll(@Param("tenantId") Long tenantId, @Param("search") String search);

    @Query("select d from Designation d where d.tenant.id = :id")
    Page<Designation> findAllByTenantId(@Param("id") Long id, Pageable pageable);
}

package io.nomard.spoty_api_v1.repositories.hrm.hrm;

import io.nomard.spoty_api_v1.entities.hrm.hrm.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface DepartmentRepository extends PagingAndSortingRepository<Department, Long>, JpaRepository<Department, Long> {
    @Query("SELECT d FROM Department d WHERE d.tenant.id = :tenantId " +
            "AND TRIM(LOWER(d.name)) LIKE %:search%")
    ArrayList<Department> searchAll(@Param("tenantId") Long tenantId, @Param("search") String search);

    @Query("select d from Department d where d.tenant.id = :id")
    Page<Department> findAllByTenantId(@Param("id") Long id, Pageable pageable);
}

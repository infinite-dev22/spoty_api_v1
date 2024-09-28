package io.nomard.spoty_api_v1.repositories;

import io.nomard.spoty_api_v1.entities.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface EmployeeRepository extends PagingAndSortingRepository<Employee, Long>, JpaRepository<Employee, Long> {
    @Query("SELECT e FROM Employee e WHERE e.tenant.id = :tenantId " +
            "AND TRIM(LOWER(e.email)) LIKE %:search%")
    ArrayList<Employee> searchEmployee(@Param("tenantId") Long tenantId, @Param("search") String search);

    @Query("SELECT e FROM Employee e WHERE e.tenant.id = :tenantId")
    Page<Employee> findByEmail(@Param("tenantId") Long tenantId, Pageable pageable);

    @Query("SELECT e FROM Employee e WHERE  e.email = :email")
    Employee findByEmail(String email);
}

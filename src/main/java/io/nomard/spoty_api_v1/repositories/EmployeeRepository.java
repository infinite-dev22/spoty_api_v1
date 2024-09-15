package io.nomard.spoty_api_v1.repositories;

import io.nomard.spoty_api_v1.entities.Customer;
import io.nomard.spoty_api_v1.entities.Employee;
import io.nomard.spoty_api_v1.entities.Supplier;
import io.nomard.spoty_api_v1.entities.User;
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
    @Query("SELECT u FROM Employee u WHERE u.tenant.id = :tenantId " +
            "AND TRIM(LOWER(u.email)) LIKE %:search%")
    ArrayList<Employee> searchEmployee(@Param("tenantId") Long tenantId, @Param("search") String search);

    @Query("select p from Employee p where p.tenant.id = :tenantId")
    Page<Employee> findByEmail(@Param("tenantId") Long tenantId, Pageable pageable);

    @Query("select p from Employee p where p.tenant.id = :tenantId AND p.email = :email")
    Employee findByEmail(@Param("tenantId") Long tenantId, String email);

    @Query("select p from Employee p where  p.email = :email")
    Employee findByEmail(String email);
}

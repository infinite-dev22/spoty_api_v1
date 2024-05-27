package io.nomard.spoty_api_v1.repositories.hrm.pay_roll;

import io.nomard.spoty_api_v1.entities.hrm.pay_roll.Salary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SalaryRepository extends PagingAndSortingRepository<Salary, Long>, JpaRepository<Salary, Long> {
    @Query("select p from Salary p where p.tenant.id = :id")
    Page<Salary> findAllByTenantId(@Param("id") Long id, Pageable pageable);
}

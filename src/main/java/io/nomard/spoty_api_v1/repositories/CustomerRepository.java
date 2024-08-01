package io.nomard.spoty_api_v1.repositories;

import io.nomard.spoty_api_v1.entities.Customer;
import io.nomard.spoty_api_v1.entities.accounting.Account;
import io.nomard.spoty_api_v1.models.DashboardKPIModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public interface CustomerRepository extends PagingAndSortingRepository<Customer, Long>, JpaRepository<Customer, Long> {
    @Query("SELECT c FROM Customer c WHERE c.tenant.id = :tenantId " +
            "AND CONCAT(" +
            "TRIM(LOWER(c.name))," +
            "TRIM(LOWER(c.code))," +
            "TRIM(LOWER(c.city))," +
            "TRIM(LOWER(c.phone))," +
            "TRIM(LOWER(c.address))," +
            "TRIM(LOWER(c.country))) LIKE %:search%")
    ArrayList<Customer> searchAll(@Param("tenantId") Long tenantId, @Param("search") String search);

    @Query("select p from Customer p where p.tenant.id = :id")
    Page<Customer> findAllByTenantId(@Param("id") Long id, Pageable pageable);

    @Query("SELECT new io.nomard.spoty_api_v1.models.DashboardKPIModel('Total Customers', COUNT(s)) " +
            "FROM Customer s " +
            "WHERE s.tenant.id = :id")
    DashboardKPIModel countCustomers(@Param("id") Long tenantId);
}

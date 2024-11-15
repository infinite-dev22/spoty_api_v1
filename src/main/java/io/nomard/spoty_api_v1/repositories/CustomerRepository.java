package io.nomard.spoty_api_v1.repositories;

import io.nomard.spoty_api_v1.entities.Customer;
import io.nomard.spoty_api_v1.models.DashboardKPIModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface CustomerRepository extends PagingAndSortingRepository<Customer, Long>, JpaRepository<Customer, Long> {
    @Query("SELECT c FROM Customer c WHERE c.tenant.id = :tenantId " +
            "AND CONCAT(" +
            "TRIM(LOWER(c.firstName))," +
            "TRIM(LOWER(c.otherName))," +
            "TRIM(LOWER(c.lastName))," +
            "TRIM(LOWER(c.city))," +
            "TRIM(LOWER(c.phone))," +
            "TRIM(LOWER(c.address))," +
            "TRIM(LOWER(c.country))) LIKE %:search%")
    ArrayList<Customer> search(@Param("tenantId") Long tenantId, @Param("search") String search);

    @Query("select c from Customer c where c.tenant.id = :id OR c.tenant IS NULL")
    Page<Customer> findByTenantId(@Param("id") Long id, Pageable pageable);

    @Query("SELECT COUNT(c) " +
            "FROM Customer c " +
            "WHERE c.tenant.id = :id OR c.tenant IS NULL")
    Number countCustomers(@Param("id") Long tenantId);
}

package io.nomard.spoty_api_v1.repositories;

import io.nomard.spoty_api_v1.entities.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface SupplierRepository extends PagingAndSortingRepository<Supplier, Long>, JpaRepository<Supplier, Long> {
    @Query("SELECT c FROM Customer c WHERE c.tenant.id = :tenantId " +
            "AND CONCAT(" +
            "TRIM(LOWER(c.firstName))," +
            "TRIM(LOWER(c.otherName))," +
            "TRIM(LOWER(c.lastName))," +
            "TRIM(LOWER(c.city))," +
            "TRIM(LOWER(c.phone))," +
            "TRIM(LOWER(c.address))," +
            "TRIM(LOWER(c.country))) LIKE %:search%")
    ArrayList<Supplier> search(@Param("tenantId") Long tenantId, @Param("search") String search);

    @Query("select p from Supplier p where p.tenant.id = :id OR p.tenant IS NULL")
    Page<Supplier> findByTenantId(@Param("id") Long id, Pageable pageable);

    @Query("SELECT COUNT(s) " +
            "FROM Supplier s " +
            "WHERE s.tenant.id = :id OR s.tenant IS NULL")
    Number countSuppliers(@Param("id") Long id);
}

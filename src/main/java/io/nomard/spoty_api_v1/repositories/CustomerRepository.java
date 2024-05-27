package io.nomard.spoty_api_v1.repositories;

import io.nomard.spoty_api_v1.entities.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends PagingAndSortingRepository<Customer, Long>, JpaRepository<Customer, Long> {
    List<Customer> searchAllByNameContainingIgnoreCaseOrCodeContainingIgnoreCaseOrCityContainingIgnoreCaseOrPhoneContainingIgnoreCaseOrAddressContainingIgnoreCaseOrCountryContainingIgnoreCase(String name, String code, String city, String phone, String address, String country);

    @Query("select p from Customer p where p.tenant.id = :id")
    Page<Customer> findAllByTenantId(@Param("id") Long id, Pageable pageable);
}

package io.nomard.spoty_api_v1.repositories;

import io.nomard.spoty_api_v1.entities.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupplierRepository extends PagingAndSortingRepository<Supplier, Long>, JpaRepository<Supplier, Long> {
    @Query("select s from Supplier s where concat(" +
            "trim(lower(s.name))," +
            "trim(lower(s.code))," +
            "trim(lower(s.city))," +
            "trim(lower(s.phone))," +
            "trim(lower(s.email))," +
            "trim(lower(s.address))," +
            "trim(lower(s.country))) like %:search%")
    List<Supplier> searchAll(String search);

    List<Supplier> searchAllByNameContainingIgnoreCaseOrCodeContainingIgnoreCaseOrAddressContainingIgnoreCaseOrCityContainingIgnoreCaseOrCountryContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingIgnoreCase(String name, String code, String address, String city, String country, String email, String phone);

    Supplier findSupplierByEmail(String email);
}

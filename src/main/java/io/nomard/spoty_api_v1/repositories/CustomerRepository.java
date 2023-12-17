package io.nomard.spoty_api_v1.repositories;

import io.nomard.spoty_api_v1.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    List<Customer> searchAllByNameContainingIgnoreCaseOrCodeContainingIgnoreCaseOrCityContainingIgnoreCaseOrPhoneContainingIgnoreCaseOrAddressContainingIgnoreCaseOrCountryContainingIgnoreCase(String name, String code, String city, String phone, String address, String country);
}

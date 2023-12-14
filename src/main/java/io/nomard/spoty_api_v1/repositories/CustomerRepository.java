package io.nomard.spoty_api_v1.repositories;

import io.nomard.spoty_api_v1.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    @Query("select c from Customer c where concat(" +
            "trim(lower(c.name))," +
            "trim(lower(c.code))," +
            "trim(lower(c.city))," +
            "trim(lower(c.phone))," +
            "trim(lower(c.email))," +
            "trim(lower(c.address))," +
            "trim(lower(c.country))) like %:search%")
    List<Customer> searchAll(String search);
}

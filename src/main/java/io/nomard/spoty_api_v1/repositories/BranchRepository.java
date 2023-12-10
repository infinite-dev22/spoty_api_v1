package io.nomard.spoty_api_v1.repositories;

import io.nomard.spoty_api_v1.entities.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BranchRepository extends JpaRepository<Branch, Long> {
    @Query("select b from Branch b where concat(" +
            "trim(lower(b.email))," +
            "trim(lower(b.name))," +
            "trim(lower(b.city))," +
            "trim(lower(b.town))," +
            "trim(lower(b.phone))," +
            "trim(lower(b.zipCode))) like %:search")
    List<Branch> searchAll(String search);
}

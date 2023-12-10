package io.nomard.spoty_api_v1.repositories;

import io.nomard.spoty_api_v1.entities.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {
    @Query("select b from Brand b where concat(" +
            "trim(lower(b.name))," +
            "trim(lower(b.description))) like %:search")
    List<Brand> searchAll(String search);
}

package io.nomard.spoty_api_v1.repositories;

import io.nomard.spoty_api_v1.entities.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BrandRepository extends PagingAndSortingRepository<Brand, Long>, JpaRepository<Brand, Long> {
    List<Brand> searchAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String description);
}

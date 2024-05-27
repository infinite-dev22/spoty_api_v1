package io.nomard.spoty_api_v1.repositories;

import io.nomard.spoty_api_v1.entities.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BrandRepository extends PagingAndSortingRepository<Brand, Long>, JpaRepository<Brand, Long> {
    List<Brand> searchAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String description);

    @Query("select p from Brand p where p.tenant.id = :id")
    Page<Brand> findAllByTenantId(@Param("id") Long id, Pageable pageable);
}

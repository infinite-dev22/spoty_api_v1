package io.nomard.spoty_api_v1.repositories.hrm.hrm;

import io.nomard.spoty_api_v1.entities.hrm.hrm.Designation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface DesignationRepository extends PagingAndSortingRepository<Designation, Long>, JpaRepository<Designation, Long> {
    ArrayList<Designation> searchAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String designation);

    @Query("select p from Designation p where p.tenant.id = :id")
    Page<Designation> findAllByTenantId(@Param("id") Long id, Pageable pageable);
}

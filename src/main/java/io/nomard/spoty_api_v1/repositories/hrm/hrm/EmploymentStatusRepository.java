package io.nomard.spoty_api_v1.repositories.hrm.hrm;

import io.nomard.spoty_api_v1.entities.hrm.hrm.EmploymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmploymentStatusRepository extends PagingAndSortingRepository<EmploymentStatus, Long>, JpaRepository<EmploymentStatus, Long> {
    List<EmploymentStatus> searchAllByNameContainingIgnoreCaseOrColorContainingIgnoreCaseOrDescriptionContainsIgnoreCase(String name, String color, String description);

    @Query("select p from EmploymentStatus p where p.tenant.id = :id")
    Page<EmploymentStatus> findAllByTenantId(@Param("id") Long id, Pageable pageable);
}

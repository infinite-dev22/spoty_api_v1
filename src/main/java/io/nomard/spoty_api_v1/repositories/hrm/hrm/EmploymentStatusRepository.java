package io.nomard.spoty_api_v1.repositories.hrm.hrm;

import io.nomard.spoty_api_v1.entities.hrm.hrm.EmploymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmploymentStatusRepository extends PagingAndSortingRepository<EmploymentStatus, Long>, JpaRepository<EmploymentStatus, Long> {
}

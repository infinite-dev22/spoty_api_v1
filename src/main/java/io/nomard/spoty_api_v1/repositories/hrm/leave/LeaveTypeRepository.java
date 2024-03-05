package io.nomard.spoty_api_v1.repositories.hrm.leave;

import io.nomard.spoty_api_v1.entities.hrm.leave.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LeaveTypeRepository extends PagingAndSortingRepository<LeaveType, Long>, JpaRepository<LeaveType, Long> {
}

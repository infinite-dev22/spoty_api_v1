package io.nomard.spoty_api_v1.repositories.hrm.pay_roll;

import io.nomard.spoty_api_v1.entities.hrm.pay_roll.PaySlip;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PaySlipRepository extends PagingAndSortingRepository<PaySlip, Long>, JpaRepository<PaySlip, Long> {
    @Query("select p from PaySlip p where p.tenant.id = :id")
    Page<PaySlip> findAllByTenantId(@Param("id") Long id, Pageable pageable);
}

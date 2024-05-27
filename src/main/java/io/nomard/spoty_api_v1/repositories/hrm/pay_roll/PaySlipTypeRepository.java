package io.nomard.spoty_api_v1.repositories.hrm.pay_roll;

import io.nomard.spoty_api_v1.entities.hrm.pay_roll.PaySlipType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PaySlipTypeRepository extends PagingAndSortingRepository<PaySlipType, Long>, JpaRepository<PaySlipType, Long> {
    @Query("select p from PaySlipType p where p.tenant.id = :id")
    Page<PaySlipType> findAllByTenantId(@Param("id") Long id, Pageable pageable);
}

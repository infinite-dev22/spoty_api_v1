package io.nomard.spoty_api_v1.repositories.hrm.pay_roll;

import io.nomard.spoty_api_v1.entities.hrm.pay_roll.PaySlipType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaySlipTypeRepository extends PagingAndSortingRepository<PaySlipType, Long>, JpaRepository<PaySlipType, Long> {
}

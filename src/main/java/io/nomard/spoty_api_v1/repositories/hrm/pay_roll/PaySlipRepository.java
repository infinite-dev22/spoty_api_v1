package io.nomard.spoty_api_v1.repositories.hrm.pay_roll;

import io.nomard.spoty_api_v1.entities.hrm.pay_roll.PaySlip;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface PaySlipRepository extends ReactiveSortingRepository<PaySlip, Long>, ReactiveCrudRepository<PaySlip, Long> {
    @Query("SELECT p FROM PaySlip p WHERE p.tenant.id = :id")
    Flux<PaySlip> findAllByTenantId(@Param("id") Long id, Pageable pageable);
}

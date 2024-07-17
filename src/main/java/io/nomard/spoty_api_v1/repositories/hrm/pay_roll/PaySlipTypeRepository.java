package io.nomard.spoty_api_v1.repositories.hrm.pay_roll;

import io.nomard.spoty_api_v1.entities.hrm.pay_roll.PaySlipType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface PaySlipTypeRepository extends ReactiveSortingRepository<PaySlipType, Long>, ReactiveCrudRepository<PaySlipType, Long> {
    @Query("SELECT p FROM PaySlipType p WHERE p.tenant.id = :id")
    Flux<PaySlipType> findAllByTenantId(@Param("id") Long id, Pageable pageable);
}

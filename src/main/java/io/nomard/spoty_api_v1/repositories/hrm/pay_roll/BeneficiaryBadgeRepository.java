package io.nomard.spoty_api_v1.repositories.hrm.pay_roll;

import io.nomard.spoty_api_v1.entities.hrm.pay_roll.BeneficiaryBadge;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface BeneficiaryBadgeRepository extends ReactiveSortingRepository<BeneficiaryBadge, Long>, ReactiveCrudRepository<BeneficiaryBadge, Long> {
    @Query("SELECT bb " +
            "FROM BeneficiaryBadge bb " +
            "WHERE bb.tenant.id = :id " +
            "AND CONCAT(LOWER(bb.name), LOWER(bb.color)) " +
            "LIKE %:search%")
    Flux<BeneficiaryBadge> search(@Param("id") Long id, @Param("search") String search);

    @Query("SELECT p FROM BeneficiaryBadge p WHERE p.tenant.id = :id")
    Flux<BeneficiaryBadge> findAllByTenantId(@Param("id") Long id, Pageable pageable);
}

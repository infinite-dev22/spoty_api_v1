package io.nomard.spoty_api_v1.repositories.hrm.pay_roll;

import io.nomard.spoty_api_v1.entities.hrm.pay_roll.BeneficiaryType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface BeneficiaryTypeRepository extends ReactiveSortingRepository<BeneficiaryType, Long>, ReactiveCrudRepository<BeneficiaryType, Long> {
    @Query("SELECT bt " +
            "FROM BeneficiaryType bt " +
            "WHERE bt.tenant.id = :id " +
            "AND CONCAT(LOWER(bt.name), LOWER(bt.color)) " +
            "LIKE %:search%")
    Flux<BeneficiaryType> search(@Param("id") Long id, @Param("search") String search);

    @Query("SELECT p FROM BeneficiaryType p WHERE p.tenant.id = :id")
    Flux<BeneficiaryType> findAllByTenantId(@Param("id") Long id, Pageable pageable);
}

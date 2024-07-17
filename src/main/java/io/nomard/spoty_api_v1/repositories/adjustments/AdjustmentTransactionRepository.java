package io.nomard.spoty_api_v1.repositories.adjustments;

import io.nomard.spoty_api_v1.entities.adjustments.AdjustmentTransaction;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface AdjustmentTransactionRepository extends ReactiveCrudRepository<AdjustmentTransaction, Long> {
    @Query("SELECT at FROM AdjustmentTransaction at WHERE at.adjustmentDetail.id = :id")
    Mono<AdjustmentTransaction> findByAdjustmentDetailId(Long id);
}

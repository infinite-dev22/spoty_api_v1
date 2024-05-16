package io.nomard.spoty_api_v1.repositories.adjustments;

import io.nomard.spoty_api_v1.entities.adjustments.AdjustmentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdjustmentTransactionRepository extends JpaRepository<AdjustmentTransaction, Long> {
    @Query("select at from AdjustmentTransaction at where at.adjustmentDetail.id = :id")
    Optional<AdjustmentTransaction> findByAdjustmentDetailId(Long id);
}

package io.nomard.spoty_api_v1.repositories.adjustments;

import io.nomard.spoty_api_v1.entities.adjustments.AdjustmentDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdjustmentDetailRepository extends PagingAndSortingRepository<AdjustmentDetail, Long>, JpaRepository<AdjustmentDetail, Long> {
}

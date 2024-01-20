package io.nomard.spoty_api_v1.repositories.adjustments;

import io.nomard.spoty_api_v1.entities.adjustments.AdjustmentMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdjustmentMasterRepository extends PagingAndSortingRepository<AdjustmentMaster, Long>, JpaRepository<AdjustmentMaster, Long> {
    List<AdjustmentMaster> searchAllByRefContainingIgnoreCase(String ref);
}

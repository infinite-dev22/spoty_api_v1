package io.nomard.spoty_api_v1.repositories.requisitions;

import io.nomard.spoty_api_v1.entities.requisitions.RequisitionDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequisitionDetailRepository extends JpaRepository<RequisitionDetail, Long> {
}

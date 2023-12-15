package io.nomard.spoty_api_v1.repositories.requisitions;

import io.nomard.spoty_api_v1.entities.requisitions.RequisitionMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequisitionMasterRepository extends JpaRepository<RequisitionMaster, Long> {
    List<RequisitionMaster> searchAllByRefContainingIgnoreCaseOrStatusContainingIgnoreCase(String ref, String status);
}

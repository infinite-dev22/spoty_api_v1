package io.nomard.spoty_api_v1.repositories.transfers;

import io.nomard.spoty_api_v1.entities.transfers.TransferMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransferMasterRepository extends JpaRepository<TransferMaster, Long> {
    List<TransferMaster> searchAllByRefContainingIgnoreCase(String ref);
}

package io.nomard.spoty_api_v1.repositories.transfers;

import io.nomard.spoty_api_v1.entities.transfers.TransferTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransferTransactionRepository extends JpaRepository<TransferTransaction, Long> {
    @Query("select tt from TransferTransaction tt where tt.transferDetail.id = :id")
    Optional<TransferTransaction> findByTransferDetailId(Long id);

    @Query("select p from TransferTransaction p where p.tenant.id = :id")
    Page<TransferTransaction> findAllByTenantId(@Param("id") Long id, Pageable pageable);
}

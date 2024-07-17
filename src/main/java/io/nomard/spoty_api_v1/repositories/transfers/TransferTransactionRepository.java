package io.nomard.spoty_api_v1.repositories.transfers;

import io.nomard.spoty_api_v1.entities.transfers.TransferTransaction;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface TransferTransactionRepository extends ReactiveCrudRepository<TransferTransaction, Long> {
    @Query("SELECT tt FROM TransferTransaction tt WHERE tt.transferDetail.id = :id")
    Mono<TransferTransaction> findByTransferDetailId(@Param("id") Long id);
}

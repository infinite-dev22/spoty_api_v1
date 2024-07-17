package io.nomard.spoty_api_v1.repositories.stock_ins;

import io.nomard.spoty_api_v1.entities.stock_ins.StockInTransaction;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface StockInTransactionRepository extends ReactiveCrudRepository<StockInTransaction, Long> {
    @Query("SELECT st FROM StockInTransaction st WHERE st.stockInDetail.id = :id")
    Mono<StockInTransaction> findByStockInDetailId(@Param("id") Long id);
}

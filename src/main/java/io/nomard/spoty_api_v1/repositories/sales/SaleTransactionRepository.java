package io.nomard.spoty_api_v1.repositories.sales;

import io.nomard.spoty_api_v1.entities.sales.SaleTransaction;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface SaleTransactionRepository extends ReactiveCrudRepository<SaleTransaction, Long> {
    @Query("select st from SaleTransaction st where st.saleDetail.id = :id")
    Mono<SaleTransaction> findSaleDetail(@Param("id") Long id);
}

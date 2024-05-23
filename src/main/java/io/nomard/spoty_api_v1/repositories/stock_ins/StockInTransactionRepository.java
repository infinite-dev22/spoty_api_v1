package io.nomard.spoty_api_v1.repositories.stock_ins;

import io.nomard.spoty_api_v1.entities.stock_ins.StockInTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StockInTransactionRepository extends JpaRepository<StockInTransaction, Long> {
    @Query("select st from StockInTransaction st where st.stockInDetail.id = :id")
    Optional<StockInTransaction> findByStockInDetailId(Long id);
}

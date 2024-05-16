package io.nomard.spoty_api_v1.repositories.sales;

import io.nomard.spoty_api_v1.entities.sales.SaleTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SaleTransactionRepository extends JpaRepository<SaleTransaction, Long> {
    @Query("select st from SaleTransaction st where st.saleDetail.id = :id")
    Optional<SaleTransaction> findBySaleDetailId(Long id);
}

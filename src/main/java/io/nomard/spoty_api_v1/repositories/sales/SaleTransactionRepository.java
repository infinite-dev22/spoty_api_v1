package io.nomard.spoty_api_v1.repositories.sales;

import io.nomard.spoty_api_v1.entities.sales.SaleTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SaleTransactionRepository extends JpaRepository<SaleTransaction, Long> {
    @Query("select st from SaleTransaction st where st.saleDetail.id = :id")
    Optional<SaleTransaction> findBySaleDetailId(Long id);

    @Query("select p from SaleTransaction p where p.tenant.id = :id")
    Page<SaleTransaction> findAllByTenantId(@Param("id") Long id, Pageable pageable);
}

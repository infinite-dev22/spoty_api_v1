package io.nomard.spoty_api_v1.repositories.returns.sale_returns;

import io.nomard.spoty_api_v1.entities.returns.sale_returns.SaleReturnDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SaleReturnDetailRepository extends JpaRepository<SaleReturnDetail, Long> {
}

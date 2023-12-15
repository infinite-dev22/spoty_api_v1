package io.nomard.spoty_api_v1.repositories.returns.sale_returns;

import io.nomard.spoty_api_v1.entities.returns.sale_returns.SaleReturnMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SaleReturnMasterRepository extends JpaRepository<SaleReturnMaster, Long> {
    List<SaleReturnMaster> searchAllByRefContainingIgnoreCase(String ref);
}

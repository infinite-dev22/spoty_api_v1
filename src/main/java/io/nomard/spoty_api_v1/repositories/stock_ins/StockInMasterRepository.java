package io.nomard.spoty_api_v1.repositories.stock_ins;

import io.nomard.spoty_api_v1.entities.stock_ins.StockInMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockInMasterRepository extends JpaRepository<StockInMaster, Long> {
    List<StockInMaster> searchAllByRefContainingIgnoreCase(String ref);
}

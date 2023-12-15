package io.nomard.spoty_api_v1.repositories.sales;

import io.nomard.spoty_api_v1.entities.sales.SaleMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SaleMasterRepository extends JpaRepository<SaleMaster, Long> {
    List<SaleMaster> searchAllByRefContainingIgnoreCase(String ref);
}

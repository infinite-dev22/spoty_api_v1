package io.nomard.spoty_api_v1.repositories.quotations;

import io.nomard.spoty_api_v1.entities.quotations.QuotationMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuotationMasterRepository extends JpaRepository<QuotationMaster, Long> {
    List<QuotationMaster> searchAllByRefContainingIgnoreCaseOrShippingContainingIgnoreCaseOrStatusContainingIgnoreCase(String ref, String shipping, String status);
}

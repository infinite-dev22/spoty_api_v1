package io.nomard.spoty_api_v1.repositories.returns.purchase_returns;

import io.nomard.spoty_api_v1.entities.returns.purchase_returns.PurchaseReturnMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseReturnMasterRepository extends JpaRepository<PurchaseReturnMaster, Long> {
    List<PurchaseReturnMaster> searchAllByRefContainingIgnoreCaseOrShippingContainingIgnoreCaseOrStatusContainingIgnoreCaseOrPaymentStatusContainsIgnoreCase(String ref, String shipping, String status, String paymentStatus);
}

package io.nomard.spoty_api_v1.repositories.purchases;

import io.nomard.spoty_api_v1.entities.purchases.PurchaseMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseMasterRepository extends JpaRepository<PurchaseMaster, Long> {
    List<PurchaseMaster> searchAllByRefContainingIgnoreCaseOrShippingContainingIgnoreCaseOrStatusContainingIgnoreCaseOrPaymentStatusContainsIgnoreCase(String ref, String shipping, String status, String paymentStatus);
}
